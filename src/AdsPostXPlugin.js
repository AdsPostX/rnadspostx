import { NativeModules, NativeEventEmitter } from 'react-native';
class AdsPostXPlugin {
  constructor(props) {
    this.style = 0;
    this.transparent = 0;
  }

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

  show = (topMargin, rightMargin, bottomMargin, leftMargin, callback) => {
    const eventEmitter = new NativeEventEmitter(NativeModules.adsPostXPlugin);
    eventEmitter.addListener('onShow', (event) => {
      console.log(`on show result: ${event}`);
    });
    eventEmitter.addListener('onError', (event) => {
      console.log(`on Error result: ${event}`);
      if (eventEmitter) {
        eventEmitter.removeAllListeners('onError');
        eventEmitter.removeAllListeners('onShow');
        eventEmitter.removeAllListeners('onDismiss');
      }
      callback(event);
    });
    eventEmitter.addListener('onDismiss', (event) => {
      console.log(`on dismiss result: ${event}`);
      if (eventEmitter) {
        eventEmitter.removeAllListeners('onError');
        eventEmitter.removeAllListeners('onShow');
        eventEmitter.removeAllListeners('onDismiss');
      }
      callback(event);
    });

    NativeModules.adsPostXPlugin.show(
      this.style,
      this.transparent === 0,
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
  setPresentationStyle = (style) => {
    this.style = style;
  };
  setTransparancy = (transparent) => {
    this.transparent = transparent;
  };
}

const adsPostXPlugin = new AdsPostXPlugin();
export default adsPostXPlugin;
