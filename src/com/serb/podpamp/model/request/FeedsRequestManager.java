package com.serb.podpamp.model.request;

import android.content.Context;
import com.foxykeep.datadroid.requestmanager.RequestManager;

public class FeedsRequestManager extends RequestManager {
	private static FeedsRequestManager sInstance;



	private FeedsRequestManager(Context context) {
		super(context, FeedsService.class);
	}



	public static FeedsRequestManager from(Context context) {
		if (sInstance == null) {
			sInstance = new FeedsRequestManager(context);
		}
		return sInstance;
	}
}
