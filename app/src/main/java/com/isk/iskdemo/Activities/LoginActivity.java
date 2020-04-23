package com.isk.iskdemo.Activities;

//region ** Imports **

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.isk.iskdemo.Classes.Globals.Globals;
import com.isk.iskdemo.Classes.WebService.Classes.T.ToucanObject;
import com.isk.iskdemo.Classes.WebService.Classes.U.User;
import com.isk.iskdemo.Classes.WebService.General.WS_ISK;
import com.isk.iskdemo.Classes.WebService.General.WS_Result;
import com.isk.iskdemo.R;
import com.isk.iskdemo.databinding.ActivityLoginBinding;

import java.util.Objects;

import uk.co.smartcalling.smartcalling.SmartCallingManager;

//endregion


public class LoginActivity extends AppCompatActivity {

	//region ** Variables **

	private ActivityLoginBinding ui;

	//endregion



	//region ** Init Routines **

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (!isTaskRoot()) {
			finish();
			return;
		}

		super.onCreate(savedInstanceState);

		ui = ActivityLoginBinding.inflate(getLayoutInflater());
		View root = ui.getRoot();
		setContentView(root);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// Create channel to show notifications.
			String channelId  = getString(R.string.default_notification_channel_id);
			String channelName = getString(R.string.default_notification_channel_name);
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
		}

		if (getIntent().getExtras() != null) {
			for (String key : getIntent().getExtras().keySet()) {
				Object value = getIntent().getExtras().get(key);
				Toast.makeText(LoginActivity.this, "Key: " + key + " Value: " + value, Toast.LENGTH_SHORT).show();
				Log.d("ISKDemo", "Key: " + key + " Value: " + value);
			}
		}

		//		SmartCallingManager.init(getApplication(), "https://turbot3-215ee28e.localhost.run/", null);
		SmartCallingManager.init(getApplication(), "https://portal-uat.smartcom.net/", null);

		SmartCallingManager smartCallingManager = SmartCallingManager.getInstance();
		smartCallingManager.requestPermissions(this);

		smartCallingManager.setOnlineMode(true);
		Globals.smartCallingId = smartCallingManager.getUserId();

		Globals.ctx = this.getApplicationContext();
		Globals.packageName = this.getPackageName();
		Globals.currentActivity = this;

		setupPush();
		subscribePush();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
		);

		ui.cmdLogin.setOnClickListener(loginClicked);
		ui.cmdDebug.setOnClickListener(debugClicked);

		Globals.init();
	}

	//endregion


	//region ** Events **

	private final View.OnClickListener loginClicked = v -> {
		Globals.dismissKeyBoard(LoginActivity.this);

		String uName = ui.txtUserName.getText().toString().trim();
		String pWord = ui.txtPWord.getText().toString().trim();

		if (Globals.IsNullOrEmpty(uName) || Globals.IsNullOrEmpty(pWord)) {
			Globals.showOKMessage(Globals.enumMsgType.MSG_INFO, "Values Required!", "You must enter valid values into the email and password fields.", null);
		}
		else {
			Globals.showProgress("Validating Login", "One moment please...", false);
			tryLogin();
		}
	};


	private final View.OnClickListener debugClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ui.txtUserName.setText(Globals.testUserName);
			ui.txtPWord.setText(Globals.testPassword);
		}
	};

	//endregion


	//region ** Misc Routines **

	private void setupPush() {
		FirebaseInstanceId.getInstance().getInstanceId()
				.addOnCompleteListener(task -> {
					if (!task.isSuccessful()) {
						Log.w("ISKDemo", "getInstanceId failed", task.getException());
						return;
					}

					// Get new Instance ID token
					Globals.pushToken = Objects.requireNonNull(task.getResult()).getToken();
				});
	}


	private void subscribePush() {
		FirebaseMessaging.getInstance().subscribeToTopic("campaign")
				.addOnCompleteListener(task1 -> {
					if (!task1.isSuccessful()) {
						Log.d("ISKDemo", "Failed");
					}
				});
	}


	private void tryLogin() {
		final String strUName = ui.txtUserName.getText().toString().trim();
		final String strPWord = ui.txtPWord.getText().toString().trim();

		Globals.dismissKeyBoard(LoginActivity.this);

		WS_ISK.GetToucan(strUName, strPWord, result -> {
			if (result.result == WS_Result.enumWSResult.SUCCESS) {
				ToucanObject toucan = (ToucanObject)result.xObject;
				if (!Globals.IsNullOrEmpty(toucan.Toucan)) {
					Globals.Toucan = toucan;

					WS_ISK.GetUser(toucan.UserID, result1 -> {
						if (result1.result == WS_Result.enumWSResult.SUCCESS) {
							User nUser = (User) result1.xObject;

							Globals.dismissProgress();

							Globals.currentUser = nUser;

							final Handler handler = new Handler();
							handler.postDelayed(() -> {
								setDefaults();
								goHome();
							}, 250);
						}
						else {
							Globals.dismissProgress();
							ui.txtPWord.setText("");
							Globals.Toucan = null;
							Globals.showOKMessage(Globals.enumMsgType.MSG_ERROR, "Invalid Login", "The app does not recognise the login details you have entered.\nPlease try again.", null);
						}
					});
				}
				else {
					Globals.dismissProgress();
					ui.txtPWord.setText("");
					Globals.Toucan = null;
					Globals.showOKMessage(Globals.enumMsgType.MSG_ERROR, "Invalid Login", "The app does not recognise the login details you have entered.\nPlease try again.", null);
				}
			}
			else if (result.result == WS_Result.enumWSResult.RETRY) {
				Globals.dismissProgress();
				tryLogin();
			}
			else if (result.result == WS_Result.enumWSResult.INVALIDLOGIN) {
				Globals.dismissProgress();
				ui.txtPWord.setText("");
				Globals.Toucan = null;
				Globals.showOKMessage(Globals.enumMsgType.MSG_ERROR, "Invalid Login", "The app does not recognise the login details you have entered.\nPlease try again.", null);
			}
			else {
				Globals.dismissProgress();
				ui.txtPWord.setText("");
				Globals.Toucan = null;
				Globals.showOKMessage(Globals.enumMsgType.MSG_ERROR, "Invalid Login", "The app does not recognise the login details you have entered.\nPlease try again.", null);
			}
		});
	}


	private void setDefaults() {
		String strUserName = ui.txtUserName.getText().toString().trim();
		Globals.setStringSetting("UserName", strUserName);

		String pwd = ui.txtPWord.getText().toString();
		if (!Globals.IsNullOrEmpty(pwd)) {
			String encPwd = Globals.encryptString(pwd);
			Globals.setStringSetting("Password", encPwd);
		}

		Globals.setIntSetting("UserID", Globals.currentUser.UserID);
		Globals.setStringSetting("UniqueId", Globals.currentUser.UniqueId);
	}


	private void goHome() {
		Intent i = new Intent(LoginActivity.this, HomeActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(i);
		overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
		finish();
	}

	//endregion


	//region ** Permissions **

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		SmartCallingManager.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		SmartCallingManager.getInstance().onActivityResult(this, requestCode, resultCode, data);
	}

	//endregion

}
