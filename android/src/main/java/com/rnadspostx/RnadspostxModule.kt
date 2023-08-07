package com.rnadspostx

import android.os.Handler
import android.os.Looper
import com.adspostx.sdk.*
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactMethod
import org.json.JSONObject
import org.json.JSONArray

class RnadspostxModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

    private var context: ReactApplicationContext = reactContext

  @ReactMethod
  fun initWith(sdkId: String,
               completion: Callback
  ) {
    val map = Arguments.createMap()
    AdsPostX.init(sdkId) { status, error ->
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

private fun convertNullableMapToMap(nullableMap: Map<String, Any?>): Map<String, Any> {
    return nullableMap
        .filterValues { it != null } // Remove null values
        .mapValues { it.value!! } // Convert Any? to Any (force unwrapping)
}

private fun readableMapToAnyMap(readableMap: ReadableMap?): Map<String, Any?> {
  if (readableMap == null) {
      // Handle the null case
      return emptyMap()
  }

  val map = mutableMapOf<String, Any?>()

  val iterator = readableMap.keySetIterator()
  while (iterator.hasNextKey()) {
      val key = iterator.nextKey()
      val value = readableMap.getType(key)
      when (value) {
          ReadableType.Null -> map[key] = null
          ReadableType.Boolean -> map[key] = readableMap.getBoolean(key)
          ReadableType.Number -> map[key] = readableMap.getDouble(key)
          ReadableType.String -> map[key] = readableMap.getString(key)
          ReadableType.Map -> map[key] = readableMap.getMap(key)?.let { readableMapToAnyMap(it) }
          ReadableType.Array -> map[key] = readableMap.getArray(key)?.let { readableArrayToAnyList(it) }
          // Add more cases for other types if needed
          else -> map[key] = null // Set unknown types to null
      }
  }

  return map
}

private fun readableArrayToAnyList(readableArray: ReadableArray): List<Any?> {
  val list = mutableListOf<Any?>()

  for (i in 0 until readableArray.size()) {
      val value = readableArray.getType(i)
      when (value) {
          ReadableType.Null -> list.add(null)
          ReadableType.Boolean -> list.add(readableArray.getBoolean(i))
          ReadableType.Number -> list.add(readableArray.getDouble(i))
          ReadableType.String -> list.add(readableArray.getString(i))
          ReadableType.Map -> list.add(readableArray.getMap(i)?.let { readableMapToAnyMap(it) })
          ReadableType.Array -> list.add(readableArray.getArray(i)?.let { readableArrayToAnyList(it) })
          // Add more cases for other types if needed
          else -> list.add(null) // Set unknown types to null
      }
  }

  return list
}


private fun jsonArrayToWritableArray(jsonArray: JSONArray): WritableArray {
  val writableArray = Arguments.createArray()
  for (i in 0 until jsonArray.length()) {
    val value = jsonArray[i]
    when (value) {
      is String -> writableArray.pushString(value)
      is Int -> writableArray.pushInt(value)
      is Double -> writableArray.pushDouble(value)
      is Boolean -> writableArray.pushBoolean(value)
      is JSONObject -> writableArray.pushMap(jsonObjectToWritableMap(value))
      is JSONArray -> writableArray.pushArray(jsonArrayToWritableArray(value))
      else -> writableArray.pushNull()
    }
  }
  return writableArray
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
          is JSONArray -> writableMap.putArray(key, jsonArrayToWritableArray(value))
          else -> writableMap.putNull(key)
          // Add additional cases for other types if needed
      }
  }
  return writableMap
}

@ReactMethod
fun getOffers(sdkId: String, parameters: ReadableMap, completion: Callback) {
  val attributes = readableMapToAnyMap(parameters)
  val senitizedAttributes =  convertNullableMapToMap(attributes)

  AdsPostX.getOffers(sdkId, senitizedAttributes, context) { result ->
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
