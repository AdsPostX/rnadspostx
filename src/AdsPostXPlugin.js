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
    const sanitizedAttributes = attributes || {}; // Use {} if attributes is null or undefined
    NativeModules.adsPostXPlugin.load(sanitizedAttributes, (response) => {
      callback(response);
    });
  };

  show = (showOptions = {}) => {
    const {
      position = {
        topMargin: 5.0,
        rightMargin: 5.0,
        bottomMargin: 5.0,
        leftMargin: 5.0,
      },
      styles = { transparent: true, type: 'popup' },
      callbacks = {},
    } = showOptions;

    const { showCallback, errorCallback, dismissCallback } = callbacks;

    const transparent = styles.transparent;
    const type = styles.type === 'popup' ? 0 : 1;

    const eventEmitter = new NativeEventEmitter(NativeModules.adsPostXPlugin);
    eventEmitter.addListener('onShow', (event) => {
      showCallback && showCallback();
    });
    eventEmitter.addListener('onError', (event) => {
      if (eventEmitter) {
        eventEmitter.removeAllListeners('onError');
        eventEmitter.removeAllListeners('onShow');
        eventEmitter.removeAllListeners('onDismiss');
      }
      errorCallback && errorCallback(event);
    });
    eventEmitter.addListener('onDismiss', (event) => {
      if (eventEmitter) {
        eventEmitter.removeAllListeners('onError');
        eventEmitter.removeAllListeners('onShow');
        eventEmitter.removeAllListeners('onDismiss');
      }
      dismissCallback && dismissCallback();
    });

    const { topMargin, rightMargin, bottomMargin, leftMargin } = position;

    NativeModules.adsPostXPlugin.show(
      type,
      transparent,
      topMargin,
      rightMargin,
      bottomMargin,
      leftMargin
    );
  };

  getAttributes = (callback) => {
    NativeModules.adsPostXPlugin.getAttributes((attributes) => {
      callback(attributes);
    });
  };

  getEnvironment = (callback) => {
    NativeModules.adsPostXPlugin.getEnvironment((environment) => {
      callback(environment);
    });
  };

  setDebugLog = (isenabled) => {
    NativeModules.adsPostXPlugin.setDebugLog(isenabled);
  };

  setEnvironment = (env) => {
    NativeModules.adsPostXPlugin.setEnvironment(env);
  };

  setTimeOut = (seconds) => {
    NativeModules.adsPostXPlugin.setTimeOut(seconds);
  };

  getOffers = async (apiKey, parameters = {}) => {
    return new Promise((resolve, reject) => {
      const sanitizedParameters = parameters || {}; // Use {} if parameters is null or undefined
      NativeModules.adsPostXPlugin.getOffers(
        apiKey,
        sanitizedParameters,
        (status, response) => {
          if (status) {
            resolve({ status, response });
          } else {
            reject({ status, response });
          }
        }
      );
    });
  };
}

const adsPostXPlugin = new AdsPostXPlugin();
export default adsPostXPlugin;
