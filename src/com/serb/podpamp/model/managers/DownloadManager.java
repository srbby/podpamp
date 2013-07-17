package com.serb.podpamp.model.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.foxykeep.datadroid.exception.DataException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager {
	//region Private Members.

	private static final int BUFFER_SIZE = 1024;
	private static final int UPDATE_PERIOD = 512;

	//endregion

	//region Public Interfaces.

	public static interface OnProgressUpdateListener {
		public void updateProgress(EpisodeMetadata metadata);
	}

	//endregion

	//region Public Methods.

	public static byte[] downloadImage(String url) {
		try {
			if (url == null || url.length() == 0)
				return null;

			URLConnection conn = getConnection(new URL(url));
			if (conn != null)
			{
				InputStream is = conn.getInputStream();

				Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), 128, 128, true);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
				return stream.toByteArray();
			}
		} catch (Exception e) {
			Log.d("Utils", "Error: " + e.toString());
		}
		return null;
	}



	public static void downloadEpisode(EpisodeMetadata metadata, OnProgressUpdateListener progressUpdateListener) {
		try {
			URL url = new URL(metadata.url);
			URLConnection conn = getConnection(url);
			if (conn != null)
			{
				// this will be useful so that you can show a typical 0-100% progress bar
				metadata.size = conn.getContentLength();

				// download the file
				InputStream input = new BufferedInputStream(url.openStream());

				if (metadata.file.createNewFile())
				{
					OutputStream output = new FileOutputStream(metadata.file);

					byte data[] = new byte[BUFFER_SIZE];
					long total = 0;
					int count;
					while ((count = input.read(data)) != -1) {
						total += count;
						// publishing the progress....
						//	Bundle resultData = new Bundle();
						//	resultData.putInt("progress" ,(int) (total * 100 / fileLength));
						//	receiver.send(UPDATE_PROGRESS, resultData);
						output.write(data, 0, count);

						if (progressUpdateListener != null && (count % UPDATE_PERIOD == 0))
						{
							metadata.downloaded = total;
							progressUpdateListener.updateProgress(metadata);
						}
					}

					metadata.downloaded = metadata.size;

					output.flush();
					output.close();
					input.close();
				}
				else
				{
					throw new DataException("Unable to create a file: " + metadata.fileName);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DataException e) {
			e.printStackTrace();
		}
	}

	//endregion

	//region Private Methods.

	private static URLConnection getConnection(URL url) {
		URLConnection conn = null;
		try {
			conn = url.openConnection();
			conn.setReadTimeout(10000); // milliseconds
			conn.setConnectTimeout(15000); // milliseconds
			conn.connect();
		} catch (IOException e) {
			Log.d("DownloadManager", "Error: " + e.toString());
		}
		return conn;
	}

	//endregion
}
