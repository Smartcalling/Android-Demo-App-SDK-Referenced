package com.isk.iskdemo.Classes.Workers;

//region ** Imports **

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.isk.iskdemo.Classes.Globals.Globals;
import com.isk.iskdemo.Classes.WebService.General.WS_ISK;

//endregion


public class SCWorker extends Worker {

	public SCWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
	}


	@NonNull
	@Override
	public Result doWork() {
		Globals.smartCallingManager.sync();
		WS_ISK.AddLog(Globals.currentUser.UserID, "Ran Background Task");

		return Result.success();
	}


	@Override
	public void onStopped() {
		super.onStopped();

		WS_ISK.AddLog(Globals.currentUser.UserID, "Background Task Stopped");
	}

}
