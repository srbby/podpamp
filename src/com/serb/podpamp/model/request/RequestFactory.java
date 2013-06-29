package com.serb.podpamp.model.request;

import com.foxykeep.datadroid.requestmanager.Request;

public class RequestFactory {
	public static final int REQUEST_ADD_FEED = 1;
	public static final int REQUEST_DOWNLOAD_EPISODE = 2;
	public static final int REQUEST_REFRESH_FEEDS = 3;
	public static final int REQUEST_DOWNLOAD_NEW_EPISODES = 4;

	public static final String FEED_URL = "feed_url";
	public static final String FEED_ITEM_ID = "feed_item_id";


	private RequestFactory() {
	}



	public static Request getAddFeedRequest(String feed_url) {
		Request request = new Request(REQUEST_ADD_FEED);
		request.put(FEED_URL, feed_url);
		return request;
	}



	public static Request getDownloadEpisodeRequest(long feed_item_id) {
		Request request = new Request(REQUEST_DOWNLOAD_EPISODE);
		request.put(FEED_ITEM_ID, feed_item_id);
		return request;
	}



	public static Request getRefreshFeedsRequest() {
		return new Request(REQUEST_REFRESH_FEEDS);
	}



	public static Request getDownloadNewEpisodesRequest() {
		return new Request(REQUEST_DOWNLOAD_NEW_EPISODES);
	}
}
