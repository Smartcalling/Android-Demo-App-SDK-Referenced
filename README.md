# Android-Demo-App-SDK-Referenced
	App Version: 1.5
	SDK Version: 1.3.1

Please report any bugs/issues/suggestions to <cj@smartcalling.co.uk>


## Installation


The SmartCalling SDK is provided in the form of an AAR file (Android Library Project) and can be included in your project as a Maven dependency.


**NOTE FOR ECLIPSE USERS: Whilst Android Studio has excellent support for AAR dependencies, Eclipse has only limited support for working with AARs. For more information regarding using AAR dependencies in Eclipse, please see https://commonsware.com/blog/2014/07/03/consuming-aars-eclipse.html. This guide will cover the use of Android Studio only.**


1) Open your build.gradle file corresponding to a new/existing Android Studio project which you wish to integrate with SmartCalling (this is normally the build.gradle file for your 'app' module).

2) Add the following repositories section at the top level of build.gradle:

	```
	repositories {
		maven { url 'https://smartcalling.github.io/repo/' }
	}
	```


3) Add the following dependencies section (or add to your existing dependencies section) at the top level of build.gradle:

	```
	dependencies {
		compile('uk.co.smartcalling.sdk:smartcalling:1.2.3@aar') {
			transitive=true
			changing=true
			exclude(group: 'org.json', module: ‘json')
		}
	}
	```
  
4) For compatibility, you may also need to add the following code into the 'android' section:

	```
	android {
		...
		compileOptions {
			sourceCompatibility = 1.8
			targetCompatibility = 1.8
		}
		...
	}
	```

5) The SmartCalling SDK has now been integrated and you should now perform a Gradle Sync and build your project to pull in the AAR dependency.

6) If you haven't already, you will need to add your app to the SmartCom portal. Once this is done, you will be provided with an API Key. You should add the following values to the Manifest using your API Key:
	
	```
	<meta-data
		android:name="uk.co.smartcalling.ApiKey"
		android:value="XXX" />
	```
	
7) The SDK requires the following manifest permissions which must be added to the manifest if they have not already been added:

	```
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
	<uses-permission android:name="android.permission.WAKE_LOCK" />
	<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
	<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"/>
	```

8) Next, Add the following to the onCreate() method of your project’s Application:

	```
	@Override
	Public void onCreate() {
		super.onCreate();
	   
		SmartCallingManager.init(this, "https://portal.smartcom.net/", null);
	}
	```
  
9) If you require a reference to the SmartCalling Manager instance you can do so using something like the code below. You can then use this reference instead of calling *SmartCallingManager.getInstance()* each time:

	```
	public static SmartCallingManager smartCallingManager;
	smartCallingManager = SmartCallingManager.getInstance();
	```

10) The SDK requires certain permissions to work correctly, to that end you need to make a call to the 'requestPermissions' function in the SDK:

	```
	SmartCallingManager.getInstance().requestPermissions(this);
	```

11) Then you will need to ensure your activity extends from AppCompatActivity and you will need to implement two methods to forward Android permission callbacks on to the SmartCalling SDK. These methods are onRequestPermissionsResult and onActivityResult. Once you have implemented these, ensure they are calling super() with all the required arguments and then ensure that each method forwards on to the SmartCallingManager. Your methods should look like this:

	```
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
	```

12) You also need to tell the SDK that you have an internet connection by making the following call:

	```
	SmartCallingManager.getInstance().setOnlineMode(true);
	```
		
13) Each device utilising the SmartCom SDK will also be assigned an ID that is used internally. If, for any reason you want to store this value, you can retrieve the ID using the following command:

	```
	String scID = SmartCallingManager.getInstance().getUserId();
	```


## Login Process

1) An assumption is made that your app will have a login process that results in the app receiving user information. For the SDK to work correctly, your login process must return a unique ID for the user logged in. This unique ID must be passed to the SDK using the following code (where XXX is the unique user ID):

	```
	SmartCallingManager.getInstance().setClientId("XXX");
	```

2) The unique ID can be any value such as a primary key value or GUID. It simply needs to be unique to your user within your system.


## Firebase Cloud Messaging Support
  
1) The SmartCom solution utilises FireBase Cloud Messaging (FCM) to notify your app of any changes to your campaigns. Once your app has a push token, this must be applied to the FCMToken property of the SmartCallingManager. Below is an example of initialising Firebase and sending the token to the SDK:

	```
	FirebaseInstanceId.getInstance().getInstanceId()
		.addOnCompleteListener(task -> {
			if (!task.isSuccessful()) {
				Log.w("SCDemo", "getInstanceId failed", task.getException());
				return;
			}

			// Get new Instance ID token
			String token = Objects.requireNonNull(task.getResult()).getToken();
			SmartCallingManager.getInstance().setFCMToken(token);
		});

	```

2) Next, you must add code to subscribe to the 'campaign' topic:

	```
	FirebaseMessaging.getInstance().subscribeToTopic("campaign")
		.addOnCompleteListener(task1 -> {
			if (!task1.isSuccessful()) {
				Log.d("SCDemo", "Failed");
			}
		});
	```

3) When a push is received your push handler must pass the push data to the SDK. This demo app has an example of how to do this using a 'OneTimeWorkRequest':

	```
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
		assert pm != null;
		boolean isScreenOn = pm.isInteractive();
		if(!isScreenOn) {
			@SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
			wl.acquire(10000);
			@SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
			wl_cpu.acquire(10000);
		}

		if (remoteMessage.getData().size() > 0) {
			scheduleJob(remoteMessage.getData());
		}
	}


	private void scheduleJob(Map<String, String> data) {
		// Store push data in global static variable
		Globals.pushData = data;

		OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(FBWorker.class)
				.build();
		WorkManager.getInstance().beginWith(work).enqueue();
	}
	
	
	
	public class FBWorker extends Worker {
		public FBWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
			super(appContext, workerParams);
		}

		@NonNull
		@Override
		public Result doWork() {
			// Utilise push data stored in global static variable
			String result = SmartCallingManager.getInstance().parsePushMessage(Globals.pushData);
			return Result.success();
		}
	}
	```
	
4) If you can't or do not want to support the FCM push messaging solution you will need to implement a background worker that calls the SmartCallingManager.sync command. An example of this can be found in the HomeActivity of demo app:

	```
	protected void onCreate(Bundle savedInstanceState) {
		...
		startWorker();
		...
	}

	private void startWorker() {
		WorkManager mWorkManager = WorkManager.getInstance(getApplicationContext());

		Constraints constraints = new Constraints.Builder()
				.setRequiredNetworkType(NetworkType.CONNECTED)
				.build();

		PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(SCWorker.class, 60, TimeUnit.MINUTES)
				.setConstraints(constraints)
				.build();

		mWorkManager.enqueueUniquePeriodicWork("SmartComSync", ExistingPeriodicWorkPolicy.REPLACE, workRequest);

		mWorkManager.getWorkInfoByIdLiveData(workRequest.getId())
				.observe(this, info ->
						Log.d("SCDemo", "")
				);
	}
	
	
	public class SCWorker extends Worker {
		public SCWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
			super(context, workerParams);
		}

		@NonNull
		@Override
		public Result doWork() {
			SmartCallingManager.getInstance().sync();
			return Result.success();
		}

		@Override
		public void onStopped() {
			super.onStopped();
		}
	}
	```

		
#### Congratulations, you are now ready to start using SmartCalling!
