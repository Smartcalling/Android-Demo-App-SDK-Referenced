# Android-Demo-App-SDK-Referenced
	App Version: 4.18
	Library Version: 4.1.8

Please report any bugs/issues/suggestions to <cj@smartcalling.co.uk>


## Pre-Requisites

First you must setup an Account, an App and a Campaign in the portal. The UAT portal address is: https://portal-uat.smartcom.net/

The next step is to add an app into the portal. If you do not already have an app you will need to create your own test app. Once you have an app, you will need to log into the portal and use the Add App button. Make sure you provide all the required details correctly, especially the bundle name. For the IOS package name just enter any value if you do not have an IOS app. The library works much better if you can include the FireBase Push Messaging details so you may need to create a Firebase account for your test app. To find your FCM Key and Sender ID, login to your Firebase account, select your app and then press the Settings button next to the Project Overview label. Then select Cloud Messaging and you will see your Server Key and Sender Id there. If you do not have a server key you will need to add one.

Once the app has been created in the portal, you can then go to the Account section in the portal (Menu - Account) to see and copy your API Key. You will need this key when integrating the library into your app.

You are now ready to follow the instructions below. Simply follow the instructions to reference the library in your app and provide the library with the details it requires. One important part is the ClientId, your app must provide a unique ClientId in your app for each device/user and you must use these clientIds when creating Campaigns.

## Emulators

While you can test the SmartCalling library in an emulator we have noticed some occasional spurious results when testing campaigns and anti-vishing. For that reason, we strongly recommend you test the SmartCalling libraries on physical devices for best results.

## Installation


