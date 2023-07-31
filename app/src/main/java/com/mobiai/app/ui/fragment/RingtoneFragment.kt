package com.mobiai.app.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ads.control.ads.AperoAd
import com.ads.control.billing.AppPurchase
import com.ads.control.funtion.AdCallback
import com.mobiai.BuildConfig
import com.mobiai.app.adapter.MyRingtoneAdapter
import com.mobiai.app.storage.AdsRemote
import com.mobiai.app.ui.activity.LanguageActivity
import com.mobiai.app.ui.dialog.RequestWriteSettingDialog
import com.mobiai.app.ui.model.ItemDeviceRingtone
import com.mobiai.app.ultils.AudioControllerRingtone
import com.mobiai.app.ultils.Constant
import com.mobiai.app.ultils.MyRingtoneManager
import com.mobiai.app.ultils.MyRingtoneManager.setDefaultRingtone
import com.mobiai.app.ultils.NetWorkChecker
import com.mobiai.app.ultils.NetworkConnected
import com.mobiai.app.ultils.listenEvent
import com.mobiai.base.basecode.extensions.gone
import com.mobiai.base.basecode.extensions.visible
import com.mobiai.base.basecode.language.Language
import com.mobiai.base.basecode.storage.SharedPreferenceUtils
import com.mobiai.base.basecode.ui.fragment.BaseFragment
import com.mobiai.databinding.FragmentSettingRingtoneBinding
import kotlin.math.round
import kotlin.math.roundToInt

class RingtoneFragment : BaseFragment<FragmentSettingRingtoneBinding>() {
    companion object{
        fun instance():RingtoneFragment {
            return newInstance(RingtoneFragment::class.java)
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
    private lateinit var audioManager: AudioManager
    private var mediaPlayer: MediaPlayer? = null

    private var isBannerShowed: Boolean = false
    private fun initAdsBanner() {
        if (!AppPurchase.getInstance().isPurchased && AdsRemote.showBanner) {
            AperoAd.getInstance().loadBannerFragment(
                requireActivity(),
                BuildConfig.banner,
                binding.frAds,
                object : AdCallback() {
                    override fun onAdImpression() {
                        super.onAdImpression()
                        isBannerShowed = true
                    }

                })
        } else {
            binding.frAds.gone()
            binding.lineSpaceAds.gone()
        }
    }

    private fun handlerEvent() {
        addDispose(listenEvent({
            when (it) {
                is NetworkConnected -> {
                    if (it.isOn) {
                        binding.frAds.visible()
                        binding.lineSpaceAds.visible()
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (isAdded && !isBannerShowed) {
                                initAdsBanner()
                            }
                        }, 500)
                    } else if (!isBannerShowed) {
                        binding.frAds.gone()
                        binding.lineSpaceAds.gone()
                    }
                }

            }
        }))
    }
    override fun initView() {
        if (NetWorkChecker.instance.isNetworkConnected(requireContext()) && !isBannerShowed) {
            binding.frAds.visible()
            binding.lineSpaceAds.visible()
            initAdsBanner()
        }
        handlerEvent()
        getDefaultRingtoneFromDevice()
        audioController = AudioControllerRingtone(requireContext())
        binding.sbRingtoneVolume.setProgress(0f, 100f)
        binding.sbRingtoneVolume.setIndicatorTextDecimalFormat("0")
        binding.sbRingtoneVolume.setIndicatorTextStringFormat("%s%%")
        binding.sbRingtoneVolume.setProgress(SharedPreferenceUtils.volumeRing.toFloat())

        binding.icArrowLeft.setOnClickListener {
            if (currentPlayingRingtone != null) {
                stopRingtone(currentPlayingRingtone!!)
                mediaPlayer?.release()
                mediaPlayer = null
            }
            else if (mediaPlayer!=null){
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            handlerBackPressed()
        }
        audioManager =
            context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        mediaPlayer?.setOnCompletionListener {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)
            mediaPlayer?.release()
            mediaPlayer = null
        }
        binding.sbRingtoneVolume.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                rangeSeekBar: RangeSeekBar,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                SharedPreferenceUtils.volumeRing = round(leftValue).roundToInt()
                var ratioRing =
                    (100 / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).toFloat()
                var volumeRing =
                    (SharedPreferenceUtils.volumeRing.toFloat() / ratioRing).roundToInt()
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC, volumeRing, 0
                )
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
                defaultRingtone?.let { stopRingtone(it) }
                if(mediaPlayer==null) mediaPlayer = MediaPlayer.create(requireContext(), Settings.System.DEFAULT_RINGTONE_URI)
                mediaPlayer?.start()

            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }
        })
    }

    private fun showLoading() {
        binding.frProgressBar.visible()
    }

    private fun hideLoading() {
        binding.frProgressBar.gone()
    }
    private fun getDefaultRingtoneFromDevice() {
        listRingtones = arrayListOf()
        var ringtoneSystem: ItemDeviceRingtone? = null
        val position = 0
        showLoading()
        runBackground({
            MyRingtoneManager.getDeviceRingtone(requireContext())
        },{

            listRingtones.addAll(it)

            for (ringtone in listRingtones) {
                if (ringtone.isChoose){
                    ringtoneSystem = ringtone
                    break
                }
            }
            if (ringtoneSystem != null) {
                listRingtones.remove(ringtoneSystem)
                listRingtones.add(0, ringtoneSystem!!)
            }
            listRingtones[position].isChoose = true
            if (isAdded){
                initAdapter()
            }
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

    override fun onStop() {
        super.onStop()
        if (currentPlayingRingtone != null) {
            stopRingtone(currentPlayingRingtone!!)
            mediaPlayer?.release()
            mediaPlayer = null
        }
        else if (mediaPlayer!=null){
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
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

            mediaPlayer = MediaPlayer.create(requireContext(), Settings.System.DEFAULT_RINGTONE_URI)
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
        mediaPlayer?.release()
        mediaPlayer = null
        if (currentPlayingRingtone == null ) {
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
    ): FragmentSettingRingtoneBinding = FragmentSettingRingtoneBinding.inflate(inflater,container,false)
}