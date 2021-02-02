// Empty constructor
function SmartScannerPlugin() {}

// The function that passes work along to native shells
SmartScannerPlugin.prototype.startSmartScanner = function(options = {}, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, 'SmartScannerPlugin', 'START_SCANNER', [options]);
}

// Installation constructor that binds SmartScannerPlugin to window
SmartScannerPlugin.install = function() {
  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.smartscannerplugin = new SmartScannerPlugin();
  return window.plugins.smartscannerplugin;
};
cordova.addConstructor(SmartScannerPlugin.install);