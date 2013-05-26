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
import org.mcsoxford.rss.*;

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
			values.put(Contract.FeedsColumns.ICON, rss_feed.getIconUrl().toString());
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
		values.put(Contract.FeedItemsColumns.DESC, item.getSummary());

		final Enclosure enclosure = item.getEnclosure();
		if (enclosure != null)
		{
			values.put(Contract.FeedItemsColumns.MEDIA_URL, enclosure.getUrl().toString());
			values.put(Contract.FeedItemsColumns.LENGTH, enclosure.getLength());
		}

		values.put(Contract.FeedItemsColumns.IS_READ, false);
		values.put(Contract.FeedItemsColumns.PUBLISHED, item.getPubDate().getTime());

		context.getContentResolver().insert(Contract.FeedItems.CONTENT_URI, values);
	}
}
