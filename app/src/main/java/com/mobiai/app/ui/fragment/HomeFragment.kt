package com.mobiai.app.ui.fragment

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mobiai.app.ui.safe_click.setOnSafeClickListener
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentHomeBinding


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    companion object{
        fun instance() : HomeFragment{
            return newInstance(HomeFragment::class.java)
        }
    }
    override fun initView() {
        binding.cvItemHomeCall.setOnSafeClickListener(500) {
            addFragment(CallAnnouncerFragment.instance())
        }

        binding.cvItemHomeSMS.setOnSafeClickListener(500) {
            addFragment(SmsAnnouncerFragment.instance())
        }

        binding.cvItemHomeSetting.setOnSafeClickListener(500) {
            addFragment(SettingFragment.instance())
        }

        permissionSms()
      //  permissionDefault()
    }

    private fun permissionSms(){
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_SMS,android.Manifest.permission.SEND_SMS,android.Manifest.permission.RECEIVE_SMS), 111)
        }


    }
    private fun permissionDefault(){
      /*  // Kiểm tra xem quyền truy cập thông báo đã được cấp hay chưa
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            // Nếu chưa được cấp, yêu cầu quyền truy cập thông báo
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }*/

        // quyền truy cập vào không làm phiền
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.RECEIVE_BOOT_COMPLETED), 1)
        }

    }
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container,false)
    }
}