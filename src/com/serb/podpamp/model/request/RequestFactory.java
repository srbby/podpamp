package com.serb.podpamp.model.request;

import com.foxykeep.datadroid.requestmanager.Request;

public class RequestFactory {
	public static final int REQUEST_ADD_FEED = 1;

	public static final String FEED_URL = "feed_url";


	private RequestFactory() {
	}



	public static Request getAddFeedRequest(String feed_url) {
		Request request = new Request(REQUEST_ADD_FEED);
		request.put(FEED_URL, feed_url);
		return request;
	}
}
