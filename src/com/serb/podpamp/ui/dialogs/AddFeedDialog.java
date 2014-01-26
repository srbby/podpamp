package com.serb.podpamp.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;
import com.serb.podpamp.R;

public class AddFeedDialog extends DialogFragment {
	public interface AddFeedDialogListener {
		public void onDialogPositiveClick(String feed_url);
	}

	AddFeedDialogListener mListener;

	public static final int FILE_SELECT_CODE = 4444;



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
			.setPositiveButton(R.string.add_feed_text, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					mListener.onDialogPositiveClick(String.valueOf(input.getText()));
				}
			})
			.setNegativeButton(R.string.cancel_btn_text, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
				}
			})
			.setNeutralButton(R.string.add_feeds_from_file_text, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					pickFile();
				}
			});
		return builder.create();
	}



	private void pickFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		try {
			getActivity().startActivityForResult(intent, FILE_SELECT_CODE);
		}
		catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getActivity(), getString(R.string.install_file_manager),
				Toast.LENGTH_SHORT).show();
		}
	}
}
