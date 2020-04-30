package com.isk.iskdemo.Activities;

//region ** Imports **

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.isk.iskdemo.Classes.Globals.Globals;
import com.isk.iskdemo.Classes.WebService.General.WS_ISK;
import com.isk.iskdemo.Classes.Workers.SCWorker;
import com.isk.iskdemo.R;
import com.isk.iskdemo.databinding.ActivityHomeBinding;

import java.util.concurrent.TimeUnit;

//endregion


public class HomeActivity extends AppCompatActivity {

	//region ** Variables **

	private ActivityHomeBinding ui;

	//endregion

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ui = ActivityHomeBinding.inflate(getLayoutInflater());
		View root = ui.getRoot();
		setContentView(root);

		Globals.currentActivity = this;

		ui.lblInfo.setMovementMethod(new ScrollingMovementMethod());
		LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(smartComReceiver, new IntentFilter("SCLogging"));

		ui.lblWelcome.setText("Welcome " + Globals.currentUser.getFullName());
		ui.lblSCId.setText("SmartCalling Id:\n" + Globals.smartCallingId);
		ui.lblClientId.setText("Client Id:\n" + Globals.currentUser.UniqueId);

		Globals.smartCallingManager.setClientId(Globals.currentUser.UniqueId);
		Globals.smartCallingManager.setFCMToken(Globals.pushToken);

		if (!TextUtils.isEmpty(Globals.pushToken)) {
			WS_ISK.PostPushToken(Globals.currentUser.UserID, Globals.pushToken, result -> {
			});
		}

		startWorker();

		ui.cmdLogOut.setOnClickListener(v -> {
			Globals.smartCallingManager.setClientId("");
			Globals.currentUser = null;

			Globals.setStringSetting("UserName", "");
			Globals.setStringSetting("Password", "");
			Globals.setIntSetting("UserID", 0);
			Globals.setStringSetting("UniqueId", "");

			Intent i = new Intent(HomeActivity.this, LoginActivity.class);
			HomeActivity.this.startActivity(i);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			finish();
		});


		ui.cmdPoll.setOnClickListener(v -> Globals.smartCallingManager.sync());
	}


	//region ** Broadcast Receivers **

	private final BroadcastReceiver smartComReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String message = intent.getStringExtra("Message");
			String cText = ui.lblInfo.getText().toString();
			cText = message + "\n\n" + cText;

			ui.lblInfo.setText(cText);
		}
	};

	//endregion


	//region ** Background Worker **

	private void startWorker() {
		WorkManager mWorkManager = WorkManager.getInstance(getApplicationContext());

		Constraints constraints = new Constraints.Builder()
				.setRequiredNetworkType(NetworkType.CONNECTED)
				.build();

		PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(SCWorker.class, 15, TimeUnit.MINUTES)
				.setConstraints(constraints)
				.build();

		mWorkManager.enqueueUniquePeriodicWork("SmartComSync", ExistingPeriodicWorkPolicy.REPLACE, workRequest);

		mWorkManager.getWorkInfoByIdLiveData(workRequest.getId())
				.observe(this, info ->
						Log.d("ISKDemo", "")
				);
	}

	//endregion

}
