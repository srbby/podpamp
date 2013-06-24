package com.serb.podpamp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public abstract class Utils {
	private static final HashMap<Long, byte[]> feed_icons_map = new HashMap<Long, byte[]>();



	public static byte[] downloadImage(String url) {
		try {
			if (url == null || url.length() == 0)
				return null;

			URL imageUrl = new URL(url);
			URLConnection conn = imageUrl.openConnection();
			InputStream is = conn.getInputStream();

			Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), 128, 128, true);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			return stream.toByteArray();
		} catch (Exception e) {
			Log.d("Utils", "Error: " + e.toString());
		}
		return null;
	}



	public static void setImageView(ImageView image_view, byte[] image, int alt_image)
	{
		if (image != null && image.length > 0) {
			Bitmap b = BitmapFactory.decodeByteArray(image, 0, image.length);
			image_view.setImageBitmap(b);
		}
		else if (alt_image > -1) {
			image_view.setImageResource(alt_image);
		}
	}



	public static void setImageView(Context context, ImageView imageView, long feed_id, int alt_image) {
		if (!feed_icons_map.containsKey(feed_id))
			putFeedIcon(context, feed_id);

		if (feed_icons_map.containsKey(feed_id))
			setImageView(imageView, feed_icons_map.get(feed_id), alt_image);
		else
			setImageView(imageView, null, alt_image);
	}



	public static String getFileSizeText(double size_in_bytes) {
		if (size_in_bytes > 0)
		{
			DecimalFormat form = new DecimalFormat("#.##");
			if (size_in_bytes >= 1048576)
				return form.format(size_in_bytes / 1048576) + " MB";
			return form.format(size_in_bytes / 1024) + " KB";
		}
		return "";
	}



	public static String getDateText(long date_in_milliseconds) {
		if (date_in_milliseconds > 0)
		{
			Date pub_date = new Date(date_in_milliseconds);
			return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(pub_date);
		}
		return "";
	}



	public static boolean isNetworkAvailable(Context context, boolean respectWifiSettings) {
		final ConnectivityManager conMgr =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();

		boolean result;
		if (activeNetwork != null && activeNetwork.isConnected()) {
			if (respectWifiSettings)
			{
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
				boolean isWiFiOnly = settings.getBoolean("pref_conn_type", true);
				result = !isWiFiOnly || activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
			}
			else
				result = true;
		} else {
			result = false;
		}

		if (!result)
			Toast.makeText(context, R.string.no_connection, Toast.LENGTH_LONG).show();

		return result;
	}



	public static String getDownloadFolder() {
		return "/sdcard/download/podpamp/";
	}



	public static int getNewFeedKeepUnreadCount(Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getInt("pref_keep_unread_count", 2);
	}

	//region Private Methods.

	private static void putFeedIcon(Context context, long feed_id) {
		String[] projection = {
			Contract.Feeds.ICON
		};

		String selection = Contract.Feeds._ID + " = ?";
		String[] selectionArgs = { String.valueOf(feed_id) };

		Cursor cursor = context.getContentResolver().query(Contract.Feeds.CONTENT_URI,
			projection, selection, selectionArgs, null);

		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				byte[] icon = cursor.getBlob(cursor.getColumnIndex(Contract.Feeds.ICON));
				if (icon != null)
					feed_icons_map.put(feed_id, icon);
			}
			cursor.close();
		}
	}

	//endregion
}
