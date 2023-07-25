package com.mobiai.app.ui.fragment

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.mobiai.R
import com.mobiai.app.adapter.LanguageAdapter
import com.mobiai.app.adapter.MyRingtoneAdapter
import com.mobiai.app.ui.dialog.RequestWriteSettingDialog
import com.mobiai.app.ui.model.ItemDeviceRingtone
import com.mobiai.app.ultils.AudioControllerRingtone
import com.mobiai.app.ultils.Constant
import com.mobiai.app.ultils.MyRingtoneManager
import com.mobiai.app.ultils.MyRingtoneManager.setDefaultRingtone
import com.mobiai.base.basecode.extensions.gone
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.language.Language
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.base.basecode.ultility.RxBus
import com.mobiai.base_storage.permission.StoragePermissionUtils
import com.mobiai.databinding.FragmentSettingRingstoneBinding

class RingstoneFragment : BaseFragment<FragmentSettingRingstoneBinding>() {
    companion object{
        fun instance():RingstoneFragment {
            return newInstance(RingstoneFragment::class.java)
        }
    }
   private lateinit var myRingtoneAdapter: MyRingtoneAdapter
    var listRingtones: ArrayList<ItemDeviceRingtone> = arrayListOf()
    private var defaultRingtone: ItemDeviceRingtone? = null
    private var currentPlayingRingtone: ItemDeviceRingtone? = null
    private var currentSelectedItem: ItemDeviceRingtone? = null
    private lateinit var audioController: AudioControllerRingtone
    private lateinit var requestWriteSettingDialog: RequestWriteSettingDialog
    private val DEFAULT_WRITE_SETTING_CODE: Int = 1111


    override fun initView() {
        getDefaultRingtoneFromDevice()
        audioController = AudioControllerRingtone(requireContext())
        binding.sbRingtoneVolume.setProgress(0f, 100f)
        binding.sbRingtoneVolume.setIndicatorTextDecimalFormat("0")
        binding.sbRingtoneVolume.setIndicatorTextStringFormat("%s%%")

        binding.icArrowLeft.setOnClickListener {
            if (currentPlayingRingtone != null) {
                stopRingtone(currentPlayingRingtone!!)
            }
            handlerBackPressed()
        }
    }

    private fun showLoading() {
        binding.frProgressBar.visible()
    }

    private fun hideLoading() {
        binding.frProgressBar.gone()
    }
    private fun getDefaultRingtoneFromDevice() {
        listRingtones = arrayListOf()
        showLoading()
        runBackground({
            MyRingtoneManager.getDeviceRingtone(requireContext())
        },{
            listRingtones.addAll(it)
            initAdapter()
            hideLoading()
        },{
            t: Throwable ->
            Log.d("TAG", "getDefaultRingtoneFromDevice: ${t.message}")
            hideLoading()
            showNotFoundRingtoneNotice()
        })
    }
    private fun showNotFoundRingtoneNotice(){
        binding.tvNotFound.visible()
    }
    private fun initAdapter(){
        myRingtoneAdapter =  MyRingtoneAdapter(requireContext(), object : MyRingtoneAdapter.OnRingtoneClickListener{
            override fun onClickItemListener(item: ItemDeviceRingtone) {
                playRingtone(item)
                setDeviceRingtone(item)
            }
        })
        binding.rcvRingtone.layoutManager = LinearLayoutManager(requireContext())
        myRingtoneAdapter.setItems(listRingtones)
        binding.rcvRingtone.adapter = myRingtoneAdapter
    }

