package com.isk.iskdemo.Classes.FireBase;

//region ** Imports **

import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.isk.iskdemo.Classes.Globals.Globals;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

//endregion



public class PushHandler extends FirebaseMessagingService {

	public PushHandler() {

	}

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		Log.d("ISKDemo", "From: " + remoteMessage.getFrom());

//		PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
//		assert pm != null;
//		boolean isScreenOn = pm.isInteractive();
//		//Log.e("screen on.................................", ""+isScreenOn);
//		if(!isScreenOn) {
//			@SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
//			wl.acquire(10000);
//			@SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
//			wl_cpu.acquire(10000);
//		}

		if (remoteMessage.getData().size() > 0) {
			scheduleJob(remoteMessage.getData());
		}

		if (remoteMessage.getNotification() != null) {
			Log.d("ISKDemo", "Message Notification Body: " + remoteMessage.getNotification().getBody());
		}
	}


	@Override
	public void onNewToken(@NotNull String token) {
		Log.d("ISKDemo", "Refreshed token: " + token);

		sendRegistrationToServer(token);
	}


	private void sendRegistrationToServer(String token) {
		// TODO: Implement this method to send token to your app server.
	}


	private void scheduleJob(Map<String, String> data) {
		Globals.pushData = data;

		OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(FBWorker.class)
				.build();
		WorkManager.getInstance().beginWith(work).enqueue();
	}
}
