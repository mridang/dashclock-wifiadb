package com.mridang.wifiadb;

import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import android.app.Application;
import android.content.Context;

@SuppressWarnings("ALL")
@ReportsCrashes(formUri = "https://mridang.cloudant.com/acra-dashclock/_design/acra-storage/_update/report", reportType = HttpSender.Type.JSON, httpMethod = HttpSender.Method.POST, formUriBasicAuthLogin = "selyinsainsinevesevarmst", formUriBasicAuthPassword = "OVB0aOwRxDBs0jUKKLwMNyH6", formKey = "", sendReportsInDevMode = false, customReportContent = {
		ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
		ReportField.PACKAGE_NAME, ReportField.REPORT_ID, ReportField.BUILD, ReportField.STACK_TRACE, ReportField.BRAND,
		ReportField.PHONE_MODEL, ReportField.BUILD, ReportField.INSTALLATION_ID, ReportField.CUSTOM_DATA,
		ReportField.USER_CRASH_DATE })
/*
 * Dummy application class for use with ACRA since ACRA cannot be initialized
 * with the instance of an application. The Dashclock Service is not a subclass
 * of Application and therefore we use this pseudo application class with the
 * context of the Dashclock widget to initialize ACRA
 */
public class AcraApplication extends Application {

	/**
	 * Constructor the the dummy application which simply attached the correct
	 * context
	 * 
	 * @param ctxContext The context of the Dashclock widget
	 */
	public AcraApplication(Context ctxContext) {
		this.attachBaseContext(ctxContext);
	}

}
