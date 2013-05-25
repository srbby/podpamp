package com.serb.podpamp.model.operations;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.model.request.RequestFactory;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.util.List;

public class AddFeedOperation implements RequestService.Operation {
	@Override
	public Bundle execute(Context context, Request request)
		throws ConnectionException, DataException, CustomRequestException {
		String url = request.getString(RequestFactory.FEED_URL);

		RSSReader reader = new RSSReader();

		try {
			RSSFeed rss_feed = reader.load(url);
			List<RSSItem> items = rss_feed.getItems();

			ContentValues values = new ContentValues();
			values.put(Contract.FeedsColumns.TITLE, rss_feed.getTitle());
			values.put(Contract.FeedsColumns.URL, url);
			values.put(Contract.FeedsColumns.NEW_ITEMS_COUNT, items.size());

			Uri feedUri = context.getContentResolver().insert(Contract.Feeds.CONTENT_URI, values);

			long feedId = ContentUris.parseId(feedUri);
			for (RSSItem item : items) {
				addFeedItem(context, feedId, item);
			}
		} catch (RSSReaderException e) {
			e.printStackTrace();
		}
		return null;
	}



	private void addFeedItem(Context context, long feedId, RSSItem item) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.FEED_ID, feedId);
		values.put(Contract.FeedItemsColumns.TITLE, item.getTitle());
		values.put(Contract.FeedItemsColumns.DESC, item.getDescription());
		values.put(Contract.FeedItemsColumns.MEDIA_URL, item.getLink().toString());
		values.put(Contract.FeedItemsColumns.IS_READ, false);
		values.put(Contract.FeedItemsColumns.PUBLISHED, item.getPubDate().getTime());

		context.getContentResolver().insert(Contract.FeedItems.CONTENT_URI, values);
	}
}
