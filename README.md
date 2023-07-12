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
const payload = { firstname: 'john', country: 'US' };

AdsPostXPlugin.load((response) => {
  /*
    if response is 'true' mean offer loaded successfully,
    else check 'error' field for error while loading offers.
    */
}, payload);
```

3. Show AdsPostX Offers:

```js
// values used here are just for demo purpose, use actual values when you are integrating SDK.

const showOptions = {
  position: {
    topMargin: 5.0,
    rightMargin: 5.0,
    bottomMargin: 5.0,
    leftMargin: 5.0,
  },
  styles: {
    transparent: true,
    type: 'popup', // OR 'fullscreen',
  },
  callbacks: {
    showCallback: () => {
      console.log('Show callback called');
    },
    errorCallback: (event) => {
      console.log('Error callback called:', event);
    },
    dismissCallback: () => {
      console.log('Dismiss callback called');
    },
  },
};

AdsPostXPlugin.show(showOptions);
```

4. Get Offers

```js
const apiKey = 'YOUR_API_KEY';
const payload = {
  country: 'USA',
  firstname: 'Dev',
  creative: '1',
  dev: '1',
};

try {
  const responseData = await AdsPostXPlugin.getOffers(apiKey, payload);

  // to access offers
  if (responseData.status) {
    const offers = responseData.response.data.offers;
  }
} catch (error) {
  // Handle any errors that occurred during the getOffers call...
}
```

5. Set Environment

```js
AdsPostXPlugin.setEnvironment(0); // for 'live' environment.

OR;

AdsPostXPlugin.setEnvironment(1); // for 'test' environment.
```

6. Set Debug Log

```js
AdsPostx.setDebugLog(true);
OR;
AdsPostx.setDebugLog(false);
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
