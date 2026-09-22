package io.github.MarsGao.speed;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

/**
 * Read-only IPC bridge for hook processes. App-private SharedPreferences remain
 * the source of truth; this provider avoids direct cross-UID file access.
 */
public class SpeedConfigProvider extends ContentProvider {
    public static final String AUTHORITY = "io.github.MarsGao.speed.config";
    public static final String PATH_SPEED = "speed";
    public static final String COLUMN_SPEED = "speed";
    public static final Uri SPEED_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_SPEED);
    private static final float DEFAULT_SPEED = 1.5f;
    private static final float MIN_SPEED = 0.25f;
    private static final float MAX_SPEED = 4.0f;

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (!PATH_SPEED.equals(uri.getLastPathSegment())) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        SharedPreferences prefs = getContext().getSharedPreferences("speed", 0);
        float speed = prefs.getFloat(COLUMN_SPEED, DEFAULT_SPEED);
        if (!Float.isFinite(speed) || speed < MIN_SPEED || speed > MAX_SPEED) {
            speed = DEFAULT_SPEED;
        }
        MatrixCursor cursor = new MatrixCursor(new String[]{COLUMN_SPEED}, 1);
        cursor.addRow(new Object[]{speed});
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.item/vnd." + AUTHORITY + ".speed";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("VideoSpeed configuration is read-only");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("VideoSpeed configuration is read-only");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("VideoSpeed configuration is read-only");
    }
}