The SmartCalling library is provided in the form of an AAR file (Android Library Project) and can be included in your project as a Maven dependency. **This library requires a Android SDK version 21 as a minimum**.


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
		implementation('uk.co.smartcalling.sdk:smartcalling:4.1.7@aar') {
   			exclude(group: 'org.json', module: 'json')
		}
	}
	```
  
4) For compatibility, you may also need to add the following code into the 'android' section:

	```
	android {
		...
		compileOptions {
			sourceCompatibility = JavaVersion.VERSION_1_8
			targetCompatibility = JavaVersion.VERSION_1_8
		}
		...
	}
	```

5) The SmartCalling library has now been integrated and you should now perform a Gradle Sync and build your project to pull in the AAR dependency.

6) If you haven't already, you will need to add your app to the SmartCom portal. Once this is done, you will be provided with an API Key. You should add the following values into the <Application> section of your Manifest. Replace the 'XXX' with your API Key:
	
	```
	<meta-data
		android:name="uk.co.smartcalling.ApiKey"
		android:value="XXX" />
	```
	
7) The library requires the following manifest permissions which must be added to the root <manifest> section of your manifest if they have not already been added The ‘*android.permission.INTERNET*’ permission is required to access any online services while the ‘*com.google.android.c2dm.permission.RECEIVE*’ permission is required to receive Google Firebase push notifications:

	```
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
	```

8) If you do not already have one, you must create an Application class and reference that class in your manifest:

	```
	public class MyApplication extends Application {
		@Override
		public void onCreate() {
			super.onCreate();
		}
	}

	<manifest>
		...
		<application
			android:name=”MyApplication”
			...
		>
			...
		</application>
		...
	<mainfest>
	```

9) Next, Add the following to the onCreate() method of your project’s Application:

	**JAVA**
	```
	@Override
	public void onCreate() {
		super.onCreate();
	   
		SmartCallingManager.init(this, "https://portal.smartcom.net/", null);
	}
	```
		
	**KOTLIN**
	```
	override fun onCreate() {
		super.onCreate()
		SmartCallingManager.init(this, "https://portal.smartcom.net/", null)
	}	
	```

10) Alternatively, if you want to support SSL Pinning to prevent a 'Man in the Middle' attack you can provide a set of SHA256 keys as a list of strings in the second parameter. The keys shown here are for the SmartCom API but if you are hosting the Portal/API yourself you can gather your keys using this site https://www.ssllabs.com/ssltest. Please note that each string must start with 'sha256/':

	**JAVA**
	```
	@Override
	public void onCreate() {
		super.onCreate();
	   
		List<String> sslPins = new ArrayList<String>();
		sslPins.add("sha256/oqVjl7U2cA40xKaPgwLOLl2OaBulsnLEWGCA//gd9qo=");
		sslPins.add("sha256/JSMzqOOrtyOT1kmau6zKhgT676hGgczD5VMdRMyJZFA=");
		sslPins.add("sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI=");
		sslPins.add("sha256/KwccWaCgrnaw6tsrrSO61FgLacNgG2MMLq8GE6+oP5I=");
		SmartCallingManager.init(this, "https://portal.smartcom.net/", sslPins);
	}
	```

	**KOTLIN**
	```
	override fun onCreate() {
		super.onCreate()
		List<String> sslPins = new ArrayList<String>()
		sslPins.add("sha256/oqVjl7U2cA40xKaPgwLOLl2OaBulsnLEWGCA//gd9qo=")
		sslPins.add("sha256/JSMzqOOrtyOT1kmau6zKhgT676hGgczD5VMdRMyJZFA=")
		sslPins.add("sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI=")
		sslPins.add("sha256/KwccWaCgrnaw6tsrrSO61FgLacNgG2MMLq8GE6+oP5I=")
		SmartCallingManager.init(this, "https://portal.smartcom.net/", sslPins)
	}	
	```

11) If you require a reference to the SmartCalling Manager instance you can do so using something like the code below. You can then use this reference instead of calling *SmartCallingManager.getInstance()* each time. The example code below will continue to use *SmartCallingManager.getInstance()* but all occurrences of this code could be replaced with your referenced instance variable:

	**JAVA**
	```
	public static SmartCallingManager smartCallingManager;
	smartCallingManager = SmartCallingManager.getInstance();
	```
		
	**KOTLIN**
	```
	var smartCallingManager: SmartCallingManager? = null 
	smartCallingManager = SmartCallingManager.getInstance()
	```

12) The library requires certain permissions to work correctly, to that end you need to make a call to the 'requestPermissions' function in the library. Ideally this line of code should be written into your starting activity. **This function MUST be called after the 'init' command**:

	**JAVA**
	```
	SmartCallingManager.getInstance().requestPermissions(this);
	```
		
	**KOTLIN**
	```
	SmartCallingManager.getInstance().requestPermissions(this)
	```

13) Then you will need to ensure your activity extends from AppCompatActivity and you will need to implement two methods to forward Android permission callbacks on to the SmartCalling library. These methods are onRequestPermissionsResult and onActivityResult. Once you have implemented these, ensure they are calling super() with all the required arguments and then ensure that each method forwards on to the SmartCallingManager. Your methods should look like this:

	**JAVA**
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
		
	**KOTLIN**
	```
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		SmartCallingManager.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) { 
		super.onActivityResult(requestCode, resultCode, data)
		SmartCallingManager.getInstance().onActivityResult(this, requestCode, resultCode, data)
	}	
	```


## Login Process

1) An assumption is made that your app will have a login process that results in the app receiving user information. For the library to work correctly, your login process must return a unique ID for the user logged in. This unique ID must be passed to the library using the following code (where XXX is the unique user ID):

	**JAVA**
	```
	SmartCallingManager.getInstance().setClientId("XXX");
	```
		
	**KOTLIN**
	```
	SmartCallingManager.getInstance().setClientId("XXX")
	```

2) The unique ID can be any value such as a primary key value or GUID. It simply needs to be unique to your user within your system.


## Logout Process

1) If your app has a logout process it is important that you add code to logout of the library also. Simply add the following line of code to your logout process:

	**JAVA**
	```
	SmartCallingManager.getInstance().logOut();
	```
		
	**KOTLIN**
	```
	SmartCallingManager.getInstance().logOut()
	```


## Firebase Cloud Messaging Support
  
1) The SmartCom solution utilises FireBase Cloud Messaging (FCM) to notify your app of any changes to your campaigns. Once your app has a push token, this must be applied to the FCMToken property of the SmartCallingManager. Below is an example of initialising Firebase and sending the token to the library:

	**JAVA**
	```
	FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
		if (!task.isSuccessful()) {
			// You may want to log this issue
			return;
		}

		// Get new Instance ID token
		String token = Objects.requireNonNull(task.getResult()).getToken();
		SmartCallingManager.getInstance().setFCMToken(token);
	});
	```
		
	**KOTLIN**
	```
	FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task: Task<InstanceIdResult?> -> 
		if (!task.isSuccessful) {
			// You may want to log this issue
			return@addOnCompleteListener
		}
		// Get new Instance ID token
		val token: String = Objects.requireNonNull(task.result).getToken()
		SmartCallingManager.getInstance().setFCMToken(token)
	}
	```

2) Next, you must add code to subscribe to the 'campaign' topic:

	**JAVA**
	```
	FirebaseMessaging.getInstance().subscribeToTopic("smartcallingcampaign").addOnCompleteListener(task1 -> {
		if (!task1.isSuccessful()) {
			// You may want to log this issue
		}
	});
	```
		
	**KOTLIN**
	```
	FirebaseMessaging.getInstance().subscribeToTopic("smartcallingcampaign").addOnCompleteListener { task1: Task<Void?> -> 
		if (!task1.isSuccessful) {
			// You may want to log this issue
		}
	}
	```

3) If your Smartcalling contract supports the use of blacklists, you must also add code to subscribe to the 'smblacklistupdate' topic. This code can go immediately beneath the code subscribing to the 'smartcallingcampaign' topic:

	**JAVA**
	```
	FirebaseMessaging.getInstance().subscribeToTopic("smblacklistupdate")
		.addOnCompleteListener(task1 -> {
			if (!task1.isSuccessful()) {
				// You may want to log this issue
			}
		});
	```
		
	**KOTLIN**
	```
	FirebaseMessaging.getInstance().subscribeToTopic("smblacklistupdate").addOnCompleteListener { task1: Task<Void?> -> 
		if (!task1.isSuccessful) {
			// You may want to log this issue
		}
	}
	```

4) When a push is received your push handler must pass the push data to the library. This demo app has an example of how to do this using a 'OneTimeWorkRequest':

	**JAVA**
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
		
	**KOTLIN**
	```
	fun onMessageReceived(remoteMessage:RemoteMessage) {
		super.onMessageReceived(remoteMessage)
		val pm = (applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager) 
		val isScreenOn = pm.isInteractive
		if (!isScreenOn) {
			@SuppressLint("InvalidWakeLockTag") 
			val wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUPor PowerManager.ON_AFTER_RELEASE, "MyLock")
			wl.acquire(10000)
			@SuppressLint("InvalidWakeLockTag") 
			val wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock")
			wl_cpu.acquire(10000)
		}
		if (remoteMessage.getData().size > 0) {
			scheduleJob(remoteMessage.getData())
		}
	}

		
	private fun scheduleJob(data: Map<String, String>) {
		Globals.pushData = data
		val work = OneTimeWorkRequest.Builder(FBWorker::class.java).build()
		WorkManager.getInstance().beginWith(work).enqueue()
	}

		
	public class FBWorker : Worker() {
		override fun doWork(): Result {
			// Utilise push data stored in global static variable
			val result: String = Globals.smartCallingManager.parsePushMessage(Globals.pushData) 
			return Result.success()
		}
	}
	```
	
5) If you can't or do not want to support the FCM push messaging solution you will need to implement a background worker that calls the SmartCallingManager.sync command. An example of this can be found in the HomeActivity of demo app:

	**JAVA**
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
					// You may want to log this
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
		
	**KOTLIN**
	```
	override fun onCreate(savedInstanceState: Bundle?) {
		...
		startWorker()
		...
	}

	private fun startWorker() {
		val mWorkManager = WorkManager.getInstance(applicationContext) 
		val constraints = Constraints.Builder()
			.setRequiredNetworkType(NetworkType.CONNECTED)
			.build()
		val workRequest = PeriodicWorkRequest.Builder(SCWorker::class.java, 15, TimeUnit.MINUTES)
			.setConstraints(constraints)
			.build()
		mWorkManager.enqueueUniquePeriodicWork("SmartComSync", ExistingPeriodicWorkPolicy.REPLACE, workRequest)
		mWorkManager.getWorkInfoByIdLiveData(workRequest.id)
			.observe(this, 
				Observer { info: WorkInfo? ->
					// You may want to log this
				}
			)
		}
	}

	public class SCWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
		override fun doWork(): Result {
			Globals.smartCallingManager.sync()
			return Result.success()
		}

		override fun onStopped() {
			super.onStopped()
		}
	}
	```

		
#### Congratulations, you are now ready to start using SmartCalling!
