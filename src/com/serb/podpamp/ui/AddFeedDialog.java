package com.serb.podpamp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.serb.podpamp.R;

public class AddFeedDialog extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.add_feed_dialog_message)
			.setTitle(R.string.add_feed_dialog_title)
			.setPositiveButton(R.string.add_button_text, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {

				}
			})
			.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {

				}
			});
		return builder.create();
	}

}
