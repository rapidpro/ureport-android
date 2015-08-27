# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ilhasoft/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# activeandroid
-keep class com.activeandroid.** { *; }
-keep class com.activeandroid.**.** { *; }
-keep class * extends com.activeandroid.Model
-keep class * extends com.activeandroid.serializer.TypeSerializer

-keepattributes Column
-keepattributes Table
-keepclasseswithmembers class * { @com.activeandroid.annotation.Column <fields>; }

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#Remove Log
-assumenosideeffects class android.util.Log { *; }

# support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

-keep public class * extends android.support.design.widget.CoordinatorLayout$Behavior {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#twitter
-include ../proguard-com.twitter.sdk.android.twitter.txt

#Firebase
-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**

#Retrofit

-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-dontwarn rx.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keep class sun.misc.Unsafe { *; }
-keep class in.ureport.models.** { *; }
-keep class in.ureport.network.** { *; }

#Gson
-keepattributes Signature
-keepattributes *Annotation*

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }

-keepattributes InnerClasses,*Annotation*

#Amazon
#Amazon AWS
-keep class org.apache.commons.logging.**               { *; }
-keep class com.amazonaws.services.sqs.QueueUrlHandler  { *; }
-keep class com.amazonaws.javax.xml.transform.sax.*     { public *; }
-keep class com.amazonaws.javax.xml.stream.**           { *; }
-keep class com.amazonaws.services.**.model.*Exception* { *; }
-keep class com.amazonaws.internal.** 					{ *; }
-keep class org.codehaus.**                             { *; }
-keep class org.joda.convert.*							{ *; }
-keepattributes Signature,*Annotation*,EnclosingMethod
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class com.amazonaws.** { *; }

-dontwarn com.amazonaws.auth.policy.conditions.S3ConditionFactory
-dontwarn org.joda.time.**
-dontwarn com.fasterxml.jackson.databind.**
-dontwarn javax.xml.stream.events.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.http.conn.scheme.**
-dontwarn org.apache.http.annotation.**
-dontwarn org.ietf.jgss.**
-dontwarn org.w3c.dom.bootstrap.**

-dontwarn javax.xml.stream.events.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.http.conn.scheme.**

-dontwarn com.amazon.**
-keep class com.amazon.** {*;}
-keepattributes InnerClasses,*Annotation*

-keep class com.amazonaws.**
-dontwarn com.amazonaws.**

-keep class in.ureport.models.** { *; }
-keep class com.amazonaws.** { *; }

-dontwarn org.apache.commons.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**

#SDK split into multiple jars so certain classes may be referenced but not used
-dontwarn com.amazonaws.services.s3.**
-dontwarn com.amazonaws.services.sqs.**

-dontnote com.amazonaws.services.sqs.QueueUrlHandler
