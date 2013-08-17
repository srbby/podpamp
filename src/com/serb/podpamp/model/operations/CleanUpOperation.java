package com.serb.podpamp.model.operations;

import android.content.Context;
import android.os.Bundle;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;
import com.serb.podpamp.model.managers.FeedsManager;

public class CleanUpOperation implements RequestService.Operation {
	@Override
	public Bundle execute(Context context, Request request)
		throws ConnectionException, DataException, CustomRequestException {

		int count = FeedsManager.cleanUp(context);
		Bundle result = new Bundle();
		result.putInt("count", count);
		return result;
	}
}
