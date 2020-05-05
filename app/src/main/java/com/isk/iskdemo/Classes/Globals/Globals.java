package com.isk.iskdemo.Classes.Globals;

//region ** Imports **

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.isk.iskdemo.Classes.Misc.Crypto;
import com.isk.iskdemo.Classes.WebService.Classes.T.ToucanObject;
import com.isk.iskdemo.Classes.WebService.Classes.U.User;
import com.isk.iskdemo.R;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Objects;

import uk.co.smartcalling.smartcalling.SmartCallingManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

//endregion



public class Globals {

	//region ** Static Variables & Enums **

	public static Context ctx;
	public static String packageName;

	public static ToucanObject Toucan;
	public static String apiURL;
	public static String testUserName;
	public static String testPassword;

	public static SmartCallingManager smartCallingManager;

	private static Crypto.SecretKeys secretKeys;

	public static User currentUser;
	public static Activity currentActivity;

	public static String smartCallingId;

	private static MaterialDialog _progressDialog;

	public static String pushToken;
	public static Map<String, String> pushData;

	public enum enumMsgType {
		MSG_NONE,
		MSG_PROGRESS,
		MSG_ERROR,
		MSG_SUCCESS,
		MSG_INFO,
		MSG_QUESTION
	}

	//endregion


	//region ** Constructor & Init **

	private Globals() {
	}

	public static void init() {
		// LIVE
		Globals.apiURL = "https://smartcomapi.azurewebsites.net/api/";
		Globals.testUserName = "Turbot";
		Globals.testPassword = "P455W0rd!";

		generateKey();
	}

	//endregion


	//region ** Misc **

	public static boolean IsNullOrEmpty(String source) {
		if (source == null) {
			return true;
		}
		else if (source.trim().equals("null")) {
			return true;
		}
		else {
			source = source.trim();
			return source.equals("");
		}
	}


	public static void dismissKeyBoard(Activity activity) {
		if (activity == null) {
			activity = Globals.currentActivity;
		}
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);

		assert imm != null;
		if(imm.isAcceptingText()) {
			try {
				imm.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
			}
			catch (java.lang.NullPointerException ex) {
				Log.d("ISKDemo", Objects.requireNonNull(ex.getMessage()));
			}
		}
	}


	public static Drawable getDrawable(int dId) {
		return Globals.ctx.getResources().getDrawable(dId);
	}


	public static String getAppVersion() {
		String result = "";
		try {
			PackageInfo pinfo = Globals.ctx.getPackageManager().getPackageInfo(Globals.ctx.getPackageName(), 0);
			result = pinfo.versionName;
		}
		catch (PackageManager.NameNotFoundException ignored) {
		}

		return result;
	}

	//endregion


	//region ** Settings **

	public static void setStringSetting(String key, String value) {
		SharedPreferences sharedPref = Globals.ctx.getSharedPreferences("ISK_SmartCom", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.apply();
	}


	public static String getStringSetting(String key, String defaultValue) {
		String result;

		SharedPreferences sharedPref = Globals.ctx.getSharedPreferences("ISK_SmartCom", Context.MODE_PRIVATE);
		result = sharedPref.getString(key, defaultValue);

		return result;
	}


	public static void setIntSetting(String key, int value) {
		SharedPreferences sharedPref = Globals.ctx.getSharedPreferences("ISK_SmartCom", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(key, value);
		editor.apply();
	}


	public static int getIntSetting(String key, int defaultValue) {
		int result;

		SharedPreferences sharedPref = Globals.ctx.getSharedPreferences("ISK_SmartCom", Context.MODE_PRIVATE);
		result = sharedPref.getInt(key, defaultValue);

		return result;
	}

	//endregion


	//region ** Encryption **

	private static void generateKey() {
		try {
			secretKeys = Crypto.generateKeyFromPassword("Turbot", "McFoon");
		}
		catch(GeneralSecurityException ex) {
			Log.d("ISKDemo", ex.toString());
		}
	}


	public static String encryptString(String source) {
		String result = "";

		try {
			Crypto.CipherTextIvMac cipherTextIvMac = Crypto.encrypt(source, secretKeys);
			result = cipherTextIvMac.toString();
		}
		catch (UnsupportedEncodingException | GeneralSecurityException ex) {
			Log.d("ISKDemo", ex.toString());
		}

		return result;
	}



	public static String decryptString(String source)  {
		String result = "";

		try {
			Crypto.CipherTextIvMac cipherTextIvMac = new Crypto.CipherTextIvMac(source);
			result = Crypto.decryptString(cipherTextIvMac, secretKeys);
		}
		catch (UnsupportedEncodingException | GeneralSecurityException ex) {
			Log.d("ISKDemo", ex.toString());
		}

		return result;
	}

	//endregion


	//region ** Message Boxes **

	private static Drawable getAlertIcon(enumMsgType type) {
		Drawable result = null;

		switch (type) {
			case MSG_NONE: {
				result = getDrawable(R.drawable.dlg_none);
				break;
			}
			case MSG_ERROR: {
				result = getDrawable(R.drawable.dlg_error);
				break;
			}
			case MSG_SUCCESS: {
				result = getDrawable(R.drawable.dlg_success);
				break;
			}
			case MSG_PROGRESS: {
				result = getDrawable(R.drawable.dlg_progress);
				break;
			}
			case MSG_INFO: {
				result = getDrawable(R.drawable.dlg_info);
				break;
			}
			case MSG_QUESTION: {
				result = getDrawable(R.drawable.dlg_question);
				break;
			}
		}

		return result;
	}


	public static void showOKMessage(enumMsgType alertType, String stringTitle, String stringMessage, MaterialDialog.SingleButtonCallback okResult) {
		Drawable icon = getAlertIcon(alertType);

		try {
			new MaterialDialog.Builder(Globals.currentActivity)
					.title(stringTitle)
					.content(stringMessage)
					.positiveText("OK")
					.onPositive(okResult)
					.icon(icon)
					.show();
		}
		catch (Exception ex) {
			Log.d("ISKDemo", ex.toString());
		}
	}


	public static void showProgress(String title, String message, boolean withCancel) {
		if (_progressDialog != null) {
			_progressDialog.dismiss();
			_progressDialog = null;
		}

		if (title == null || title.equals("")) {
			title = "One moment please";
		}

		Activity activity = Globals.currentActivity;

		if (withCancel) {
			_progressDialog = new MaterialDialog.Builder(activity)
					.title(title)
					.content(message)
					.negativeText("Cancel")
					.progress(true, 0)
					.icon(getAlertIcon(enumMsgType.MSG_PROGRESS))
					.onNegative((dialog, which) -> _progressDialog.dismiss())
					.show();
		}
		else {
			_progressDialog = new MaterialDialog.Builder(activity)
					.title(title)
					.content(message)
					.progress(true, 0)
					.icon(getAlertIcon(enumMsgType.MSG_PROGRESS))
					.show();
		}
	}


	public static void dismissProgress() {
		if (_progressDialog != null) {
			_progressDialog.dismiss();
			_progressDialog = null;
		}
	}

	//endregion

}
