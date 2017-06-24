# Loggerek
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)    

[![BuddyBuild](https://dashboard.buddybuild.com/api/statusImage?appID=594e77e516479d00017a517a&branch=master&build=latest)](https://dashboard.buddybuild.com/apps/594e77e516479d00017a517a/build/latest?branch=master)

Loggerek is App for lazy geocachers :smile:

Works with opencaching api:
https://opencaching.pl/okapi/

# Technologies used
- 100% Kotlin
- Retrofit2 and OkHttp
- RxJava2
- Koin (DI)
- and more... 

# Usage (for developers)
If you want build this app you have to add keys, you can genereate it here:
https://opencaching.pl/okapi/signup.html
- opencaching key in [strings.xml](../master/app/src/main/res/values/strings.xml) file

```
    <string name="consumer_key">set_your_key</string>
    <string name="consumer_secret">set_your_key</string>
```
