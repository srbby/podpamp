package com.serb.podpamp.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import com.serb.podpamp.utils.DownloadService;

public class DownloadActivity extends FragmentActivity {
	//region Private Members.

	protected DownloadService.DownloadingListener downloadingListener = new DownloadService.DownloadingListener() {
		@Override
		public void downloadCompleted(long feedItemId) {
			onDownloadCompleted(feedItemId);
		}

		@Override
		public void allDownloadsCompleted() {
			onAllDownloadsCompleted();
		}

		@Override
		public void downloadErrorOccurred(long feedItemId) {
			onDownloadErrorOccurred(feedItemId);
		}
	};

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection downloadServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
			downloadService = binder.getService();
			isDownloadServiceBound = true;
			downloadService.registerClient(downloadingListener);
			onDownloadServiceConnected();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			isDownloadServiceBound = false;
			onDownloadServiceDisconnected();
		}
	};

	//endregion

	//region Protected Members.

	protected DownloadService downloadService;
	protected boolean isDownloadServiceBound = false;

	//endregion

	//region Overridden Methods.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}



	@Override
	protected void onStart() {
		super.onStart();

		Intent downloaderIntent = new Intent(this, DownloadService.class);
		bindService(downloaderIntent, downloadServiceConnection, Context.BIND_AUTO_CREATE);
	}



	@Override
	protected void onStop() {
		super.onStop();

		if (isDownloadServiceBound) {
			downloadService.unregisterClient(downloadingListener);
			unbindService(downloadServiceConnection);
			isDownloadServiceBound = false;
		}
	}

	//endregion

	//region Protected Methods.

	protected void onDownloadCompleted(long feedItemId) {
	}



	protected void onAllDownloadsCompleted() {
	}



	protected void onDownloadErrorOccurred(long feedItemId) {
	}



	protected void onDownloadServiceConnected() {
	}



	protected void onDownloadServiceDisconnected() {
	}

	//endregion
}