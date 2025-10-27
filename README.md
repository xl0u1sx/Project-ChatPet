## LLM Inference on Android

The original repo said: Note that LLM Inference API is optimized for high-end Android devices and does not reliably support device emulators.

NOTE: But by experiment, the Pixel 2 API 36.0 can run this version of repo just fine.

### ADB (Android Debug Bridge) 
**Check if you have ADB installed.**
* Go to Tools > SDK Manager.
* In the SDK Manager window, select SDK Tools.
* Look for Android SDK Platform-Tools. If it's not installed, check the box and click Apply to install it.
* Note the Android SDK Location displayed at the top of the SDK Manager window. This is your SDK directory.
* The adb executable is located in the platform-tools subdirectory within your SDK directory. For example, if your SDK location is /Users/yourusername/Library/Android/sdk, then adb will be at /Users/yourusername/Library/Android/sdk/platform-tools/adb.
* Add the SDK directory to your PATH environment variable, e.g., by typing the following command in a terminal: `export PATH=$PATH:/Users/yourusername/Library/Android/sdk/platform-tools`

**Verify `adb` is working.**
* Open a terminal and type: `adb devices`
* This should show all the list of connected devices or emulators. Make sure you have at least one emulator or device running.

NOTE: When you reach the "Verify `adb` is working" step, please make sure the emulator is running. DO NOT TURN THE EMULATOR OFF after setting up, you'll need it on for the file push to work. 

### Follow the Quickstart
Following the Google's Quickstart: https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference/android#quickstart


After running `adb devices -l` in the Android Studio terminal, you should see some output like the following: 
```
% adb devices -l

List of devices attached
emulator-5554          device product:sdk_gphone64_arm64 model:sdk_gphone64_arm64 device:emu64a transport_id:3
```

Make sure there's no previously loaded models. 
```
% adb shell rm -r /data/local/tmp/llm/

rm: /data/local/tmp/llm/: No such file or directory
```

Remake the directory
```
 % adb shell mkdir -p /data/local/tmp/llm/
```

Download gemma3-1b-it-int4.task from [Huggingface](https://huggingface.co/litert-community/Gemma3-1B-IT/tree/main) 
Then push the downloaded file (assume it's in your `Download` directory)
You can save this model path inside `strings.xml` and call it from your code.
```
 % adb push /Users/<USERNAME>/Downloads/gemma3-1b-it-int4.task /data/local/tmp/llm/gemma3-1b-it-int4.task
 
/Users/<USERNAME>/Downloads/gemma3-1b-it-int4.task: 1 file pushed, 0 skipped. 805.1 MB/s (554661243 bytes in 0.657s)
```

The LLM should be working now :D

**Initialize and run the task**
* Follow the Quickstart and the code in `MainActivity.kt` and `ChatViewModel.java` for reference.
* In my experience, getting a response to short prompts didn't take more than 1 minute.  See [the attached screenshot](screenshot.png).

NOTE: the running time depends on your RAM and GPU used


