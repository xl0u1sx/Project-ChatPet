(ADB checks as demonstrated by the README.md)

Please use the updated version in github repo, not the original one from briankim113 as it's not compiling. 

When you reach the "Verify `adb` is working" step, please ==make sure the emulator is running==. DO NOT TURN THE EMULATOR OFF after setting up, you'll need it on for the file push to work. 

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
```
 % adb push /Users/<USERNAME>/Downloads/gemma3-1b-it-int4.task /data/local/tmp/llm/gemma3-1b-it-int4.task
 
/Users/<USERNAME>/Downloads/gemma3-1b-it-int4.task: 1 file pushed, 0 skipped. 805.1 MB/s (554661243 bytes in 0.657s)
```

The LLM should be working now :D
