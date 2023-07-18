package com.example.merobook;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.util.HashMap;
import java.util.Map;

public class BookCache {
    private static final String PREFS_NAME = "book_cache";
    private SharedPreferences preferences;

    public BookCache(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isBookCached(String bookId) {
        return preferences.contains(bookId);
    }

    public void cacheBook(String bookId, byte[] data) {
        Map<String, String> cachedBooks = getCachedBooks();
        cachedBooks.put(bookId, Base64.encodeToString(data, Base64.DEFAULT));
        SharedPreferences.Editor editor = preferences.edit();
        for (Map.Entry<String, String> entry : cachedBooks.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    public byte[] getBookFromCache(String bookId) {
        Map<String, String> cachedBooks = getCachedBooks();
        String dataString = cachedBooks.get(bookId);
        if (dataString != null) {
            return Base64.decode(dataString, Base64.DEFAULT);
        }
        return null;
    }

    public void removeBookFromCache(String bookId) {
        Map<String, String> cachedBooks = getCachedBooks();
        cachedBooks.remove(bookId);
        SharedPreferences.Editor editor = preferences.edit();
        for (Map.Entry<String, String> entry : cachedBooks.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    private Map<String, String> getCachedBooks() {
        Map<String, String> cachedBooks = new HashMap<>();
        Map<String, ?> prefsMap = preferences.getAll();
        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String dataString = (String) value;
                cachedBooks.put(entry.getKey(), dataString);
            }
        }
        return cachedBooks;
    }
}

