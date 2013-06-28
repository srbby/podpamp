package com.serb.podpamp.model.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + Contract.TABLE_FEEDS + " (" +
			Contract.Feeds._ID + " integer primary key autoincrement, " +
			Contract.Feeds.URL + " text, " +
			Contract.Feeds.TITLE + " text, " +
			Contract.Feeds.SUBTITLE + " text, " +
			Contract.Feeds.ICON_URL + " text, " +
			Contract.Feeds.ICON + " blob, " +
			Contract.Feeds.NEW_ITEMS_COUNT + " integer" +
			")";
		db.execSQL(sql);

		sql = "create table " + Contract.TABLE_FEED_ITEMS + " (" +
			Contract.FeedItems._ID + " integer primary key autoincrement, " +
			Contract.FeedItems.FEED_ID + " integer, " +
			Contract.FeedItems.GUID + " text, " +
			Contract.FeedItems.PUBLISHED + " integer, " +
			Contract.FeedItems.TITLE + " text, " +
			Contract.FeedItems.DESC + " text, " +
			Contract.FeedItems.MEDIA_URL + " text, " +
			Contract.FeedItems.FILE_PATH + " text, " +
			Contract.FeedItems.SIZE + " integer, " +
			Contract.FeedItems.IS_READ + " integer, " +
			Contract.FeedItems.DURATION + " integer" +
			")";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
