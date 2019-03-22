package au.sjowl.lib.ktlint

import org.junit.Test

val inputCode = """
class BrowserFragment :
    BaseAppFragment<BrowserPresenter, BrowserView>(),
    BrowserView {

    private val menuPopupWrapper: MenuPopupWrapper by lazy { getMenuPopup() }

    private val adapter by lazy {
        SuggestionsAdapter { presenter.onSuggestionClicked(it) }
    }

    private val animationDuration = 70L

    private val transition = AutoTransition().apply { duration = animationDuration }

    override val layoutId: Int get() = R.layout.fragment_browser

    override val key: String get() = Screens.BROWSER

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        with(webView) {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return false
                }

                override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
                    presenter.onUrlChanged(url)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    bg { CookieManager.getInstance().flush() }
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress == 100) {
                        progressBar?.hide()
                    } else {
                        progressBar?.show()
                        progressBar?.progress = newProgress
                    }
                }

                override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                    presenter.onSaveIcon(view?.url, icon)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    presenter.onSaveTitle(view?.url, title)
                }
            }

            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true

            onScroll { _, scrollY, _, oldScrollY ->
                //                when {
//                    scrollY > oldScrollY -> {
// //                        appbar?.runSlideUpAnimation()
//                        readerFab?.hide()
//                    }
//                    scrollY <= oldScrollY -> {
// //                    appbar?.show()
// //                    appbar?.runSlideDownAnimation()
//                        readerFab?.show()
//                    }
//                }


                if (scrollY > oldScrollY) readerFab?.hide() else readerFab?.show()
            }
        }
    }

    /*********************       View      *********************/

    override suspend fun showSuggestions(suggestions: List<Suggestion>) = ui {
        suggestionsRecyclerView.show()
        adapter.items = suggestions
    }

    override suspend fun hideSuggestions() = ui {
        suggestionsRecyclerView.hide()
        adapter.items = emptyList()
    }

    override suspend fun loadUrl(url: String) = ui {
        suggestionsRecyclerView.hide()
        webView.loadUrl(url)
        linkEditText.hideKeyboard()
        webView.requestFocus()
    }

    override suspend fun setUrlText(url: String) = ui {
        linkEditText.tag = "tag"
        setLinkSpan(url)
        linkEditText.tag = null
    }

    override suspend fun onPageIsNotRssError() = ui {
        toast(R.string.rss_search_not_rss)
    }

    override suspend fun showMenu() = ui {
        menuPopupWrapper.show()
    }

    /*********************       Lifecycle      *********************/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setWebView()
        webView.requestFocus()

        readerFab.onClick { presenter.onOpenReader(webView.url) }

        suggestionsRecyclerView.adapter = adapter
        suggestionsRecyclerView.layoutManager = LinearLayoutManager(context)
        suggestionsRecyclerView.hide()

        linkEditText.tag = null
        compositeDisposable.add(linkEditText.textChanges()
            .map { it.toString().trim() }
            .skip(1)
            .filter { it.length > 1 }
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .subscribe({
                presenter.onQueryChanged(it)
            }) {}
        )
        linkEditText.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_GO -> presenter.onSuggestionClicked(Suggestion(linkEditText.text.toString()))
                else -> return@setOnEditorActionListener false
            }
            true
        }

        setupInput()

        menuImageView.onClick { presenter.onOpenMenu() }
        progressBar.hide()
    }

    override fun providePresenter(): BrowserPresenter = appInjector.getInstance()

    override fun onBack(): Boolean {
        return when {
            adapter.items.isNotEmpty() -> {
                adapter.items = emptyList()
                webView.requestFocus()
                true
            }
            else -> presenter.onBrowserBack()
        }
    }

    private fun setupInput() {
        cancelImageView.onClick { linkEditText.setText("") }

        cancelImageView.setVisibleOrInvisible(false)
        linkEditText.onFocusChange { v, hasFocus ->

            appbar.constrain(transition) { cs ->
                cancelImageView.setVisibleOrInvisible(hasFocus)
                if (hasFocus) {
                    linkEditText.selectAll()
                    cancelImageView.animateFadeAndScale(0f, 1f, animationDuration)
                    cs.connect(linkEditText.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                    cs.connect(cancelImageView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                } else {
                    cancelImageView.animateFadeAndScale(1f, 0f, animationDuration)
                    cs.connect(linkEditText.id, ConstraintSet.END, menuImageView.id, ConstraintSet.START)
                    cs.connect(cancelImageView.id, ConstraintSet.END, menuImageView.id, ConstraintSet.START)
                    setLinkSpan(presenter.url)
                }
            }

            if (hasFocus) {
                with(linkEditText) { setPadding(paddingLeft, paddingTop, context.dip(40), paddingBottom) }
            } else {
                linkEditText.setSelection(0, 0)
                with(linkEditText) { setPadding(paddingLeft, paddingTop, paddingLeft, paddingBottom) }
            }
        }
    }

    /*********************       Privates      *********************/

    private fun getMenuPopup(): MenuPopupWrapper {
        val popupWrapper = MenuPopupWrapper(R.layout.menu_browser, menuImageView)
        with(popupWrapper.layout) {
            menuAddRssTextView.onClick {
                presenter.onSaveRssFeed(webView.url)
                popupWrapper.dismiss()
            }
            menuForwardTextView.onClick {
                presenter.onGoForward()
                popupWrapper.dismiss()
            }
            menuOpenInReaderTextView.onClick {
                presenter.onOpenReader(webView.url)
                popupWrapper.dismiss()
            }
            menuHistoryTextView.onClick {
                presenter.onOpenHistory()
                popupWrapper.dismiss()
            }
            menuRefreshTextView.onClick {
                webView.reload()
                popupWrapper.dismiss()
            }
        }
        return popupWrapper
    }

    private fun setLinkSpan(url: String) {
        ui {
            val span = SpannableString(url)
            val host = URL(url).host
            val start = url.indexOf(host)
            val end = start + host.length
            span.setSpan(ColorSpan(context.getColorFromAttr(R.attr.colorInputTextSecondary)), 0, url.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            span.setSpan(ColorSpan(context.getColorFromAttr(R.attr.colorInputTextMain)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            linkEditText.setText(span, TextView.BufferType.SPANNABLE)
        }
    }

    companion object {

        fun getInstance(screenState: ScreenState) = BrowserFragment().apply {
            this.state = screenState.savedState
            this.arguments = screenState.arguments
        }
    }
}
""".trimIndent()

