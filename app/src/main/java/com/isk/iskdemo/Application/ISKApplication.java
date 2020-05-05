package com.isk.iskdemo.Application;

//region **  Imports **

import android.app.Application;
import com.isk.iskdemo.Classes.Globals.Globals;
import uk.co.smartcalling.smartcalling.SmartCallingManager;

//endregion


public class ISKApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

//		SmartCallingManager.init(this, "https://portal-uat.smartcom.net/", null);
		SmartCallingManager.init(this, "http://10.0.2.2:60776/", null);

		Globals.smartCallingManager = SmartCallingManager.getInstance();

		Globals.smartCallingManager.setOnlineMode(true);
		Globals.smartCallingId = Globals.smartCallingManager.getUserId();
	}

}
