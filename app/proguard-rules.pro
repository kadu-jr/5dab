# Keep Kotlinx Serialization classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.carlosmarins.fivedab.**$$serializer { *; }
-keepclassmembers class com.carlosmarins.fivedab.** {
    *** Companion;
}
-keepclasseswithmembers class com.carlosmarins.fivedab.** {
    kotlinx.serialization.KSerializer serializer(...);
}
