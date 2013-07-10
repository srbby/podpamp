package com.serb.podpamp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

//todo Make an interface IPlayer. Use and instance of IPlayer instead of static members.
public class Player {
	//region Private Members.

	private static final String ACTION_API_COMMAND = "com.maxmpz.audioplayer.API_COMMAND";
	private static final String COMMAND = "cmd";
	private static final int PLAY_COMMAND = 20;
	private static final int RESUME_COMMAND = 3;
	private static final int PAUSE_COMMAND = 2;

	private static String playingFile;

	private static int playCommand = PLAY_COMMAND;

	//endregion

	//region Public Methods.

	public static boolean play(Context context, String filePath) {
		if (TextUtils.isEmpty(filePath))
			return false;

		if (filePath.equals(playingFile) && playCommand == RESUME_COMMAND)
		{
			context.startService(new Intent(ACTION_API_COMMAND).putExtra(COMMAND, playCommand));
		}
		else
		{
			playingFile = filePath;
			playCommand = PLAY_COMMAND;
			context.startService(new Intent(ACTION_API_COMMAND)
				.putExtra(COMMAND, playCommand)
				//.putExtra(PowerAMPiAPI.Track.POSITION, 10) // Play from 10th second.
				.setData(Uri.parse("file://" + filePath)));
		}
		return true;
	}



	public static boolean pause(Context context) {
		playCommand = RESUME_COMMAND;
		context.startService(new Intent(ACTION_API_COMMAND).putExtra(COMMAND, PAUSE_COMMAND));
		return true;
	}

	//endregion

	//region Private Methods.



	//endregion
}
