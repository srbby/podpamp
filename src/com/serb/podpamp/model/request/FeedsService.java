package com.serb.podpamp.model.request;

import com.foxykeep.datadroid.service.RequestService;
import com.serb.podpamp.model.operations.*;

public class FeedsService extends RequestService {
	@Override
	public Operation getOperationForType(int requestType) {
		switch (requestType) {
			case RequestFactory.REQUEST_ADD_FEEDS:
				return new AddFeedOperation();
			case RequestFactory.REQUEST_DOWNLOAD_EPISODE:
				return new DownloadEpisodeOperation();
			case RequestFactory.REQUEST_REFRESH_FEEDS:
				return new RefreshFeedsOperation();
			case RequestFactory.REQUEST_CLEANUP:
				return new CleanUpOperation();
			default:
				return null;
		}
	}
}
