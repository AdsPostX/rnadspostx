import { NativeModules, NativeEventEmitter } from 'react-native';
class AdsPostXPlugin {
  init = (accountId, callback) => {
    NativeModules.adsPostXPlugin.initWith(accountId, (response) => {
      // console.log(`obj : ${obj}`);
      // console.log(`status is : ${obj.status} and error is: ${obj.error}`);
      callback(response);
    });
  };

  load = (attributes, callback) => {
    NativeModules.adsPostXPlugin.load(attributes, (response) => {
      // console.log(`status is : ${resp.status} and error is: ${resp.error}`);
      callback(response);
    });
  };

  show = (
    topMargin,
    rightMargin,
    bottomMargin,
    leftMargin,
    isTransparent,
    style,
    errorCallback,
    showCallback,
    dismissCallback
  ) => {
    const eventEmitter = new NativeEventEmitter(NativeModules.adsPostXPlugin);
    eventEmitter.addListener('onShow', (event) => {
      //console.log(`on show result: ${event}`);
      showCallback();
    });
    eventEmitter.addListener('onError', (event) => {
      //console.log(`on Error result: ${event}`);
      if (eventEmitter) {
        eventEmitter.removeAllListeners('onError');
        eventEmitter.removeAllListeners('onShow');
        eventEmitter.removeAllListeners('onDismiss');
      }
      errorCallback(event);
    });
    eventEmitter.addListener('onDismiss', (event) => {
      //console.log(`on dismiss result: ${event}`);
      if (eventEmitter) {
        eventEmitter.removeAllListeners('onError');
        eventEmitter.removeAllListeners('onShow');
        eventEmitter.removeAllListeners('onDismiss');
      }
      dismissCallback();
    });

    NativeModules.adsPostXPlugin.show(
      style,
      isTransparent,
      topMargin,
      rightMargin,
      bottomMargin,
      leftMargin
    );
  };

  setDebugLog = (isenabled) => {
    NativeModules.adsPostXPlugin.setDebugLog(isenabled);
  };

  setEnvironment = (env) => {
    NativeModules.adsPostXPlugin.setEnvironment(env);
  };
}

const adsPostXPlugin = new AdsPostXPlugin();
export default adsPostXPlugin;
