package com.serb.podpamp.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import com.serb.podpamp.R;

public class FeedsActivity extends FragmentActivity implements View.OnClickListener,
		AddFeedDialog.AddFeedDialogListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feeds);

		findViewById(R.id.btn_add_feed).setOnClickListener(this);
	}



	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_add_feed:
				showAddFeed();
				break;
		}
	}



	@Override
	public void onDialogPositiveClick(String feed_url) {
		if (feed_url.length() > 0 && feed_url.startsWith("http"))
			addFeed(feed_url);
	}

	//region Private Methods.

	private void showAddFeed() {
		DialogFragment newFragment = new AddFeedDialog();
		newFragment.show(getSupportFragmentManager(), "add_feed");
	}



	private void addFeed(String feed_url) {
		((TextView)findViewById(R.id.txt_greet)).setText(feed_url);
	}

	//endregion
}