val input2 = """
package au.sjowl.apps.everlang.presentation.rssfeeds

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import au.sjowl.apps.everlang.R
import au.sjowl.apps.everlang.base.BaseFragment
import au.sjowl.apps.everlang.data.entities.RssFeedEntity
import au.sjowl.apps.everlang.presentation.components.SwipeCallback
import au.sjowl.apps.everlang.presentation.home.HomeView
import au.sjowl.apps.everlang.presentation.tabs.Screens
import au.sjowl.base.utils.hide
import au.sjowl.base.utils.show
import au.sjowl.libs.navigation.Screen
import au.sjowl.libs.navigation.ScreenState
import io.michaelrocks.lightsaber.getInstance
import kotlinx.android.synthetic.main.fragment_rss_feeds.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class RssFeedsFragment :
    BaseFragment<RssFeedsPresenter, RssFeedsView>(),
    RssFeedsView,
    Screen {

    override val layoutId: Int get() = R.layout.fragment_rss_feeds
    override val key: String = Screens.RSS_FEEDS
    override fun providePresenter() = appInjector.getInstance<RssFeedsPresenter>()

    private val adapterListener = object : RssFeedsHolderListener {
        override fun onClick(rssFeedEntity: RssFeedEntity) {
            presenter.onItemClicked(rssFeedEntity)
        }
    }

    private val adapter by lazy {
        RssFeedsAdapter(adapterListener)
    }

    private val itemTouchHelper = object : ItemTouchHelper(SwipeCallback { adapterPosition ->
        presenter.deleteFeed(adapter.items[adapterPosition])
    }) {}

    /*********************       Lifecycle      *********************/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        fab.onClick { presenter.onAddRss() }
    }

    /*********************       View      *********************/

    override suspend fun showEmpty() = ui {
        emptyView.show()
        recyclerView.hide()
    }

    override suspend fun setItems(items: List<RssFeedEntity>) = ui {
        emptyView.hide()
        recyclerView.show()
        adapter.items = items
    }

    override suspend fun showUndoFeedRemoved(rssFeedEntity: RssFeedEntity) = ui {
        showUndo(rssFeedEntity.title, getString(R.string.undo))
    }

    override suspend fun setTheme(theme: Int) = ui {
        (activity as HomeView).setupTheme(theme)
    }

    /*********************       Privates      *********************/

    companion object {
        fun getInstance(screenState: ScreenState) = RssFeedsFragment().apply {
            this.state = screenState.savedState
            this.arguments = screenState.arguments
        }
    }
}
""".trimIndent()

class SortRuleTest {
    @Test
    fun sortTest() {
//        println("result = \'${SortRule().format(inputCode)}\'")
//        SortRule().format(inputCode)
//        println("result = \'\n${SortRule().format(input2)}\n\'")
//        SortRule().format(input2)
    }
}