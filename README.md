# SmartScanner Cordova

Cordova plugin for the [SmartScanner Core](https://github.com/idpass/smartscanner-core) library to scan MRZ, NFC and barcodes.

## Installation

This plugin can be installed from NPM:

```bash
# Using npm
npm install @idpass/smartscanner-cordova

# Using yarn
yarn add @idpass/smartscanner-cordova

# Then let cordova know about the plugin
cordova plugin add @idpass/smartscanner-cordova
```

## Usage

The plugin is available in the `cordova.plugins` object, which is the registry of all available plugins.

```js
const { SmartScannerPlugin } = cordova.plugins;
```

MRZ scanning example:

```js
const result = await SmartScannerPlugin.executeScanner({
  action: 'START_SCANNER',
  options: {
    mode: 'mrz',
    mrzFormat: 'MRTD_TD1',
    config: {
      background: '#89837c',
      branding: false,
      isManualCapture: true,
    },
  },
});
```
OCR scanning example:

```js
const result = await SmartScannerPlugin.executeScanner({
  action: 'START_SCANNER',
  options: {
    mode: 'ocr',
    ocrOptions: {
      type: 'documentNumber',
      regex: '\\d{4} \\d{4} \\d{5}'
    },
    config: {
      background: '#89837c',
      branding: false,
      isManualCapture: false, //if true user will be required to tap the capture button
      showGuide: true, //values below will only be used when this is set to true
      xGuide: 0.8, //accepts values from 0.0 - 1.0. Offsets the guide horizontally based on percentage.
      yGuide: 0.5, //accepts values from 0.0 - 1.0. Offsets the guide vertically based on percentage.
      widthGuide: 150, //sets the guide width. Default is 150
      heightGuide: 40 //sets the guide height. Default is 40
    },
  },
});

NFC scanning example:

```js
const result = await SmartScannerPlugin.executeScanner({
  action: 'START_SCANNER',
  options: {
    mode: 'nfc-scan',
    config: {
      background: '#89837c',
      branding: false,
      isManualCapture: true,
    },
  },
});
```

Barcode scanning example:

```js
const result = await SmartScannerPlugin.executeScanner({
  action: 'START_SCANNER',
  options: {
    mode: 'barcode',
    barcodeOptions: {
      barcodeFormats: [
        'AZTEC',
        'CODABAR',
        'CODE_39',
        'CODE_93',
        'CODE_128',
        'DATA_MATRIX',
        'EAN_8',
        'EAN_13',
        'QR_CODE',
        'UPC_A',
        'UPC_E',
        'PDF_417',
      ],
    },
    config: {
      background: '#ffc234',
      label: 'Sample Label',
    },
  },
});
```

Refer to the [API Reference](https://github.com/idpass/smartscanner-cordova/wiki/API-Reference) for more information about the available API options and the returned result.

## Related projects

- [smartscanner-core](https://github.com/idpass/smartscanner-core) - Android library for scanning MRZ, OCR, Barcode, and ID PASS Lite cards
- [smartscanner-android-api](https://github.com/idpass/smartscanner-android-api) - Provides convenience methods to simplify the SmartScanner intent call out process
- [smartscanner-capacitor](https://github.com/idpass/smartscanner-capacitor) - SmartScanner [Capacitor](https://capacitorjs.com/) plugin

## License

[Apache-2.0 License](LICENSE)
