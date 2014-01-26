package com.serb.podpamp.model.request;

import android.os.Parcelable;
import com.foxykeep.datadroid.requestmanager.Request;

public class RequestFactory {
	public static final int REQUEST_ADD_FEEDS = 1;
	public static final int REQUEST_DOWNLOAD_EPISODE = 2;
	public static final int REQUEST_REFRESH_FEEDS = 3;
	public static final int REQUEST_CLEANUP = 5;

	public static final String FEED_URLS = "feed_urls";
	public static final String FEED_ITEM_ID = "feed_item_id";


	private RequestFactory() {
	}



	public static Request getAddFeedRequest(Parcelable feedUrls) {
		Request request = new Request(REQUEST_ADD_FEEDS);
		request.put(FEED_URLS, feedUrls);
		return request;
	}



	public static Request getDownloadEpisodeRequest(long feedItemId) {
		Request request = new Request(REQUEST_DOWNLOAD_EPISODE);
		request.put(FEED_ITEM_ID, feedItemId);
		return request;
	}



	public static Request getRefreshFeedsRequest() {
		return new Request(REQUEST_REFRESH_FEEDS);
	}
}
