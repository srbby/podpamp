package com.serb.podpamp.model.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class FeedsProvider extends ContentProvider {
	private DatabaseHelper mDBHelper;

	public static final String DB_NAME = Contract.TABLE_FEEDS + ".db";
	public static final int DB_VERSION = 5;

	private static final UriMatcher sUriMatcher;

	private static final int PATH_ROOT = 0;
	private static final int PATH_FEEDS = 1;
	private static final int PATH_FEED_ITEMS = 2;

	static {
		sUriMatcher = new UriMatcher(PATH_ROOT);
		sUriMatcher.addURI(Contract.AUTHORITY, Contract.Feeds.CONTENT_PATH, PATH_FEEDS);
		sUriMatcher.addURI(Contract.AUTHORITY, Contract.FeedItems.CONTENT_PATH, PATH_FEED_ITEMS);
	}

	@Override
	public boolean onCreate() {
		mDBHelper = new DatabaseHelper(getContext(), DB_NAME, null, DB_VERSION);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (sUriMatcher.match(uri)) {
			case PATH_FEEDS: {
				Cursor cursor = mDBHelper.getReadableDatabase().query(Contract.TABLE_FEEDS,
					projection, selection, selectionArgs, null, null, sortOrder);
				cursor.setNotificationUri(getContext().getContentResolver(), Contract.Feeds.CONTENT_URI);
				return cursor;
			}
			case PATH_FEED_ITEMS: {
				Cursor cursor = mDBHelper.getReadableDatabase().query(Contract.TABLE_FEED_ITEMS,
					projection, selection, selectionArgs, null, null, sortOrder);
				cursor.setNotificationUri(getContext().getContentResolver(), Contract.FeedItems.CONTENT_URI);
				return cursor;
			}
			default:
				return null;
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case PATH_FEEDS:
				return Contract.Feeds.CONTENT_TYPE;
			case PATH_FEED_ITEMS:
				return Contract.FeedItems.CONTENT_TYPE;
			default:
				return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		switch (sUriMatcher.match(uri)) {
			case PATH_FEEDS: {
				long id = mDBHelper.getWritableDatabase().insert(Contract.TABLE_FEEDS, null, contentValues);
				getContext().getContentResolver().notifyChange(Contract.Feeds.CONTENT_URI, null);
				return ContentUris.withAppendedId(Contract.Feeds.CONTENT_URI, id);
			}
			case PATH_FEED_ITEMS: {
				long id = mDBHelper.getWritableDatabase().insert(Contract.TABLE_FEED_ITEMS, null, contentValues);
				getContext().getContentResolver().notifyChange(Contract.FeedItems.CONTENT_URI, null);
				return ContentUris.withAppendedId(Contract.FeedItems.CONTENT_URI, id);
			}
			default:
				return null;
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
			case PATH_FEEDS: {
				int count = mDBHelper.getWritableDatabase().delete(Contract.TABLE_FEEDS, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(Contract.Feeds.CONTENT_URI, null);
				return count;
			}
			case PATH_FEED_ITEMS: {
				int count = mDBHelper.getWritableDatabase().delete(Contract.TABLE_FEED_ITEMS, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(Contract.FeedItems.CONTENT_URI, null);
				return count;
			}
			default:
				return 0;
		}
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
			case PATH_FEEDS: {
				int id = mDBHelper.getWritableDatabase().update(Contract.TABLE_FEEDS, contentValues, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(Contract.Feeds.CONTENT_URI, null);
				return id;
			}
			case PATH_FEED_ITEMS: {
				int id = mDBHelper.getWritableDatabase().update(Contract.TABLE_FEED_ITEMS, contentValues, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(Contract.FeedItems.CONTENT_URI, null);
				return id;
			}
			default:
				return 0;
		}
	}
}
