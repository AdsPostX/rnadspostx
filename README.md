# rnadspostx

AdsPostX offers a react native package designed to help you integrate AdsPostX into your react native mobile application within minutes and start generating more revenue and increasing sales.

## Installation

```sh
npm install rnadspostx
```

## Usage

1. Init AdsPostX:

```js
 const accountId = 'a359ddi39903994f';
 AdsPostXPlugin.init(accountId, (response) => {
     /* if response is true, mean init call is success.
        otherwise you will get error with response.*/
    }
});
```

2. Load AdsPostX Offers:

```js
const attributes = { firstname: 'john', country: 'US' };

AdsPostXPlugin.load(attributes, (response) => {
  /*
    if response is 'true' mean offer loaded successfully,
    else check 'error' field for error while loading offers.
    */
});
```

3. Show AdsPostX Offers:

```js
AdsPostXPlugin.show(
  topMargin, //eg: 5
  rightMargin, //eg: 5
  bottomMargin, //eg: 5
  leftMargin, //eg: 5
  true, // is transparent
  0, // style: 0 - popup, 1 - full screen
  (errorCallback = (error) => {
    console.log(`errorCallback: ${error}`);
  }),
  (showCallback = () => {
    console.log('showCallback: offer shown');
  }),
  (dismissCallback = () => {
    console.log('dismissCallback: offer dismissed');
  })
);
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
