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
			Contract.Feeds.UNREAD_ITEMS_COUNT + " integer, " +
			Contract.Feeds.STARRED_ITEMS_COUNT + " integer," +
			Contract.Feeds.FILENAME_PREFIX + " text" +
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
			Contract.FeedItems.DOWNLOADED + " integer, " +
			Contract.FeedItems.IS_READ + " integer, " +
			Contract.FeedItems.DURATION + " integer, " +
			Contract.FeedItems.ELAPSED + " integer, " +
			Contract.FeedItems.IS_STARRED + " integer, " +
			"foreign key(" + Contract.FeedItems.FEED_ID + ") references " + Contract.TABLE_FEEDS + "(" + Contract.Feeds._ID + ")" +
			")";
		db.execSQL(sql);

		createIndices(db);
	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			String template = "alter table " + Contract.TABLE_FEED_ITEMS + " add column %s integer";
			String sql = String.format(template, Contract.FeedItems.DOWNLOADED);
			db.execSQL(sql);
			sql = String.format(template, Contract.FeedItems.ELAPSED);
			db.execSQL(sql);
			sql = String.format(template, Contract.FeedItems.IS_STARRED);
			db.execSQL(sql);
		}
		if (oldVersion < 3) {
			String template = "alter table " + Contract.TABLE_FEEDS + " add column %s integer";
			String sql = String.format(template, Contract.Feeds.STARRED_ITEMS_COUNT);
			db.execSQL(sql);
		}
		if (oldVersion < 4) {
			createIndices(db);
		}
		if (oldVersion < 5) {
			String template = "alter table " + Contract.TABLE_FEEDS + " add column %s text";
			String sql = String.format(template, Contract.Feeds.FILENAME_PREFIX);
			db.execSQL(sql);
		}
	}



	private void createIndices(SQLiteDatabase db) {
		String sql = "create index " + Contract.TABLE_FEED_ITEMS + "_" + Contract.FeedItems.FEED_ID + "_idx" +
			" on " + Contract.TABLE_FEED_ITEMS + "(" + Contract.FeedItems.FEED_ID + ")";
		db.execSQL(sql);

		sql = "create index " + Contract.TABLE_FEED_ITEMS + "_" + Contract.FeedItems.GUID + "_idx" +
			" on " + Contract.TABLE_FEED_ITEMS + "(" + Contract.FeedItems.GUID + ")";
		db.execSQL(sql);

		sql = "create index " + Contract.TABLE_FEED_ITEMS + "_" + Contract.FeedItems.PUBLISHED + "_idx" +
			" on " + Contract.TABLE_FEED_ITEMS + "(" + Contract.FeedItems.PUBLISHED + ")";
		db.execSQL(sql);

		sql = "create index " + Contract.TABLE_FEED_ITEMS + "_" + Contract.FeedItems.IS_READ + "_idx" +
			" on " + Contract.TABLE_FEED_ITEMS + "(" + Contract.FeedItems.IS_READ + ")";
		db.execSQL(sql);

		sql = "create index " + Contract.TABLE_FEED_ITEMS + "_" + Contract.FeedItems.IS_STARRED + "_idx" +
			" on " + Contract.TABLE_FEED_ITEMS + "(" + Contract.FeedItems.IS_STARRED + ")";
		db.execSQL(sql);
	}
}
