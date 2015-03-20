package com.mridang.wifiadb;

import org.acra.ACRA;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;

import com.google.android.apps.dashclock.api.ExtensionData;

/*
 * This class is the main class that provides the widget
 */
public class WifiadbWidget extends ImprovedExtension {

	/*
	 * (non-Javadoc)
	 * @see com.mridang.wifiadb.ImprovedExtension#getIntents()
	 */
	@Override
	protected IntentFilter getIntents() {

		IntentFilter itfIntents = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		itfIntents.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		itfIntents.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		return itfIntents;

	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.wifiadb.ImprovedExtension#getTag()
	 */
	@Override
	protected String getTag() {
		return getClass().getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.wifiadb.ImprovedExtension#getUris()
	 */
	@Override
	protected String[] getUris() {
		return new String[] {Settings.Secure.getUriFor("adb_port").toString()};
	}

	/*
	 * @see
	 * com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData
	 * (int)
	 */
	@Override
	protected void onUpdateData(int intReason) {

		Log.d(getTag(), "Fetching wireless debugging status");
		ExtensionData edtInformation = new ExtensionData();
		setUpdateWhenScreenOn(false);

		try {

			Integer intPort = Settings.Secure.getInt(getContentResolver(), "adb_port", -1);
			if (intPort > 0) {

				Log.d(getTag(), "Wireless debugging is enabled");
				WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

				Log.d(getTag(), "Checking if wireless connectivity is enabled");
				if (wm.isWifiEnabled()) {

					String strIP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

					Log.d(getTag(), "Wireless connectivity is enabled");
					edtInformation.status(getString(R.string.enabled));
					edtInformation.expandedBody(getString(R.string.portno, strIP, intPort));
					edtInformation.visible(true);

				} else {

					Log.d(getTag(), "Wireless connectivity isn't enabled");
					edtInformation.status(getString(R.string.disabled));
					edtInformation.expandedBody(getString(R.string.plugin));
					edtInformation.visible(getBoolean("always", true));

				}

			} else {

				Log.d(getTag(), "Wireless debugging is disabled");
				edtInformation.status(getString(R.string.disabled));
				edtInformation.expandedBody(getString(R.string.plugin));
				edtInformation.visible(getBoolean("always", true));

			}

			edtInformation.clickIntent(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));

		} catch (Exception e) {
			edtInformation.visible(false);
			Log.e(getTag(), "Encountered an error", e);
			ACRA.getErrorReporter().handleSilentException(e);
		}

		edtInformation.icon(R.drawable.ic_dashclock);
		doUpdate(edtInformation);

	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.wifiadb.ImprovedExtension#onReceiveIntent(android.content.Context, android.content.Intent)
	 */
	@Override
	protected void onReceiveIntent(Context ctxContext, Intent ittIntent) {
		onUpdateData(UPDATE_REASON_MANUAL);
	}

}