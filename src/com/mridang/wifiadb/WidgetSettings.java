package com.mridang.wifiadb;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/*
 * This class is the activity which contains the preferences
 */
public class WidgetSettings extends PreferenceActivity {

	/*
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setIcon(R.drawable.ic_dashclock);
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	/*
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		super.onPostCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

	}

}