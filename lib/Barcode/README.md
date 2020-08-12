
- [ImpactlabsMLKitLibrary](#impactlabsmlkitlibrary)
 	- [Generating AARs](#generating-aars)
  		- [Method 1 Android Studio IDE](#method-1-Android-Studio-IDE)
  		- [Method 2 Gradle CLI](#gradle-cli)
- [MLKit-Demo](#mlkit-demo)
 

# ImpactlabsMLKitLibrary
Contains essential classes and resource files to implement the barcode scanning features based out of Firebase's  [mlkit-material-android](https://github.com/firebase/mlkit-material-android) Project.

## Generating AARs

### Method 1 Android Studio IDE
If you want to share your AAR file separately, you can find it in MLKit-Demo/impactlabsmlkitlibrary/build/outputs/aar/ and you can regenerate it by clicking Build > Make Project in Android Studio. Make sure to select the desired build variant, debug or release.

### Method 2 Gradle CLI
Alternatively, if you want to build the AARs via the Gradle CLI, simply call the assemble task (assembleRelease for release builds, and assembleDebug for debug builds).

```sh
$ gradle assembleDebug or assembleRelease
```
## LiveBarcodeScanningActivity
When starting the LiveBarcodeScanningActivity from your main module, make sure to include the necessary meta-data in the android manifest. Below is an example:

```xml
 <activity
    android:name="com.impactlabs.impactlabsmlkitlibrary.activities.LiveBarcodeScanningActivity"
    android:exported="false"
    android:screenOrientation="portrait">
</activity>
```

# MLKit-Demo
The sample project that showcases how to use the impactlabsmlkitlibrary module.

