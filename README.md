U-Report Android
=================

This is the android client of U-Report platform that contains social capabilities such as user stories, chat and media sharing.

Built for UNICEF by Ilhasoft - http://www.ilhasoft.com.br

Getting Started
=================

You need to provide a `keys.xml` file on `values` resource folder in order to run this project. These are the required keys:

`facebook_app_id`: Facebook Application ID created on [quick start page](https://developers.facebook.com/quickstarts/?platform=android);

`twitter_key`: Twitter Key from an app created on [developers page](https://apps.twitter.com);

`twitter_secret`: Twitter Secret from the same app created for `twitter_key`;

`firebase_app`: Firebase app address from [dashboard](http://firebase.com/);

`firebase_app_name`: Firebase app name because of proxy functionality. For example if the app address is https://android-ureport-app.firebaseio.com, the `firebase_app_name` is "android-ureport-app";

`amazon_s3_bucket_id`: Name of bucket to store files on Amazon S3;

`amazon_s3_access_key`: Access Key of amazon user with permissions on above bucket;

`amazon_s3_access_secret`: Access Secret of the same amazon user of above access key;

`gcm_sender_id`: Sender ID of Google Cloud Messaging project. Create a new project [here](https://developers.google.com/mobile/add);

`gcm_api_key`: API Key of the created GCM project;

`youtube_api_key`: Youtube API Key to reproduce youtube videos. See instructions [here](https://developers.google.com/youtube/android/player/?hl=pt-br).

