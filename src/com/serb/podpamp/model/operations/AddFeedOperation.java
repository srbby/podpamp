package com.serb.podpamp.model.operations;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;
import com.serb.podpamp.model.domain.FeedUrlsContainer;
import com.serb.podpamp.model.managers.DownloadManager;
import com.serb.podpamp.model.managers.FeedsManager;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.model.request.RequestFactory;
import com.serb.podpamp.utils.Utils;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class AddFeedOperation implements RequestService.Operation {

	@Override
	public Bundle execute(Context context, Request request)
		throws ConnectionException, DataException, CustomRequestException {

		FeedUrlsContainer container = (FeedUrlsContainer) request.getParcelable(RequestFactory.FEED_URLS);

		for (String url : container.getUrls()) {
			if (FeedsManager.isFeedAdded(context, url))
				continue;

			RSSReader reader = new RSSReader();

			try {
				RSSFeed rss_feed = reader.load(url);
				List<RSSItem> items = rss_feed.getItems();

				int unreadCount = Utils.getNewFeedKeepUnreadCount(context);

				ContentValues values = new ContentValues();
				values.put(Contract.FeedsColumns.TITLE, rss_feed.getTitle().trim());

				String subtitle = rss_feed.getSubtitle();
				if (TextUtils.isEmpty(subtitle))
					subtitle = rss_feed.getDescription();
				values.put(Contract.FeedsColumns.SUBTITLE, subtitle.trim());

				Uri iconUri = rss_feed.getIconUrl();
				if (iconUri != null)
				{
					values.put(Contract.FeedsColumns.ICON_URL, iconUri.toString());
					values.put(Contract.FeedsColumns.ICON, DownloadManager.downloadImage(iconUri.toString()));
				}

				values.put(Contract.FeedsColumns.URL, url);
				values.put(Contract.FeedsColumns.UNREAD_ITEMS_COUNT, items.size() < unreadCount ? items.size() : unreadCount);

				values.put(Contract.FeedsColumns.FILENAME_PREFIX, getFilenamePrefix(url));

				Uri feedUri = context.getContentResolver().insert(Contract.Feeds.CONTENT_URI, values);

				long feedId = ContentUris.parseId(feedUri);
				for (RSSItem item : items) {
					FeedsManager.addFeedItem(context, feedId, item, unreadCount <= 0);
					unreadCount--;
				}
			} catch (RSSReaderException e) {
				e.printStackTrace();
			}
		}

		return null;
	}



	private String getFilenamePrefix(String urlString) {
		String TAG = "AddFeedOperation";
		try {
			URL url = new URL(urlString);
			String host = url.getHost();
			return host.split("\\.")[0];
		} catch (MalformedURLException e) {
			Log.e(TAG, String.format("Invalid feed url %s %n %s", urlString, e.toString()));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return null;
	}
}
