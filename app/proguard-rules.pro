# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#反射中使用的元素,如一些ORM框架的使用,需要保证类名、方法不变, 不然混淆后, 就反射不了
-keepattributes Signature
-keepattributes *Annotation*

# Retain service method parameters.
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

#不混淆bean类和这些类的所有成员变量
-keep class com.example.myapplication.bean.**{*;}

#有用到WEBView的JS调用接口，需加入如下规则:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

#Gson
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

#agentweb
-keep class com.just.agentweb.** {
    *;
}
-keepclassmembers class com.just.library.agentweb.AndroidInterface{ *; }
-keepclassmembers class com.example.myapplication.impl.AndroidInterface{ *; }
-dontwarn com.just.agentweb.**


# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# Gson specific classes,keep class com.google.gson.stream.** { *; }
-dontwarn sun.misc.**
