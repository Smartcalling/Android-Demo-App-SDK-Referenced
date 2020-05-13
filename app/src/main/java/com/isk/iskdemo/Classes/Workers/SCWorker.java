package com.isk.iskdemo.Classes.Workers;

//region ** Imports **

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.isk.iskdemo.Classes.Globals.Globals;

//endregion


public class SCWorker extends Worker {

	public SCWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
	}


	@NonNull
	@Override
	public Result doWork() {
		Globals.smartCallingManager.sync();

		return Result.success();
	}


	@Override
	public void onStopped() {
		super.onStopped();
	}

}
