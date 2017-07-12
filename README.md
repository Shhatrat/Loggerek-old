# Loggerek
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)    [![BuddyBuild](https://dashboard.buddybuild.com/api/statusImage?appID=594e77e516479d00017a517a&branch=master&build=latest)](https://dashboard.buddybuild.com/apps/594e77e516479d00017a517a/build/latest?branch=master)

<a href="https://play.google.com/store/apps/details?id=com.shhatrat.loggerek" alt="Download from Google Play">
  <img src="http://www.android.com/images/brand/android_app_on_play_large.png">
</a>

Loggerek is App for lazy geocachers :smile:

Works with opencaching api:
https://opencaching.pl/okapi/

# Technologies used
- 100% Kotlin
- Retrofit2, Signpost (for OAuth) and OkHttp
- Realm
- RxJava2
- Koin (DI)
- Material Design patterns
- No libs hardly based on ~~reflection~~ like EventBus!
- Many Android stuff like Fragments, Setting Preferences, Recyclerviews, View Pagers...
- ...and more 

# Usage (for humans)
- quick logging using predefined comments
- normal logging with rates, recommendations etc.
- saving password to cache's note automatically
- logging events
- saving unsent logs
- saving notes to cache

# Usage (for developers)
If you want build this app you have to add keys, you can generate it here:
https://opencaching.pl/okapi/signup.html
- opencaching key in [strings.xml](../master/app/src/main/res/values/strings.xml) file

```
    <string name="consumer_key">set_your_key</string>
    <string name="consumer_secret">set_your_key</string>
```
- google maps key also in [strings.xml](../master/app/src/main/res/values/strings.xml) file. 
```
    <string name="google_maps_key">set_your_key</string>
```

# Screenshots
<img src="https://github.com/Shhatrat/Loggerek/raw/master/screens/1.png" width="260"> <img src="https://github.com/Shhatrat/Loggerek/raw/master/screens/2.png" width="260"> <img src="https://github.com/Shhatrat/Loggerek/raw/master/screens/3.png" width="260"> <img src="https://github.com/Shhatrat/Loggerek/raw/master/screens/4.png" width="260"> <img src="https://github.com/Shhatrat/Loggerek/raw/master/screens/5.png" width="260"> <img src="https://github.com/Shhatrat/Loggerek/raw/master/screens/6.png" width="260">
