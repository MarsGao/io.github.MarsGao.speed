package io.github.MarsGao.speed;

import android.content.Context;
import android.database.Cursor;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

/** Resolves the configured speed inside an injected target-app process. */
final class SpeedConfigBridge {
    private static final float MIN_SPEED = 0.25f;
    private static final float MAX_SPEED = 4.0f;
    private static volatile Context applicationContext;
    private static volatile Float lastProviderSpeed;
    private static volatile String lastSource;

    private SpeedConfigBridge() {
    }

    static void setContext(Context context) {
        if (context != null) {
            applicationContext = context.getApplicationContext();
        }
    }

    static float getSpeed(XSharedPreferences legacyPrefs, float fallback) {
        Float providerSpeed = readProviderSpeed();
        if (providerSpeed != null) {
            return providerSpeed;
        }
        legacyPrefs.reload();
        logSource("XSharedPreferences fallback");
        return sanitizeSpeed(legacyPrefs.getFloat("speed", fallback), fallback);
    }

    static boolean hasSpeedChanged(XSharedPreferences legacyPrefs) {
        Float providerSpeed = readProviderSpeed();
        if (providerSpeed != null) {
            Float previous = lastProviderSpeed;
            lastProviderSpeed = providerSpeed;
            return previous == null || Math.abs(previous - providerSpeed) >= 0.001f;
        }
        return legacyPrefs.hasFileChanged();
    }

    private static Float readProviderSpeed() {
        Context context = resolveContext();
        if (context == null) {
            return null;
        }
        try (Cursor cursor = context.getContentResolver().query(
                SpeedConfigProvider.SPEED_URI,
                new String[]{SpeedConfigProvider.COLUMN_SPEED},
                null,
                null,
                null)) {
            if (cursor != null && cursor.moveToFirst()) {
                float speed = cursor.getFloat(cursor.getColumnIndexOrThrow(SpeedConfigProvider.COLUMN_SPEED));
                if (isValidSpeed(speed)) {
                    logSource("ContentProvider");
                    return speed;
                }
            }
        } catch (Throwable ignored) {
            logSource("XSharedPreferences fallback");
        }
        return null;
    }

    private static float sanitizeSpeed(float speed, float fallback) {
        return isValidSpeed(speed) ? speed : fallback;
    }

    private static boolean isValidSpeed(float speed) {
        return Float.isFinite(speed) && speed >= MIN_SPEED && speed <= MAX_SPEED;
    }

    private static Context resolveContext() {
        Context context = applicationContext;
        if (context != null) {
            return context;
        }
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            java.lang.reflect.Method currentApplication = activityThread.getDeclaredMethod("currentApplication");
            currentApplication.setAccessible(true);
            Context current = (Context) currentApplication.invoke(null);
            if (current != null) {
                setContext(current);
                return applicationContext;
            }
        } catch (Throwable ignored) {
            // The legacy XSharedPreferences fallback remains available below.
        }
        return null;
    }

    private static void logSource(String source) {
        if (!source.equals(lastSource)) {
            lastSource = source;
            XposedBridge.log("[VideoSpeed] [Config] source=" + source);
        }
    }
}
