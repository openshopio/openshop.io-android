<p align="center">
<a href="http://openshop.io/">
<img src="http://i.imgur.com/fLhSUr0.png?1"/>
</a>
</p>
  
**First mobile E-commerce solution connected to Facebook Ads and Google**
  <br/>

We as a Facebook Marketing Partner company have experienced neverending struggles with Facebook and Google integration on side of our partners. It usually took weeks or months to implement all neccessary SDKs, features, measurements and events. That's the reason why we have decided to provide an open-source solution bringing marketing on the first place. Our mission is to fulfill all potential of Facebook Ads, Google Analytics and other marketing channels for an extraordinary mobile shopping experience.

*Visit out website on [openshop.io](http://openshop.io/)*

Do you want to see the app in action? 

<a href='https://play.google.com/store/apps/details?id=bf.io.openshop'>
<img src='http://s24.postimg.org/mmox1wai9/google_play_badge.png' border='0' alt="google play badge" />
</a>



# Features
* **Facebook Ads Integration** - The most advanced Facebook Ads Integration. Encourage purchases, target your mobile customers and measure conversions.
* **Google Analytics Integration** - Integration of Google Analytics can be sometimes tricky. With our solution you can't miss any conversion.
* **Unified & Powerful API** - Connecting your backend and data storages has never been easier. Our API is, thanks to Apiary, very well documented and available for testing.
* **Push Notifications** - Encourage your customers through absolutely powerful Push Notifications. So, your customers won't miss any sale or special offer.
* **Synchronized with your Web** - Synchronize abandoned shopping carts and user profiles from your website. So, your customers won't feel the difference if coming to website or mobile app.
* **Standardized XML Feeds** - We use support all advanced XML Feed features you know from other systems. So, you don't have to create another one and just connect the feed you already use.
* We also offer Deeplinks, Advanced measurement & analytics, and much more...



# How to connect
We are trying to minimize the effort necessary to ship your ecommerce mobile solution. The first step for a successful integration is connection between your data source and our server. There is prepared a standardized [apiary.io](http://docs.bfeshopapiconnector.apiary.io/) documentation which will tell you how to output your XML feed to be compatible with the data the app is expecting. For more information on how to connect to our server please [contact us](#contact-us).

![img](http://openshop.io/img/schema.png)



# Technical Intro
OpenShop.io uses Gradle as a dependency manager for OpenShop.io Android project. To run the project please follow these steps:

## Requirements
* **Application requirements:** API level 15+ ("minSdkVersion 15")
* **Development requirements:** Android studio - the latest version is the best version. Simply download Android studio, import github project, build it with gradle and development is ready.

## Graphics template
[Here](http://openshop.io/sources/openshop.io-ui_resources-android.zip) you will find the PSD template which served as the guideline for implementing user interface in the OpenShop app.

## Run the app
The example OpenShop.io application you can download from this repository or [Google Play](http://play.google.com/store/apps) runs on our custom sample data source (product, payments, shipping, branches,...). If you want to integrate your feed within the app take a look at the section [how to connect](#how-to-connect).



# Release the app with minimal effort
Do you want to release the app like 1, 2, 3, BLAST OFF!? It is as easy as creaing a few web services and editing a few files and you are done.

1. Rename the project. Especialy a package name in [Manifest and Build files](http://stackoverflow.com/questions/16804093/android-studio-rename-package).
2. Put your Organization id into the `ORGANIZATION_ID` variable inside the "CONST.java" file. You will receive the Organization id after a successfull connection, take a look at the section [how to connect](#how-to-connect).
    ![ORGANIZATION_ID](http://s29.postimg.org/n1ptf3hqv/tutorial_organization_id.jpg)
3. Update UI 
    * colors - currently defined in the "colors.xml" file under resources folder.
    * logos, icons - you will find all the image resources beneath the "drawable" in the resources folder. Replace all the OpenShop logos and images with your custom ones.
    * banners - upload custom banners which appear on the title page through the administration - sales, new collections.
4. Create Facebook application (skip this if you already have one) - [Facebook tutorial](https://developers.facebook.com/quickstarts/?platform=android)
    ![Facebook application settings](http://s21.postimg.org/4dssvr0rr/tutorial_facebook_app.jpg)
5. Connect Facebook application and the OpenShop project - enter Facebook application ID on these places: 
    * `facebook_app_id` inside the "strings.xml" file:
    ![facebook_app_id](http://s16.postimg.org/6r2gy4dpx/tutorial_facebook_app_id.jpg)
    * administration on our server
6. Validate FB configuration: Thanks to the [Facebook App Ads Helper](https://developers.facebook.com/tools/app-ads-helper/) you will be able to determine if all of the parts of the configuration were successful.
7. At the [Google Developers Console](https://console.developers.google.com/) create corresponding application. Also enable `Google Cloud Messaging API` and `Google Maps Android API` under the "API Manager -> Overview" section. For GCM integration follow the [tutorial](https://developers.google.com/cloud-messaging/android/client).
8. Under the "API Manager -> Credentials" section create a `Server` type and a `Android` type credentials. The `Server` type credential enter to the administration on our server. It is needed for Google Cloud Messaging (GCM), which provides you a feature to inform the customers about the newest collections and sales. The `Android` type credential enter to the `android_key` property inside the "strings.xml" file: ![android_key](http://s12.postimg.org/xfd7zj4n1/tutorial_android_credentials.jpg)
9. Finally in the Android Studio [generate a signed apk](http://developer.android.com/tools/publishing/app-signing.html).
10. Create new application in [Google Play Developer Console](https://play.google.com/apps/publish/). Upload the signed apk into the created application. Add a title, description, screenshots, publish it and you are DONE.

## Nice-to-have features
Implementing these features will significantly help you with getting to know the users, collecting the data about the app usage or remarketing but they aren't crutial for running the app.
* **Analytics** - To measure conversions or application usage and installs from the campaigns you can use implemented Google Analytics or Facebook Analytics inside `Analytics` class. This class serves as the proxy for different loggers. 
  * Facebook Analytics - If you have successfully configured Facebook SDK you can start logging the events. Simply uncomment a line of code in the `Analytics` class.
  * Google Analytics - All codes for tracking events and campaigns are prepared. You have to create [Google Analytics](https://www.google.com/analytics/) account, [connect it to the application](https://developers.google.com/analytics/devguides/collection/android/v4/) and simply uncomment a few lines of code in the `Analytics` class.

* **Fabric (Crashlytics)** - As Rocky Balboa once said: *"The world ain’t all sunshine and rainbows. It's about how hard you can get hit and keep moving forward."*. And let's face it. Every app can get hit pretty hard and crash. But it's about how you can analyze the crash and fix it. That's the reason why we are using Crashlytics to analyze the bugs that made the application crash. Take a look at their [Get started guide](https://get.fabric.io/) and when you are done there are two blocks of code in the `MyApplication` class which will make the integration easier.
 


# Development
The application is written with the help of widespread libraries, so the source code should be easy to read. If you wish to make more significant changes and better understanding what's going on we are providing in-depth comments inside the code. 

Don't you have your mobile development team? We totally understand that pain, when your IT department is super busy or you don't have your own in-house mobile development team. We will connect you with one of our integration partners to build the app for you. Just [contact us](#contact-us).



# TODO
* Unit Tests

# Contribution

Feel free to build your own mobile ecommerce solution on top of OpenShop.io. Send us the name of the application on help@openshop.io to get **$500** advertisement credit on Facebook for free from [Business Factory](http://b.cz/en/) - one of the 50 official Facebook Marketing Partners worldwide.

# Contact us

Do you have any troubles or issue to report?
Do you like OpenShop.io project and want to be part of it? Great! 
Contact us on help@openshop.io or here on GitHub.


# Licence

