package com.serb.podpamp.model.request;

import com.foxykeep.datadroid.service.RequestService;
import com.serb.podpamp.model.operations.AddFeedOperation;

public class FeedsService extends RequestService {
	@Override
	public Operation getOperationForType(int requestType) {
		switch (requestType) {
			case RequestFactory.REQUEST_ADD_FEED:
				return new AddFeedOperation();
			default:
				return null;
		}
	}
}
