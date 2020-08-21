// Empty constructor
function MLKitPlugin() {}

// The function that passes work along to native shells
MLKitPlugin.prototype.startMLActivity = function(options = {}, successCallback, errorCallback) {
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