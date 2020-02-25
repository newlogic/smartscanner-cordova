// Empty constructor
function MLKitPlugin() {}

// The function that passes work along to native shells
// Message is a string, duration may be 'long' or 'short'
MLKitPlugin.prototype.startMLActivity = function(successCallback, errorCallback) {
  var options = {};
  cordova.exec(successCallback, errorCallback, 'MLKitPlugin', 'startMLActivity', [options]);
}

// Installation constructor that binds MLKitPlugin to window
MLKitPlugin.install = function() {
  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.mlkitplugin = new MLKitPlugin();
  return window.plugins.mlkitplugin;
};
cordova.addConstructor(MLKitPlugin.install);