package com.serb.podpamp.utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import com.serb.podpamp.model.domain.FeedItem;
import com.serb.podpamp.model.managers.FeedsManager;
import com.serb.podpamp.model.managers.PlaylistManager;

import java.util.ArrayList;
import java.util.Queue;

public class PlayerService extends Service {
	private static final String ACTION_API_COMMAND = "com.maxmpz.audioplayer.API_COMMAND";
	private static final String ACTION_STATUS_CHANGED = "com.maxmpz.audioplayer.STATUS_CHANGED";
	private static final String COMMAND = "cmd";
	private static final String STATUS = "status";
	private static final String PAUSED = "paused";
	//private static final String FAILED = "failed";
	private static final String POSITION = "pos";

	private static final int TRACK_PLAYING = 1;
	private static final int TRACK_ENDED = 2;
	private static final int PLAYING_ENDED = 3;

	private static final int TOGGLE_PLAY_PAUSE = 1;
	private static final int PLAY_COMMAND = 20;
	private static final int PAUSE_COMMAND = 2;

	// Binder given to clients
	private final IBinder binder = new LocalBinder();

	private boolean playing;
	private static boolean paused;

	private final ArrayList<PlayerListener> clients = new ArrayList<PlayerListener>();

	private Intent statusIntent;

	private long itemId;

	private Queue<FeedItem> playlist;

	private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		statusIntent = intent;
		updateStatusUI();
		}
	};

	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public PlayerService getService() {
			// Return this instance of LocalService so clients can call public methods
			return PlayerService.this;
		}
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.e("PLAYER_SERVICE", "onStartCommand");
		// If we get killed, after returning from here, don't restart
		return START_NOT_STICKY;
	}



	@Override
	public IBinder onBind(Intent intent) {
		//Log.e("PLAYER_SERVICE", "onBind");
		return binder;
	}



	@Override
	public void onCreate() {
		//Log.e("PLAYER_SERVICE", "onCreate");
		registerAndLoadStatus();
	}



	@Override
	public void onDestroy() {
		//Log.e("PLAYER_SERVICE", "onDestroy");
		unregister();
	}

		//region Public Methods.

	public boolean isPlaying() {
		return playing;
	}



	public void play(long id, String filePath, int position) {
		//Log.e("PLAYER_SERVICE", "PLAY");
		if (id < 0 || TextUtils.isEmpty(filePath))
			return;

		if (itemId == id && paused) {
			startService(new Intent(ACTION_API_COMMAND).putExtra(COMMAND, TOGGLE_PLAY_PAUSE));
			return;
		}

		itemId = id;

		if (playlist == null || playlist.size() == 0)
			playlist = PlaylistManager.getPlaylist(this, itemId);

		Intent intent = new Intent(ACTION_API_COMMAND)
			.putExtra(COMMAND, PLAY_COMMAND)
			.setData(Uri.parse("file://" + filePath));

		if (position > 0)
			intent.putExtra(POSITION, position);

		startService(intent);
	}



	public void pause() {
		//Log.e("PLAYER_SERVICE", "PAUSE");
		startService(new Intent(ACTION_API_COMMAND).putExtra(COMMAND, PAUSE_COMMAND));
	}



	public void playNext() {
		//Log.e("PLAYER_SERVICE", "PLAY_NEXT");
		if (playlist != null && playlist.size() > 0)
		{
			FeedItem item = playlist.poll();

			play(item.getId(), item.getFilePath(), item.getElapsed());

			for (PlayerListener client : clients) {
				client.onPlayNext(item.getId());
			}
		}
		else
			if (clients.size() == 0)
			{
				//Log.e("PLAYER_SERVICE", "--STOP_SELF");
				stop();
			}
	}



	public void registerClient(PlayerListener listener) {
		//Log.e("PLAYER_SERVICE", "REGISTER_CLIENT");
		if (!clients.contains(listener))
			clients.add(listener);
	}



	public void unregisterClient(PlayerListener listener) {
		//Log.e("PLAYER_SERVICE", "UNREGISTER_CLIENT");
		clients.remove(listener);
		if (!playing && clients.size() == 0)
		{
			//Log.e("PLAYER_SERVICE", "--STOP_SELF");
			stop();
		}
	}

	//endregion

	//region Private Methods.

	private void registerAndLoadStatus() {
		//Log.e("PLAYER_SERVICE", "REGISTER");
		statusIntent = registerReceiver(statusReceiver, new IntentFilter(ACTION_STATUS_CHANGED));
	}



	private void unregister() {
		//Log.e("PLAYER_SERVICE", "UNREGISTER");
		if(statusReceiver != null) {
			try {
				unregisterReceiver(statusReceiver);
			} catch(Exception ignored){}
		}
	}



	private void updateStatusUI() {
		if(statusIntent != null) {
			int status = statusIntent.getIntExtra(STATUS, -1);

			switch(status) {
				case TRACK_PLAYING:
					paused = statusIntent.getBooleanExtra(PAUSED, false);
					if (paused)
					{
						//Log.e("PLAYER_SERVICE", "PAUSED");
						playing = false;
						int elapsed = statusIntent.getIntExtra(POSITION, -1);
						if (elapsed > 0)
						{
							FeedsManager.updateFeedItemElapsed(this, itemId, elapsed);
							for (PlayerListener client : clients) {
								client.onPaused(elapsed);
							}
						}
					}
					else
					{
						//Log.e("PLAYER_SERVICE", "PLAYING");
						playing = true;
						for (PlayerListener client : clients) {
							client.onResumed();
						}
					}
					break;
				case TRACK_ENDED:
					//Log.e("PLAYER_SERVICE", "TRACK_ENDED");
					if (!paused && playing)
					{
						//Log.e("PLAYER_SERVICE", "--TRACK_ENDED");
						playing = false;
						FeedsManager.updateFeedItemElapsed(this, itemId, 0);
						FeedsManager.markFeedItemAsReadOrUnread(this, itemId, true);

						for (PlayerListener client : clients) {
							client.onFinished();
						}

						playNext();
					}
					break;
				case PLAYING_ENDED:
					//Log.e("PLAYER_SERVICE", "PLAYING_ENDED");
					if (!paused && playing)
					{
						//Log.e("PLAYER_SERVICE", "--PLAYING_ENDED");
						playing = false;
						FeedsManager.updateFeedItemElapsed(this, itemId, 0);
						FeedsManager.markFeedItemAsReadOrUnread(this, itemId, true);

						for (PlayerListener client : clients) {
							client.onFinished();
						}

						playNext();
					}
					else if (paused)
					{
						if (clients.size() == 0)
						{
							//Log.e("PLAYER_SERVICE", "--STOP_SELF");
							stop();
						}
						paused = false;
					}
					break;
			}
		}
	}

	private void stop() {
		playing = false;
		paused = false;
		stopSelf();
	}

	//endregion

	//region Public Interfaces.

	public static interface PlayerListener {
		void onResumed();

		void onPaused(int elapsed);

		void onPlayNext(long itemId);

		void onFinished();
	}

	//endregion
}
