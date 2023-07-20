package com.mobiai.base.basecode.ui.activity.onboarding

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.mobiai.base.basecode.ui.activity.BaseActivity
import com.mobiai.base.basecode.ui.fragment.BaseFragment

abstract class BaseOnBoarding<V : ViewBinding>  : BaseActivity<V>() {
    var fragmentList: ArrayList<BaseFragment<*>> = arrayListOf() /** Fragment for content onboarding */

    protected var currentPosition = 0

    abstract fun nextScreen()

    open class OnPageChangeListenerSync(private val master: ViewPager, private val slave: ViewPager) :
        ViewPager.OnPageChangeListener {
        private var mScrollState = ViewPager.SCROLL_STATE_IDLE
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                return
            }
            slave.scrollTo(master.scrollX * slave.width / master.width, 0)
        }

        override fun onPageSelected(position: Int) {
        }
        override fun onPageScrollStateChanged(state: Int) {
            mScrollState = state
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                slave.setCurrentItem(master.currentItem, false)
            }
        }


    }

}