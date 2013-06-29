package com.serb.podpamp.model.operations;

import android.content.Context;
import android.os.Bundle;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;
import com.serb.podpamp.model.managers.FeedsManager;
import com.serb.podpamp.model.request.RequestFactory;

public class DownloadEpisodeOperation implements RequestService.Operation {
	@Override
	public Bundle execute(Context context, Request request)
		throws ConnectionException, DataException, CustomRequestException {

		long feedItemId = request.getLong(RequestFactory.FEED_ITEM_ID);
		FeedsManager.downloadEpisode(context, feedItemId);

		return null;
	}
}
