package com.example.uidesignlab;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.uidesignlab.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/users");

    private static final int USERS = 1;
    private static final int USER_ID = 2;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Matcher for all users (e.g., content://.../users)
        URI_MATCHER.addURI(AUTHORITY, "users", USERS);
        // Matcher for a specific user by ID (e.g., content://.../users/3)
        URI_MATCHER.addURI(AUTHORITY, "users/#", USER_ID);
    }

    private UserDao userDao;

    @Override
    public boolean onCreate() {
        // Get the DAO instance when the provider initializes
        userDao = AppDatabase.getInstance(getContext()).userDao();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        int match = URI_MATCHER.match(uri);

        switch (match) {
            case USERS:
                cursor = userDao.getAllCursor();
                break;
            case USER_ID:
                long id = ContentUris.parseId(uri);
                cursor = userDao.getByIdCursor(id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Set notification URI so observers know when data changes
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case USERS:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".users";
            case USER_ID:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + ".users";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (URI_MATCHER.match(uri) != USERS) {
            throw new IllegalArgumentException("Invalid URI for insert: " + uri);
        }

        if (values == null || !values.containsKey("name")) {
            throw new IllegalArgumentException("Name is required for insert");
        }

        String name = values.getAsString("name");
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        long id = userDao.insert(new UserRecord(name));

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }

        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        if (match != USER_ID) {
            throw new IllegalArgumentException("Delete is only supported for a single user ID URI: " + uri);
        }

        long id = ContentUris.parseId(uri);
        int rowsDeleted = userDao.deleteById(id);

        if (rowsDeleted > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        if (match != USER_ID) {
            throw new IllegalArgumentException("Update is only supported for a single user ID URI: " + uri);
        }

        if (values == null || !values.containsKey("name")) {
            throw new IllegalArgumentException("Name is required for update");
        }

        String name = values.getAsString("name");
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        long id = ContentUris.parseId(uri);
        int rowsUpdated = userDao.updateName(id, name);

        if (rowsUpdated > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }

        return rowsUpdated;
    }
}