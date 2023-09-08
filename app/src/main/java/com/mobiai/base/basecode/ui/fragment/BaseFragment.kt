package com.mobiai.base.basecode.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.mobiai.app.ui.activity.MainActivity
import com.mobiai.app.ui.fragment.SettingFragment
import com.mobiai.base.basecode.ui.activity.BaseActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlin.reflect.KProperty1


abstract class BaseFragment<T : ViewBinding> : Fragment() {

    protected lateinit var binding: T
    private lateinit var callback: OnBackPressedCallback

    protected val compositeDisposable = CompositeDisposable()

    open fun handlerBackPressed(){}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handlerBackPressed()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    protected fun addDispose(disposable: Disposable?) {
        disposable?.let {
            compositeDisposable.add(disposable)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        callback.remove()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = getBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    abstract fun initView()

    abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): T

    fun addFragment(fragment: Fragment){
        (requireActivity() as BaseActivity<*>).addFragment(fragment)
    }

    fun replaceFullViewFragment(fragment: Fragment, addToBackStack: Boolean){
        (requireActivity()  as BaseActivity<*>).replaceFragment(fragment, android.R.id.content, addToBackStack)
    }
    fun replaceFragment(fragment: Fragment) {
        (requireActivity()  as BaseActivity<*>).replaceFragment(fragment)
    }
    open fun closeFragment(fragment: Fragment) {
        (requireActivity() as BaseActivity<*>).handleBackpress()
    }

    fun addAndRemoveCurrentFragment(currentFragment : Fragment, newFragment : Fragment, addToBackStack: Boolean = false) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.remove(currentFragment)
        transaction.add(android.R.id.content, newFragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun gotoSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
        isGoToSetting = true
    }

    fun<T> runBackground(action: () -> T, onSuccess: (result: T) -> Unit = {}, onError: (t: Throwable) -> Unit = {})  {
        (activity as BaseActivity<*>).runBackground(action, onSuccess, onError)
    }

    fun hasDrawOverlay() : Boolean{
        return Settings.canDrawOverlays(requireContext())
    }

    protected open fun hideKeyboard() {
        if (activity != null) {
            (activity as BaseActivity<*>?)?.hideKeyboard()
        }
    }

    protected open fun showKeyboard(view: View?) {
        (requireActivity() as BaseActivity<*>?)?.showKeyboard(view)
    }

    protected fun setColorStatusBar(idColor : Int){
        if(activity != null){
            (activity as BaseActivity<*>).window.statusBarColor = ContextCompat.getColor(requireContext(), idColor)
        }
    }

    companion object{
        var isGoToSetting = false
        fun <F : Fragment> newInstance(fragment: Class<F>, args: Bundle? = null): F {
            val f = fragment.newInstance()
            args?.let {
                f.arguments = it
            }
            return f
        }

    }
}
