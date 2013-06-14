package com.serb.podpamp.ui.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.serb.podpamp.R;

public class SettingsActivity extends PreferenceActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}