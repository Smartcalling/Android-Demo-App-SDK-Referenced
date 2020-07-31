package com.isk.iskdemo.Activities;

//region ** Imports **

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.isk.iskdemo.Classes.Globals.Globals;
import com.isk.iskdemo.Classes.Workers.SCWorker;
import com.isk.iskdemo.R;
import com.isk.iskdemo.databinding.ActivityHomeBinding;

import java.util.concurrent.TimeUnit;

import uk.co.smartcalling.smartcalling.SmartCallingManager;

//endregion


public class HomeActivity extends AppCompatActivity {

	//region ** Variables **

	private ActivityHomeBinding ui;

	//endregion


	//region ** Initialisation **

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ui = ActivityHomeBinding.inflate(getLayoutInflater());
		View root = ui.getRoot();
		setContentView(root);

		Globals.currentActivity = this;

		ui.lblVersion.setText(String.format("Version: %s", Globals.getAppVersion()));

		ui.lblInfo.setMovementMethod(new ScrollingMovementMethod());
		LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(smartComReceiver, new IntentFilter("SCLogging"));

		// USE USER NAME RETURNED BY YOUR OWN API
		ui.lblWelcome.setText("Welcome John Smith");
		ui.lblSCId.setText("SmartCalling Id:\n" + Globals.smartCallingId);
		// USE UNIQUE ID RETURNED BY YOUR API
		ui.lblClientId.setText("Client Id:\na58a7ea2d64a42a7b0e206f23c6f2b95");

		Globals.smartCallingManager.setClientId("a58a7ea2d64a42a7b0e206f23c6f2b95");
		Globals.smartCallingManager.setFCMToken(Globals.pushToken);

		Globals.smartCallingManager.requestPermissions(this);

		startWorker();

		ui.cmdLogOut.setOnClickListener(v -> {
			Globals.smartCallingManager.logOut();

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

	@Override
	protected void onResume() {
		super.onResume();
	}


	//endregion


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
