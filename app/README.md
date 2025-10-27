## LLM Inference on Android

Note that LLM Inference API is optimized for high-end Android devices and does not reliably support device emulators.

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

### Follow the Quickstart
Following the Google's Quickstart: https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference/android#quickstart

**Add dependencies**
* Make sure you add it to the APP build.gradle file

**Download a HuggingFace model**
* I've selected a `gemma3-1b-it-int4.task` to download from HuggingFace.  This becomes your `model_version`.  Just downloading this one file should be enough.
* You should be able to push the model to your device.  For example, if you have the model in Downloads folder: `adb push ~/Downloads/gemma3-1b-it-int4.task /data/local/tmp/llm/gemma3-1b-it-int4.task`
* Now, you can save this model path inside `strings.xml` and call it from your code.

**Initialize and run the task**
* Follow the Quickstart and the code in `MainActivity.kt` and `ChatViewModel.java` for reference.
* Note: ChatViewModel has been converted to Java. See `CONVERSION_NOTES.md` for details.
* In my experience, getting a response to short prompts didn't take more than 1 minute.  See [the attached screenshot](screenshot.png).