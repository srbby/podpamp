package com.serb.podpamp.model.operations;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.model.request.RequestFactory;

public class AddFeedOperation implements RequestService.Operation {
	@Override
	public Bundle execute(Context context, Request request)
		throws ConnectionException, DataException, CustomRequestException {
		String url = request.getString(RequestFactory.FEED_URL);
		ContentValues feed = new ContentValues();
		feed.put(Contract.FeedsColumns.TITLE, "Test Title");
		feed.put(Contract.FeedsColumns.URL, url);
		feed.put(Contract.FeedsColumns.NEW_ITEMS_COUNT, 10);

		context.getContentResolver().insert(Contract.Feeds.CONTENT_URI, feed);
		return null;
	}
}
