package com.isk.iskdemo.Classes.FireBase;

//region ** Imports **

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.isk.iskdemo.Classes.Globals.Globals;
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

		String result = SmartCallingManager.getInstance().parsePushMessage(Globals.pushData);

		Log.d("ISKDemo", "Result = " + result);

		return Result.success();
	}

}
