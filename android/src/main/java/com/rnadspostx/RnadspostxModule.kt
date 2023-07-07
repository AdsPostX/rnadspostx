package com.rnadspostx

import android.os.Handler
import android.os.Looper
import com.adspostx.sdk.*
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactMethod
import org.json.JSONObject

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
  fun load(attributes: ReadableMap, completion: Callback) {
    val callback = object : Callback {
      var invoked = false

      override fun invoke(vararg args: Any?) {
        synchronized(this) {
          if (!invoked) {
            invoked = true
            completion.invoke(*args)
          }
        }
      }
    }

    Handler(Looper.getMainLooper()).post {
      val map = attributes.toHashMap().toMap()

      AdsPostX.load(context, map) { status, error ->
        val resultMap = Arguments.createMap()
        resultMap.putBoolean(Keys.STATUS, status)

        if (error != null) {
          resultMap.putString(Keys.ERROR, error.message)
        } else {
          resultMap.putNull(Keys.ERROR)
        }

        callback.invoke(resultMap)
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
fun getAttributes(completion: Callback) {
  val attributes: Map<String, Any>? = AdsPostX.getAttributes()
  val resultMap = Arguments.createMap()

  if (attributes != null) {
    for ((key, value) in attributes) {
      // Add key-value pairs to the resultMap
      // Adjust the logic here based on the structure of your attributes
      resultMap.putString(key, value.toString())
    }
  }
  completion.invoke(resultMap)
}

@ReactMethod
fun getEnvironment(completion: Callback) {
  val env: String? = AdsPostX.getEnvironment().name
  completion.invoke(env)
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


  private fun readableMapToStringMap(readableMap: ReadableMap?): Map<String, String> {
    if (readableMap == null) {
        // Handle the null case
        return emptyMap()
    }

    val map = mutableMapOf<String, String>()

    val iterator = readableMap.keySetIterator()
    while (iterator.hasNextKey()) {
        val key = iterator.nextKey()
        val value = readableMap.getString(key)
        if (value != null) {
            map[key] = value
        }
    }

    return map
}


private fun jsonObjectToWritableMap(jsonObject: JSONObject): WritableMap {
  val writableMap = Arguments.createMap()
  val iterator = jsonObject.keys()
  while (iterator.hasNext()) {
      val key = iterator.next()
      val value = jsonObject[key]
      when (value) {
          is String -> writableMap.putString(key, value)
          is Int -> writableMap.putInt(key, value)
          is Double -> writableMap.putDouble(key, value)
          is Boolean -> writableMap.putBoolean(key, value)
          is JSONObject -> writableMap.putMap(key, jsonObjectToWritableMap(value))
          // Add additional cases for other types if needed
      }
  }
  return writableMap
}

@ReactMethod
fun getOffers(apiKey: String, parameters: ReadableMap, completion: Callback) {
  val attributes = readableMapToStringMap(parameters)

  AdsPostX.getOffers(apiKey, attributes) { result ->
    result.onSuccess { response ->      
      completion.invoke(true, jsonObjectToWritableMap(response))
    }.onFailure { error ->
      val errorMap: WritableMap = Arguments.createMap()
      errorMap.putString("error", error.message ?: "unknown error!")
      completion.invoke(false, errorMap)
    }
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
