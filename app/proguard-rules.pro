# Keep Room generated code
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Keep notification receivers
-keep class com.karnegar.app.reminders.** { *; }
