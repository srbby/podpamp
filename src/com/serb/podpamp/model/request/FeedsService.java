package com.serb.podpamp.model.request;

import com.foxykeep.datadroid.service.RequestService;
import com.serb.podpamp.model.operations.AddFeedOperation;
import com.serb.podpamp.model.operations.DownloadEpisodeOperation;

public class FeedsService extends RequestService {
	@Override
	public Operation getOperationForType(int requestType) {
		switch (requestType) {
			case RequestFactory.REQUEST_ADD_FEED:
				return new AddFeedOperation();
			case RequestFactory.REQUEST_DOWNLOAD_EPISODE:
				return new DownloadEpisodeOperation();
			default:
				return null;
		}
	}
}
