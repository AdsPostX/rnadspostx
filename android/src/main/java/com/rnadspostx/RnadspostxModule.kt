package com.rnadspostx

import android.os.Handler
import android.os.Looper
import com.adspostx.sdk.*
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

class RnadspostxModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

    private var context: ReactApplicationContext = reactContext

  @ReactMethod
  fun initWith(accountId: String,
               completion: Callback
  ) {
    val map = Arguments.createMap()
    AdsPostX.init(accountId) { status, error ->
      map.putBoolean(Keys.STATUS, status)
      if(error != null) {
        map.putString(Keys.ERROR, error.message)
      }
    }
    completion.invoke(map)
  }

  @ReactMethod
  fun load(attributes: ReadableMap,
           completion: Callback){
    Handler(Looper.getMainLooper()).post {

      val map = attributes.toHashMap().toMap()

            AdsPostX.load(context,map) { status, error ->
              val map = Arguments.createMap()
              map.putBoolean(Keys.STATUS, status)
              if (error != null) {
                map.putString(Keys.ERROR, error.message)
              } else {
                map.putNull(Keys.ERROR)
              }
              completion.invoke(map)
            }          
    }
  }


  @ReactMethod
  fun show(presentationStyle: Int,
           isTransparent: Boolean,
           topMargin: Int,
           rightMargin: Int,
           bottomMargin: Int,
           leftMargin: Int
  ){
    var style: AdsPostXPresentationStyle = AdsPostXPresentationStyle.POPUP

    if(presentationStyle == 1) {
      style = AdsPostXPresentationStyle.FULLSCREEN
    }
        AdsPostX.showOffers(
          style,
          isTransparent,
          Margin(topMargin.toUInt(), bottomMargin.toUInt(),
            leftMargin.toUInt(), rightMargin.toUInt()
          ),
          onShow = {
            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
              ?.emit(Events.ON_SHOW,true)
          }, onError = {
            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
              ?.emit(Events.ON_ERROR,it.message)
          },
          onDismiss = {
            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
              ?.emit(Events.ON_DISMISS,true)
          })
  }


  @ReactMethod
  fun setTimeOut(seconds: Double) {
    AdsPostX.setTimeOut(seconds)
  }

  @ReactMethod
  fun setDebugLog(isEnabled: Boolean) {
    AdsPostX.setDebugLog(isEnabled)
  }

  @ReactMethod
  fun setEnvironment(env: Int) {
    if(env == 1) {
      AdsPostX.setEnvironment(AdsPostxEnvironment.TEST)
    } else {
      AdsPostX.setEnvironment(AdsPostxEnvironment.LIVE)
    }
  }

  @ReactMethod
  fun addListener(eventName: String) {
    // Set up any upstream listeners or background tasks as necessary
  }

  @ReactMethod
  fun removeListeners(count: Int) {
    // Remove upstream listeners, stop unnecessary background tasks
  }

  override fun getName(): String {
    return "adsPostXPlugin"
  }
}
