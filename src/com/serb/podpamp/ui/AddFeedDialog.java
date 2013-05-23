package com.serb.podpamp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;
import com.serb.podpamp.R;

public class AddFeedDialog extends DialogFragment {
	public interface AddFeedDialogListener {
		public void onDialogPositiveClick(String feed_url);
	}



	AddFeedDialogListener mListener;



	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Verify that the host activity implements the callback interface
		try {
			mListener = (AddFeedDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement AddFeedDialogListener");
		}
	}




	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final EditText input = new EditText(getActivity());
		input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
		input.setText("http://");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.add_feed_dialog_message)
			.setTitle(R.string.add_feed_dialog_title)
			.setView(input)
			.setPositiveButton(R.string.add_button_text, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					mListener.onDialogPositiveClick(String.valueOf(input.getText()));
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