    private fun setDeviceRingtone(ringtone: ItemDeviceRingtone) {
        if (ringtone.type == Constant.APP_RINGTONE) {
            defaultRingtone = ringtone
        } else {
            defaultRingtone = ringtone
            setDefaultRingtone(defaultRingtone!!)
        }
    }
    private fun requestWriteSettingPermission(code: Int) {
        requestWriteSettingDialog = RequestWriteSettingDialog(
            requireContext()
        ) {
            openWriteSettingInDevice(code)
        }
        requestWriteSettingDialog.setOnDismissListener {
            hideLoading()
        }
        if (!requestWriteSettingDialog.isShowing) {
            requestWriteSettingDialog.show()
        }
    }
    private fun openWriteSettingInDevice(code: Int) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:" + requireContext().packageName)
        startActivityForResult(intent, code)
    }
    fun setDefaultRingtone(ringtoneItem: ItemDeviceRingtone) {
        if (!Settings.System.canWrite(requireContext())) {
            requestWriteSettingPermission(DEFAULT_WRITE_SETTING_CODE)
            return
        }
        setDefaultRingtone(requireContext(), ringtoneItem)

        val result = RingtoneManager.getActualDefaultRingtoneUri(
            context, RingtoneManager.TYPE_RINGTONE
        )
        if (result == ringtoneItem.uri) {

            SharedPreferenceUtils.saved_ringtone_path =
                MyRingtoneManager.getPathFromUri(requireContext(), result)
            SharedPreferenceUtils.saved_ringtone_id = ringtoneItem.id

            updateAdapterData()
           Toast.makeText(requireContext(),"set success",Toast.LENGTH_SHORT).show()
            hideLoading()
        } else {
            Log.e("TAG", "setDefaultringtone: set default ringtone failed")
            hideLoading()
        }

    }
    private fun updateAdapterData() {
        if (myRingtoneAdapter != null) {
            myRingtoneAdapter.updateSelectedItem()
        }
    }
    private fun playRingtone(ringtone: ItemDeviceRingtone) {
        if (currentPlayingRingtone == null) {
            play(ringtone)
        } else if (currentPlayingRingtone == ringtone) {
            Log.i("TAG", "playRingtone: true")
            if (audioController.isPause()) {
                Log.i("TAG", "playRingtone: pause")
                continuePlay(ringtone)
            } else if (audioController.isPlaying()) {
                Log.i("TAG", "playRingtone: playing")
                pause(ringtone)
            }
            updateRingtoneIsPlayingState(ringtone)

        } else if (currentPlayingRingtone != ringtone) {
            stopRingtone(currentPlayingRingtone!!)
            play(ringtone)
        }
    }
    private fun updateRingtoneIsPlayingState(ringtone: ItemDeviceRingtone, isCompleted: Boolean = false) {
        if (isCompleted) {
            currentPlayingRingtone = null
        }
        myRingtoneAdapter.updateItemRingtonePlayingState(ringtone)
    }

    private fun stopRingtone(ringtone: ItemDeviceRingtone) {
        audioController.stop()
        ringtone.isPlaying = false
        updateRingtoneIsPlayingState(ringtone)
    }

    private fun pause(ringtone: ItemDeviceRingtone) {
        ringtone.isPlaying = false
        updateRingtoneIsPlayingState(ringtone)
        audioController.pause()
    }

    private fun continuePlay(ringtone: ItemDeviceRingtone) {
        ringtone.isPlaying = true
        updateRingtoneIsPlayingState(ringtone)
        audioController.continuePlaying()
    }

    private fun play(ringtone: ItemDeviceRingtone) {
        ringtone.isPlaying = true
        updateRingtoneIsPlayingState(ringtone)
        if (ringtone.type == Constant.APP_RINGTONE) {
            audioController.play(ringtone.id) {
                ringtone.isPlaying = false
                updateRingtoneIsPlayingState(ringtone, true)
            }
        } else {
            audioController.play((ringtone).uri) {
                ringtone.isPlaying = false
                updateRingtoneIsPlayingState(ringtone, true)
            }
        }
        currentPlayingRingtone = ringtone
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       if (requestCode == DEFAULT_WRITE_SETTING_CODE) {
            if (Settings.System.canWrite(requireContext())) {
                if (defaultRingtone != null) {
                    setDefaultRingtone(defaultRingtone!!)
                }
            } else {
                hideLoading()
            }
        }
    }

    override fun handlerBackPressed() {
        super.handlerBackPressed()
        closeFragment(this)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingRingstoneBinding = FragmentSettingRingstoneBinding.inflate(inflater,container,false)
}