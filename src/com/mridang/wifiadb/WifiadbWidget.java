package com.mridang.wifiadb;

import java.util.Random;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

/*
 * This class is the main class that provides the widget
 */
public class WifiadbWidget extends DashClockExtension {

	/* This is the instance of the observer that handles the adb toggles */
	private DebuggingObserver objObserver;

	/*
	 * This class is the observer for monitoring the ADB toggle events
	 */
	private class DebuggingObserver extends ContentObserver {

		/*
		 * 
		 */
		public DebuggingObserver() {

			super(null);

		}

		/*
		 * @see android.database.ContentObserver#onChange(boolean)
		 */
		@Override
		public void onChange(boolean selfChange) {

			super.onChange(selfChange);
			onUpdateData(UPDATE_REASON_CONTENT_CHANGED);

		}

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onInitialize(boolean)
	 */
	@Override
	protected void onInitialize(boolean booReconnect) {

		super.onInitialize(booReconnect);

		if (objObserver != null) {

			try {

				Log.d("WifiadbWidget", "Unregistered any existing content observer");
				getApplicationContext().getContentResolver().unregisterContentObserver(objObserver);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		objObserver = new DebuggingObserver();
		getApplicationContext().getContentResolver().registerContentObserver(Settings.Secure.getUriFor("adb_port"), false, objObserver);
		Log.d("WifiadbWidget", "Registered the content observer");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onCreate()
	 */
	public void onCreate() {

		super.onCreate();
		Log.d("WifiadbWidget", "Created");
		BugSenseHandler.initAndStartSession(this, getString(R.string.bugsense));

	}

	/*
	 * @see
	 * com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData
	 * (int)
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onUpdateData(int intReason) {

		Log.d("WifiadbWidget", "Fetching phone owner information");
		ExtensionData edtInformation = new ExtensionData();
		setUpdateWhenScreenOn(false);

		try {

			Integer intPort = Settings.Secure.getInt(getContentResolver(), "adb_port", -1);

			Log.d("WifiadbWidget", "Checking if wireless debugging is enabled");
			if (intPort > 0) {

				Log.d("WifiadbWidget", "Wireless debugging is enabled");
				WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

				Log.d("WifiadbWidget", "Checking if wirless connectivity is enabled");
				if (wm.isWifiEnabled()) {

					Log.d("WifiadbWidget", "Wireless connectivity is enabled");
					String strIP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

					edtInformation.status(getString(R.string.enabled));
					edtInformation.expandedBody(String.format(getString(R.string.portno), strIP, intPort));
					edtInformation.visible(true);

				} else {

					Log.d("WifiadbWidget", "Wireless connectivity isn't enabled");

					edtInformation.status(getString(R.string.disabled));
					edtInformation.expandedBody(getString(R.string.plugin));
					edtInformation.visible(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("always", true));

				}

			} else {

				Log.d("WifiadbWidget", "Wireless debugging is disabled");
				edtInformation.status(getString(R.string.disabled));
				edtInformation.expandedBody(getString(R.string.plugin));
				edtInformation.visible(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("always", true));

			}

			edtInformation.clickIntent(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));

			if (new Random().nextInt(5) == 0) {

				PackageManager mgrPackages = getApplicationContext().getPackageManager();

				try {

					mgrPackages.getPackageInfo("com.mridang.donate", PackageManager.GET_META_DATA);

				} catch (NameNotFoundException e) {

					Integer intExtensions = 0;
					Intent ittFilter = new Intent("com.google.android.apps.dashclock.Extension");
					String strPackage;

					for (ResolveInfo info : mgrPackages.queryIntentServices(ittFilter, 0)) {

						strPackage = info.serviceInfo.applicationInfo.packageName;
						intExtensions = intExtensions + (strPackage.startsWith("com.mridang.") ? 1 : 0); 

					}

					if (intExtensions > 1) {

						edtInformation.visible(true);
						edtInformation.clickIntent(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=com.mridang.donate")));
						edtInformation.expandedTitle("Please consider a one time purchase to unlock.");
						edtInformation.expandedBody("Thank you for using " + intExtensions + " extensions of mine. Click this to make a one-time purchase or use just one extension to make this disappear.");
						setUpdateWhenScreenOn(true);

					}

				}

			} else {
				setUpdateWhenScreenOn(false);
			}

		} catch (Exception e) {
			edtInformation.visible(false);
			Log.e("WifiadbWidget", "Encountered an error", e);
			BugSenseHandler.sendException(e);
		}

		edtInformation.icon(R.drawable.ic_dashclock);
		publishUpdate(edtInformation);
		Log.d("WifiadbWidget", "Done");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onDestroy()
	 */
	public void onDestroy() {

		super.onDestroy();
		Log.d("WifiadbWidget", "Destroyed");
		BugSenseHandler.closeSession(this);

	}

}