package com.serb.podpamp.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.serb.podpamp.R;

public class FeedsActivity extends FragmentActivity implements View.OnClickListener {
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


	//region Private Methods.

	private void showAddFeed() {
		DialogFragment newFragment = new AddFeedDialog();
		newFragment.show(getSupportFragmentManager(), "add_feed");
	}

	//endregion
}