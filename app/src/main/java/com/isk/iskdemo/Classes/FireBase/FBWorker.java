package com.isk.iskdemo.Classes.FireBase;

//region ** Imports **

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.isk.iskdemo.Classes.Globals.Globals;
import com.isk.iskdemo.Classes.WebService.General.WS_ISK;

import uk.co.smartcalling.smartcalling.SmartCallingManager;

//endregion


public class FBWorker extends Worker {

	public FBWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
		super(appContext, workerParams);
	}


	@NonNull
	@Override
	public Result doWork() {
		Log.d("ISKDemo", "Performing long running task in scheduled job");

		WS_ISK.AddLog(1, "Push Received Sync");

		SmartCallingManager.getInstance().parsePushMessage(Globals.pushData);

		return Result.success();
	}

}
