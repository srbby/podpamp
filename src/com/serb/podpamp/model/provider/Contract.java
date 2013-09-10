package com.serb.podpamp.model.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {
	public static final String AUTHORITY = "com.serb.podpamp";

	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	public static final String TABLE_FEEDS = "feeds";

	public static final String TABLE_FEED_ITEMS = "feed_items";

	public interface FeedsColumns {
		public static final String URL = "url";
		public static final String TITLE = "title";
		public static final String SUBTITLE = "subtitle";
		public static final String ICON_URL = "icon_url";
		public static final String ICON = "icon";
		public static final String UNREAD_ITEMS_COUNT = "new_items_count";
		public static final String STARRED_ITEMS_COUNT = "starred_items_count";
	}

	public interface FeedItemsColumns {
		public static final String FEED_ID = "feed_id";
		public static final String GUID = "guid";
		public static final String PUBLISHED = "published";
		public static final String TITLE = "title";
		public static final String DESC = "desc";
		public static final String MEDIA_URL = "media_url";
		public static final String FILE_PATH = "file_path";
		public static final String SIZE = "size";
		public static final String DOWNLOADED = "downloaded";
		public static final String IS_READ = "is_read";
		public static final String DURATION = "duration";
		public static final String ELAPSED = "elapsed";
		public static final String IS_STARRED = "is_starred";
	}

	public static final class Feeds implements BaseColumns, FeedsColumns {
		public static final String CONTENT_PATH = TABLE_FEEDS;
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_PATH);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + CONTENT_PATH;
	}

	public static final class FeedItems implements BaseColumns, FeedItemsColumns {
		public static final String CONTENT_PATH = TABLE_FEED_ITEMS;
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_PATH);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + CONTENT_PATH;
	}
}
