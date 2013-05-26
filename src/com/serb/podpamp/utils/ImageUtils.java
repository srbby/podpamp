package com.serb.podpamp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public abstract class ImageUtils {
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
			Log.d("ImageUtils", "Error: " + e.toString());
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
}
