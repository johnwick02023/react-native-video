package com.brentvatne.react

import androidx.media3.exoplayer.ExoPlayer
import com.brentvatne.exoplayer.ReactExoplayerView
import com.brentvatne.common.api.Source
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.bridge.UiThreadUtil
import com.facebook.react.uimanager.common.UIManagerType
import kotlin.math.roundToInt

class VideoManagerModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = REACT_CLASS

    @UIManagerType
    val type = if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED)
        UIManagerType.FABRIC
    else
        UIManagerType.DEFAULT

    private fun performOnPlayerView(reactTag: Int, callback: (ReactExoplayerView?) -> Unit) {
        UiThreadUtil.runOnUiThread {
            try {
                val uiManager = UIManagerHelper.getUIManager(reactApplicationContext, type)
                val view = uiManager?.resolveView(reactTag)
                if (view is ReactExoplayerView) callback(view) else callback(null)
            } catch (e: Exception) {
                callback(null)
            }
        }
    }

    @ReactMethod
    fun setPlayerPauseStateCmd(reactTag: Int, paused: Boolean) {
        performOnPlayerView(reactTag) { it?.setPausedModifier(paused) }
    }

    @ReactMethod
    fun seekCmd(reactTag: Int, time: Float, tolerance: Float) {
        performOnPlayerView(reactTag) {
            it?.seekTo((time * 1000f).roundToInt().toLong())
        }
    }

    @ReactMethod
    fun setVolumeCmd(reactTag: Int, volume: Float) {
        performOnPlayerView(reactTag) { it?.setVolumeModifier(volume) }
    }

    @ReactMethod
    fun setFullScreenCmd(reactTag: Int, fullScreen: Boolean) {
        performOnPlayerView(reactTag) { it?.setFullscreen(fullScreen) }
    }

    @ReactMethod
    fun enterPictureInPictureCmd(reactTag: Int) {
        performOnPlayerView(reactTag) { it?.enterPictureInPictureMode() }
    }

    @ReactMethod
    fun exitPictureInPictureCmd(reactTag: Int) {
        performOnPlayerView(reactTag) { it?.exitPictureInPictureMode() }
    }

    @ReactMethod
    fun setSourceCmd(reactTag: Int, source: ReadableMap?) {
        performOnPlayerView(reactTag) {
            it?.setSrc(Source.parse(source, reactApplicationContext))
        }
    }

    @ReactMethod
    fun getCurrentPosition(reactTag: Int, promise: Promise) {
        performOnPlayerView(reactTag) { it?.getCurrentPosition(promise) }
    }

    companion object {
        private const val REACT_CLASS = "VideoManager"
    }
}
