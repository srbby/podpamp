package com.serb.podpamp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public abstract class Utils {
	private static final HashMap<Long, Bitmap> feedIconsMap = new HashMap<Long, Bitmap>();



	public static void setImageView(ImageView image_view, byte[] image, int alt_image)
	{
		Bitmap bitmap = createBitmap(image, true);
		if (bitmap != null) {
			image_view.setImageBitmap(bitmap);
		}
		else if (alt_image > -1) {
			image_view.setImageResource(alt_image);
		}
	}



	public static void setImageView(Context context, ImageView imageView, long feed_id, int alt_image) {
		if (!feedIconsMap.containsKey(feed_id))
			putFeedIcon(context, feed_id);

		if (feedIconsMap.containsKey(feed_id))
			imageView.setImageBitmap(feedIconsMap.get(feed_id));
		else
			setImageView(imageView, null, alt_image);
	}



	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
			.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, pixels, pixels, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
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
			return new SimpleDateFormat("MMM dd, yyyy").format(pub_date);
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
		return Integer.parseInt(settings.getString("pref_keep_unread_count", "2"));
	}



	public static boolean isInstantDownloadSet(Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getBoolean("pref_instant_download_on_refresh", true);
	}



	public static int getEpisodeKeepDays(Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.parseInt(settings.getString("pref_episode_keep_time", "14"));
	}



	public static void showConfirmationDialog(Context context, String title, String message, DialogInterface.OnClickListener okClickListener) {
		if (okClickListener == null)
			return;

		new AlertDialog.Builder(context)
			.setTitle(title)
			.setMessage(message)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.yes, okClickListener)
			.setNegativeButton(android.R.string.no, null).show();
	}



	public static byte[] compressImage(InputStream is)
	{
		Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), 156, 156, true);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}



	public static void putFeedIcon(long feedId, byte[] icon) {
		if (feedIconsMap.containsKey(feedId))
			return;
		Bitmap bitmap = createBitmap(icon, true);
		if (bitmap != null)
			feedIconsMap.put(feedId, bitmap);
	}

	//region Private Methods.

	private static void putFeedIcon(Context context, long feedId) {
		String[] projection = {
			Contract.Feeds.ICON
		};

		String selection = Contract.Feeds._ID + " = ?";
		String[] selectionArgs = { String.valueOf(feedId) };

		Cursor cursor = context.getContentResolver().query(Contract.Feeds.CONTENT_URI,
			projection, selection, selectionArgs, null);

		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				byte[] icon = cursor.getBlob(cursor.getColumnIndex(Contract.Feeds.ICON));
				Bitmap bitmap = createBitmap(icon, true);
				if (bitmap != null)
					feedIconsMap.put(feedId, bitmap);
			}
			cursor.close();
		}
	}



	private static Bitmap createBitmap(byte[] image, boolean roundCorners) {
		if (image != null && image.length > 0) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
			if (roundCorners)
				bitmap = getRoundedCornerBitmap(bitmap, 12);
			return bitmap;
		}
		return null;
	}


	//endregion
}
