package com.serb.podpamp.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.serb.podpamp.R;
import com.serb.podpamp.model.managers.EpisodeMetadata;
import com.serb.podpamp.model.request.FeedsRequestManager;
import com.serb.podpamp.model.request.RequestFactory;
import com.serb.podpamp.ui.activities.MainActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

public class DownloadService extends Service {

	//region Private Members.

	private static final int NOTIFICATION_ID = 43287;

	// Binder given to clients
	private final IBinder binder = new LocalBinder();

	private final ArrayList<DownloadingListener> clients = new ArrayList<DownloadingListener>();

	private final ArrayList<Long> downloads = new ArrayList<Long>();
	private int totalDownloads;
	private int downloadedCount;

	private FeedsRequestManager requestManager = FeedsRequestManager.from(this);

	RequestManager.RequestListener requestListener = new RequestManager.RequestListener() {
		@Override
		public void onRequestFinished(Request request, Bundle resultData) {
			downloadedCount++;

			//Toast.makeText(context, getString(R.string.download_complete), Toast.LENGTH_LONG).show();
			long feedItemId = request.getLong(RequestFactory.FEED_ITEM_ID);

			downloads.remove(feedItemId);

			for (DownloadingListener client : clients) {
				client.downloadCompleted(feedItemId);
			}

			checkAllDownloadsCompleted();
		}

		@Override
		public void onRequestDataError(Request request) {
			showError(request.getLong(RequestFactory.FEED_ITEM_ID));
		}

		@Override
		public void onRequestCustomError(Request request, Bundle resultData) {
			showError(request.getLong(RequestFactory.FEED_ITEM_ID));
		}

		@Override
		public void onRequestConnectionError(Request request, int statusCode) {
			showError(request.getLong(RequestFactory.FEED_ITEM_ID));
		}

		void showError(long feedItemId) {
			downloadedCount++;
			downloads.remove(feedItemId);

			for (DownloadingListener client : clients) {
				client.downloadErrorOccurred(feedItemId);
			}

			/*String message = String.format("%s: %s", getString(R.string.download_failed),
				FeedsManager.getEpisodeMetadata(DownloadService.this, feedItemId).title);
			AlertDialog.Builder builder = new AlertDialog.Builder(request.context);
			builder.setTitle(android.R.string.dialog_alert_title)
				.setMessage(message)
				.create()
				.show();*/

			checkAllDownloadsCompleted();
		}

		void checkAllDownloadsCompleted() {
			if (downloads.size() == 0) {
				totalDownloads = 0;
				downloadedCount = 0;
				for (DownloadingListener client : clients) {
					client.allDownloadsCompleted();
				}
			}
		}
	};

	//endregion

	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public DownloadService getService() {
			// Return this instance of LocalService so clients can call public methods
			return DownloadService.this;
		}
	}

	//region Overridden Methods.

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// If we get killed, after returning from here, don't restart
		return START_NOT_STICKY;
	}



	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}



	@Override
	public void onDestroy() {
		//Log.e("DOWNLOAD_SERVICE", "onDestroy");
	}

	//endregion

	//region Public Methods.

	public boolean download(long feedItemId) {
		if (feedItemId > -1 && Utils.isNetworkAvailable(this, true)) {
			if (!downloads.contains(feedItemId)) {
				totalDownloads++;
				downloads.add(feedItemId);
				requestManager.execute(RequestFactory.getDownloadEpisodeRequest(feedItemId), requestListener);
			}
			return true;
		}
		return false;
	}



	public boolean downloadNewEpisodes(Collection<Long> items) {
		if (items != null && items.size() > 0 && Utils.isNetworkAvailable(this, true)) {
			for (Long feedItemId : items) {
				if (!downloads.contains(feedItemId))
				{
					totalDownloads++;
					downloads.add(feedItemId);
					requestManager.execute(RequestFactory.getDownloadEpisodeRequest(feedItemId), requestListener);
				}
			}
			return true;
		}
		return false;
	}



	public void updateDownloadProgress(Context context, EpisodeMetadata metadata) {
		final NotificationManager notificationManager =
			(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		final NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context)
			.setContentTitle(String.format("(%d/%d) %s", downloadedCount + 1, totalDownloads, metadata.title))
			.setContentText("0%")
			.setSmallIcon(R.drawable.icon_download_gray)
			.setAutoCancel(true);

		Intent intent = new Intent(context, MainActivity.class);
		// Sets the Activity to start in a new, empty task
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// Creates the PendingIntent
		PendingIntent notifyIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Puts the PendingIntent into the notification builder
		notifyBuilder.setContentIntent(notifyIntent);

		if (metadata.downloaded > 0) {
			NumberFormat percentFormat = NumberFormat.getPercentInstance();
			percentFormat.setMaximumFractionDigits(1);
			String percent = percentFormat.format((float)metadata.downloaded / (float)metadata.size) +
				" of " + Utils.getFileSizeText(metadata.size);
			notifyBuilder.setContentText(percent);
		}

		/*if (metadata.downloaded == metadata.size)
			Toast.makeText(context,
			String.format("%s: %s", getString(R.string.download_complete), metadata.title), Toast.LENGTH_LONG).show();*/

		notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
	}



	public boolean isDownloading(long feedItemId) {
		return downloads.contains(feedItemId);
	}



	public boolean isDownloading() {
		return downloads.size() > 0;
	}



	public void registerClient(DownloadingListener listener) {
		if (!clients.contains(listener))
			clients.add(listener);
	}



	public void unregisterClient(DownloadingListener listener) {
		clients.remove(listener);
		if (downloads.size() == 0 && clients.size() == 0)
		{
			stopSelf();
		}
	}

	//endregion

	//region Public Interfaces.

	public static interface DownloadingListener {
		void downloadCompleted(long feedItemId);

		void allDownloadsCompleted();

		void downloadErrorOccurred(long feedItemId);
	}

	//endregion
}
