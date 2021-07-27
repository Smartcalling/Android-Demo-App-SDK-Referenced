package com.isk.iskdemo.Application;

//region **  Imports **

import android.app.Application;

import com.isk.iskdemo.Classes.Globals.Globals;

import java.util.ArrayList;
import java.util.List;

import uk.co.smartcalling.smartcalling.SmartCallingManager;

//endregion


public class ISKApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		List<String> sslPins = new ArrayList<String>();
		sslPins.add("sha256/s/pS3RZ80zGSt0LxpzvvE462hDL3apULVVBtQRbLYnQ=");
		sslPins.add("sha256/JSMzqOOrtyOT1kmau6zKhgT676hGgczD5VMdRMyJZFA=");
		sslPins.add("sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI=");
		sslPins.add("sha256/KwccWaCgrnaw6tsrrSO61FgLacNgG2MMLq8GE6+oP5I=");
		SmartCallingManager.init(this, "https://portal-uat.smartcom.net/", sslPins, null);

		//		SmartCallingManager.init(this, "https://528a27c47b2e.ngrok.io/", null);


		Globals.smartCallingManager = SmartCallingManager.getInstance();

		Globals.smartCallingManager.setOnlineMode(true);
		Globals.smartCallingId = Globals.smartCallingManager.getUserId();
	}

}
