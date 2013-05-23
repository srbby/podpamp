package com.serb.podpamp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.serb.podpamp.R;

public class MainActivity extends Activity implements View.OnClickListener {
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		findViewById(R.id.btn_feeds).setOnClickListener(this);
	}



	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_feeds:
				showFeedsList();
				break;
		}
	}



	//region Private Methods.

	private void showFeedsList()
	{
		Intent intent = new Intent(this, FeedsActivity.class);
		startActivity(intent);
	}

	//endregion
}
