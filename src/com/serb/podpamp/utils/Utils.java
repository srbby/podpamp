package com.serb.podpamp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Utils {
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
}
