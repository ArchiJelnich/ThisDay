# ── Stack traces ────────────────────────────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Room ────────────────────────────────────────────────────────────────────
# Сущности — Room генерирует код через имена полей
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }

# ── Kotlin Parcelize ─────────────────────────────────────────────────────────
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ── Kotlin Serialization / data classes используемые в Room ──────────────────
-keepclassmembers class com.devgardenaj.thisday.room.** { *; }
-keepclassmembers class com.devgardenaj.thisday.infra.** { *; }

# ── Coroutines ───────────────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ── AppWidget RemoteViews (имена методов через reflection) ───────────────────
-keepclassmembers class android.widget.RemoteViews { *; }