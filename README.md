# react-native-zendesk-chat

Simple module that allows displaying Zopim Chat from Zendesk for React Native.
(1.4.0 for both Android and iOS SDK)

## VERSIONS

Work with latest version of react-native.

## Known issues

Need PR for customizing the UI for both iOS and Android.

## Getting started - Installing the SDK

Follow the instructions to install the SDK for [iOS](https://developer.zendesk.com/embeddables/docs/ios-chat-sdk/introduction) and [Android](https://developer.zendesk.com/embeddables/docs/android-chat-sdk/introduction).

### Manual install
#### iOS
1. `npm install https://github.com/ZakZubair/react-native-zendesk-chat.git --save`
   or `yarn add https://github.com/ZakZubair/react-native-zendesk-chat.git`
2. Create a folder called  `ZDCChat` under your project's folder name
3. In Xcode, drag and drop `RNZendeskChatModule.h` and `RNZendeskChatModule.m` inside `node_modules/react-native-zendesk-chat/ios` into `ZDCChat` as references.
4. Add `pod 'ZDCChat'` to your PodFile
   If you don't have one, create it and add
  ```
  source 'https://github.com/CocoaPods/Specs.git'

  platform :ios, '8.0'

  target 'WriteHereNameOfYourProject' do
    pod 'ZDCChat'
  end
  ```
5. `cd ios/` and `pod install`
6. Add these lines to `Header Search Path` in `Build Setting` as `recursive`
  ```
  $(SRCROOT)/../node_modules/react-native-zendesk-chat/ios
  ${SRCROOT}/Pods/ZDCChat
  ```
7. Configure `ZDCChat` in `AppDelegate.m`:

```
@import ZDCChat;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
  [ZDCChat initializeWithAccountKey:@"YOUR_ZENDESK_ACCOUNT_KEY"];
}
```

#### Android
1. `npm install https://github.com/ZakZubair/react-native-zendesk-chat.git --save`
   or `yarn add https://github.com/ZakZubair/react-native-zendesk-chat.git`
2. Append the following lines to `android/settings.gradle`:
  ```
  include ':react-native-zendesk-chat'
  project(':react-native-zendesk-chat').projectDir = new File(rootProject.projectDir,	'../node_modules/react-native-zendesk-chat/android')
  ```
3. Add these lines to `android/build.gradle` :
  ```
  allprojects {
      repositories {
          {...}
          maven {
              // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
              url "$rootDir/../node_modules/react-native/android"
          }
          maven { url 'https://zendesk.jfrog.io/zendesk/repo' }
      }
  }
  ```
4. Add these lines to `android/app/build.gradle` :
  ```
  dependencies {
      implementation group: 'com.zopim.android', name: 'sdk', version: '1.4.0'
      implementation project(':react-native-zendesk-chat')
      {...}
  }
  ```
5. Add the following lines to `android/app/main/java/[...]/MainApplication.java` :

  - Add `import com.taskrabbit.zendesk.*;` and `import com.zopim.android.sdk.api.ZopimChat;` to the imports at the top of the file.
  - Add `new RNZendeskChatPackage()` like this:
    ```
    protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(
          {...},
          new RNZendeskChatPackage()
        );
      }
    ```
  - Append the following lines to `public void onCreate()` method:
    ```
    @Override
      public void onCreate() {
        super.onCreate();
        {...}
        ZopimChat.init("YOUR_ZENDESK_ACCOUNT_KEY");
        AppEventsLogger.activateApp(this);
      }
    ```

## Usage

In your code:

```
import ZendeskChat from 'react-native-zendesk-chat';

ZendeskChat.startChat({
  name: user.full_name,
  email: user.email,
  phone: user.mobile_phone,
  tags: ['tag1', 'tag2'],
  department: "Your department"
});
```

## TODO

* Allow setting form configuration from JS
* Add examples
