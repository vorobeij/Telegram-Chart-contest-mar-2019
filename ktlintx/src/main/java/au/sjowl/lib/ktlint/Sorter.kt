package au.sjowl.lib.ktlint

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.CompositeElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.children
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class Sorter {
    val methodsOrder = listOf(
        /* Presenter */
        "onSetArguments",
        "onSetState",
        "onUpdateUI",
        /* Fragment */
        "onCreate",
        "onCreateView",
        "onViewCreated",
        "onStart",
        "onResume",
        "onPause",
        "onStop",
        "onDetach",
        "onDestroyView",
        "onDestroy",
        /* View */
        "onAttachedToWindow",
        "onSizeChanged",
        "onMeasure",
        "onDraw",
        "dispatchTouchEvent",
        "onInterceptTouchEvent",
        "onTouchEvent",
        "callOnClick",
        "setEnabled"
    )

    val sortList = listOf(
        KtStubElementTypes.PROPERTY,
        KtStubElementTypes.FUNCTION,
        KtStubElementTypes.OBJECT_DECLARATION
    )

    private val modifiersOrder = listOf(
        "abstract",
        "override",
        "",
        "suspend",
        "protected",
        "private"
    )

    private val modifiersPropertiesOrder = listOf(
        "abstract",
        "override",
        "",
        "protected",
        "private"
    )

    val space = "\n\n    "

    fun removeSpaces(node: ASTNode) {
        node.getChildren(null).filter {
            it.elementType == KtTokens.WHITE_SPACE && it.treeNext.elementType == KtTokens.WHITE_SPACE
        }.forEach { node.removeChild(it) }
    }

    fun String.containsOneOf(items: List<String>): Boolean {
        items.forEach { if (this == it) return true }
        return false
    }

    fun getModifierIndex(node: ASTNode): Int {
        val modifiers = node.children()
            .filter { it.elementType == KtStubElementTypes.MODIFIER_LIST }
            .flatMap { it.text.split(Regex("[\n ]")).asSequence() }
            .filter { it.isNotEmpty() }
            .filter { it.containsOneOf(modifiersPropertiesOrder) }

        val modifier = modifiers.firstOrNull().orEmpty()
        return when (node.elementType) {
            KtStubElementTypes.PROPERTY -> modifiersPropertiesOrder.indexOf(modifier)
            else -> modifiersOrder.indexOf(modifier) + 100
        }
    }

    fun getMethodIndex(node: ASTNode): Int {

        var index = -1
        if (node.elementType == KtStubElementTypes.FUNCTION) {
            node.children().filter { it.elementType == KtTokens.IDENTIFIER }.firstOrNull()?.let {
                index = methodsOrder.indexOf(it.text)
            }
        }
        if (index == -1) index = 1000
        return index
    }

    fun removeWhitespacesBefore(parent: ASTNode, node: ASTNode) {
        var prev = node.treePrev
        while (prev != null && prev.elementType == KtTokens.WHITE_SPACE) {
            val temp = prev.treePrev
            parent.removeChild(prev)
            prev = temp
        }
        parent.addChild(PsiWhiteSpaceImpl("\n"), node)
    }

    fun printSimple(node: ASTNode) {
        println("type = ${node.elementType}, text = \'${node.text}\'")
    }

    fun printNode(node: ASTNode) {
        if (node is CompositeElement) {
            node.children().forEach {
                printNode(it)
            }
        } else {
            printSimple(node)
        }
    }
}