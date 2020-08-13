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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.isk.iskdemo.Classes.Globals.Globals;
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
			String channelId  = getString(R.string.default_notification_channel_id);
			String channelName = getString(R.string.default_notification_channel_name);
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
		}

		Globals.init();

		Globals.smartCallingManager = SmartCallingManager.getInstance();

		Globals.smartCallingManager.setOnlineMode(true);
		Globals.smartCallingId = Globals.smartCallingManager.getUserId();

		Globals.ctx = this.getApplicationContext();
		Globals.packageName = this.getPackageName();
		Globals.currentActivity = this;

		ui.lblVersion.setText(String.format("Version: %s", Globals.getAppVersion()));

		ui.txtUserName.setText(Globals.testUserName);
		ui.txtPWord.setText(Globals.testPassword);

		setupPush();
		subscribePush();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
		);

		ui.cmdLogin.setOnClickListener(loginClicked);

		String uName = Globals.getStringSetting("UserName", "");
		String pwd = Globals.getStringSetting("Password", "");
		if (!Globals.IsNullOrEmpty(uName) && !Globals.IsNullOrEmpty(pwd)) {
			ui.txtUserName.setText(uName);

			String decPwd = Globals.decryptString(pwd);
			ui.txtPWord.setText(decPwd);

			tryLogin();
		}
		else {
			Globals.smartCallingManager.requestPermissions(this);
		}
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
					Globals.smartCallingManager.setFCMToken(Globals.pushToken);
				});
	}


	private void subscribePush() {
		FirebaseMessaging.getInstance().subscribeToTopic("smartcallingcampaign")
				.addOnCompleteListener(task1 -> {
					if (!task1.isSuccessful()) {
						Log.d("ISKDemo", "Failed");
					}
				});

		FirebaseMessaging.getInstance().subscribeToTopic("smblacklistupdate")
				.addOnCompleteListener(task1 -> {
					if (!task1.isSuccessful()) {
						Log.d("ISKDemo", "Failed");
					}
				});
	}


	private void tryLogin() {
		// HERE IS WHERE YOU WOULD LOGIN VIA YOUR OWN API, GET THE USER AND A UNIQUE ID FOR THAT USER
		Globals.dismissKeyBoard(LoginActivity.this);

		Globals.dismissProgress();

		final Handler handler = new Handler();
		handler.postDelayed(() -> {
			setDefaults();
			goHome();
		}, 250);

	}


	private void setDefaults() {
		String strUserName = ui.txtUserName.getText().toString().trim();
		Globals.setStringSetting("UserName", strUserName);

		String pwd = ui.txtPWord.getText().toString();
		if (!Globals.IsNullOrEmpty(pwd)) {
			String encPwd = Globals.encryptString(pwd);
			Globals.setStringSetting("Password", encPwd);
		}

		// SET UniqueId VALUE TO THE UNIQUE USER ID RETURNED BY YOUR LOGIN
		Globals.setIntSetting("UserID", 1);
		Globals.setStringSetting("UniqueId", "a58a7ea2d64a42a7b0e206f23c6f2b95");
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
