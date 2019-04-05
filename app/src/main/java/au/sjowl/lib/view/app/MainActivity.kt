package au.sjowl.lib.view.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import au.sjowl.apps.telegram.chart.R
import au.sjowl.lib.view.charts.telegram.fragment.BaseFragment
import au.sjowl.lib.view.charts.telegram.fragment.ChartsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainNavigationFragment, testFragment())
            commit()
        }
    }

    private fun testFragment(): BaseFragment {
        return ChartsFragment()
    }
}