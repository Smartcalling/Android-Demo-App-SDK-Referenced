package com.isk.iskdemo.Classes.WebService.General;

//region ** Imports **

import android.util.Log;

import com.isk.iskdemo.Classes.Globals.Globals;
import com.isk.iskdemo.Classes.Interfaces.iService;
import com.isk.iskdemo.Classes.WebService.Classes.Base.ObjectBase;
import com.isk.iskdemo.Classes.WebService.Classes.C.CommResult;
import com.isk.iskdemo.Classes.WebService.Classes.T.ToucanObject;
import com.isk.iskdemo.Classes.WebService.Classes.U.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.AbstractHttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

//endregion


public class WS_ISK {

	//region ** Variables **

	//endregion


	//region ** Auth & Login **

	public static void GetToucan(String userName, String password, final iService finish) {
		Map<String, String> params = new HashMap<>();
		params.put("Path", "token");

		StringEntity entity = null;
		RequestParams dataParams = new RequestParams();
		dataParams.put("grant_type", "password");
		dataParams.put("username", userName);
		dataParams.put("password", password);
		try {
			entity = new StringEntity(dataParams.toString());
		}
		catch (UnsupportedEncodingException ex) {
		}
		entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));

		doPost(params, null, ToucanObject.class, false, entity, new iService() {
			@Override
			public void complete(WS_Result result) {
				finish.complete(result);
			}
		});
	}


	public static void GetUser(final int UserID, final iService finish) {
		Map<String, String> params = new HashMap<>();
		params.put("Path", "User/GetUser");
		params.put("UserId", Integer.toString(UserID));

		doGet(params, User.class, true, false, 3, new iService() {
			@Override
			public void complete(WS_Result result) {
				finish.complete(result);
			}
		});
	}


	public static void PostPushToken(final int UserID, String token, final iService finish) {
		Map<String, String> params = new HashMap<>();
		params.put("Path", "User/PostAndroidPushToken");
		params.put("UserId", Integer.toString(UserID));
		params.put("Token", token);

		doPost(params, null, CommResult.class, true, null, finish::complete);
	}

	//endregion


	//region ** Logs **

	public static void AddLog(final int UserId, final String Message) {
		try {
			Map<String, String> params = new HashMap<>();
			params.put("Path", "Log");
			params.put("UserId", "" + UserId);
			params.put("Message", URLEncoder.encode(Message, StandardCharsets.UTF_8.toString()));

			doPostSync(params);
		}
		catch (Exception ex) {
			Log.d("ISKDemo", "AddLog error: " + ex.getMessage());
		}
	}


	public static void AddLogASync(final int UserId, final String Message) {
		try {
			Map<String, String> params = new HashMap<>();
			params.put("Path", "Log");
			params.put("UserId", "" + UserId);
			params.put("Message", URLEncoder.encode(Message, StandardCharsets.UTF_8.toString()));

			doPost(params, null, String.class, true, null, new iService() {
				@Override
				public void complete(WS_Result result) {
				}
			});
		}
		catch (UnsupportedEncodingException ex) {
		}
	}

	//endregion


	//region ** Big Stuff **

	@SuppressWarnings("TryWithIdenticalCatches")
	private static void doGet(Map<String, String> params, final Class out, Boolean withToucan, final Boolean isArray, final int attempt, final iService finish) {
		StringBuilder stringBuilder = new StringBuilder();
		Boolean first = true;
		for (String key : params.keySet()) {
			if (key.equals("Path")) {
				stringBuilder.insert(0, params.get(key));
			}
			else {
				if (first) {
					stringBuilder.append("?");
					stringBuilder.append(key);
					stringBuilder.append("=");
					stringBuilder.append(params.get(key));
				}
				else {
					stringBuilder.append("&");
					stringBuilder.append(key);
					stringBuilder.append("=");
					stringBuilder.append(params.get(key));
				}
				first = false;
			}
		}

		stringBuilder.insert(0, Globals.apiURL);

		final AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Accept", "application/json");
		if (withToucan) {
			client.addHeader("Authorization", "Bearer " + Globals.Toucan.Toucan);
		}

		client.setResponseTimeout(600000);
		client.setConnectTimeout(600000);

		client.get(stringBuilder.toString(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String response = "";

				response = new String(responseBody, StandardCharsets.UTF_8);

				WS_Result result;
				if (! Globals.IsNullOrEmpty(response)) {
					if (response.startsWith("ERROR#") || response.contains("ERROR#")) {
						result = new WS_Result(WS_Result.enumWSResult.ERROR, response, null, null);
					}
					else {
						if (out == null) {
							response = response.replace("SUCCESS#", "");
							result = new WS_Result(WS_Result.enumWSResult.SUCCESS, response, null, null);
						}
						else if (out.getSuperclass() == ObjectBase.class) {
							if (isArray) {
								try {
									JSONArray array = new JSONArray(response);
									ArrayList<?> ret = ObjectBase.getListOfObjects(out, array);
									if (ret != null) {
										result = new WS_Result(WS_Result.enumWSResult.SUCCESS, "", ret, null);
									}
									else {
										result = new WS_Result(WS_Result.enumWSResult.ERROR, "Could Not Create Object", null, null);
									}
								}
								catch (JSONException ex){
									Log.d("ISK_Demo", Objects.requireNonNull(ex.getMessage()));
									result = new WS_Result(WS_Result.enumWSResult.ERROR, "Could Not Create Object", null, null);
								}
							}
							else {
								ObjectBase ob = null;
								try {
									ob = (ObjectBase)out.newInstance();
								}
								catch (InstantiationException ex) {
									Log.d("ISK_Demo", Objects.requireNonNull(ex.getMessage()));
								}

								catch (IllegalAccessException ex2) {
									Log.d("ISK_Demo", Objects.requireNonNull(ex2.getMessage()));
								}
								if (ob != null) {
									ob.initWithJSON(response);
									result = new WS_Result(WS_Result.enumWSResult.SUCCESS, "", null, ob);
								}
								else {
									result = new WS_Result(WS_Result.enumWSResult.ERROR, "Could Not Create Object", null, null);
								}
							}
						}
						else if (out == String.class) {
							response = response.replace("SUCCESS#", "");
							response = response.replace("\"", "");
							result = new WS_Result(WS_Result.enumWSResult.SUCCESS, response, null, null);
						}
						else if (out == Boolean.class) {
							result = new WS_Result(WS_Result.enumWSResult.SUCCESS, "", null, null);
						}
						else {
							result = new WS_Result(WS_Result.enumWSResult.SUCCESS, "", null, null);
						}
					}
				}
				else {
					result = new WS_Result(WS_Result.enumWSResult.NOVALUE, "", null, null);
				}

				finish.complete(result);
			}


			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if (attempt > 3) {
					WS_Result result = new WS_Result(WS_Result.enumWSResult.ERROR, error.toString(), null, null);
					finish.complete(result);
				}
				else {
					WS_Result result = new WS_Result(WS_Result.enumWSResult.RETRY, "", null, null);
					finish.complete(result);
				}
			}
		});
	}


	private static void doPost(Map<String, String> params, ObjectBase source, final Class out, boolean withToucan, StringEntity stringEntity, final iService finish) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : params.keySet()) {
			if (key.equals("Path")) {
				sb.insert(0, params.get(key));
			}
			else {
				if (first) {
					sb.append("?");
				}
				else {
					sb.append("&");
				}
				sb.append(key);
				sb.append("=");
				sb.append(params.get(key));
				first = false;
			}
		}

		sb.insert(0, Globals.apiURL);

		final AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Accept", "application/json");
		if (withToucan) {
			client.addHeader("Authorization", "Bearer " + Globals.Toucan.Toucan);
		}

		AbstractHttpEntity absEntity = null;
		if (stringEntity == null) {
			if (source != null) {
				ByteArrayEntity entity = null;
				entity = new ByteArrayEntity(source.serialize().getBytes(StandardCharsets.UTF_8));
				entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				absEntity = entity;
			}
		}
		else {
			absEntity = stringEntity;
		}

		client.setResponseTimeout(600000);
		client.setConnectTimeout(600000);

		if (source == null && stringEntity == null) {
			client.post(Globals.currentActivity, sb.toString(), null, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String response = "";
					response = new String(responseBody, StandardCharsets.UTF_8);

					interpretPost(response, out, new iService() {
						@Override
						public void complete(WS_Result result) {
							finish.complete(result);
						}
					});
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					WS_Result result = new WS_Result(WS_Result.enumWSResult.RETRY, "", null, null);
					finish.complete(result);
				}
			});
		}
		else {
			client.post(Globals.currentActivity, sb.toString(), absEntity, "application/json", new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String response = "";
					response = new String(responseBody, StandardCharsets.UTF_8);

					interpretPost(response, out, new iService() {
						@Override
						public void complete(WS_Result result) {
							finish.complete(result);
						}
					});
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					WS_Result result = new WS_Result(WS_Result.enumWSResult.RETRY, "", null, null);
					finish.complete(result);
				}
			});
		}
	}


	private static void doPostSync(Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : params.keySet()) {
			if (key.equals("Path")) {
				sb.insert(0, params.get(key));
			}
			else {
				if (first) {
					sb.append("?");
				}
				else {
					sb.append("&");
				}
				sb.append(key);
				sb.append("=");
				sb.append(params.get(key));
				first = false;
			}
		}

		sb.insert(0, Globals.apiURL);

		final SyncHttpClient client = new SyncHttpClient();
		client.addHeader("Accept", "application/json");
//		client.addHeader("Authorization", "Bearer " + Globals.Toucan.Toucan);

		client.setResponseTimeout(600000);
		client.setConnectTimeout(600000);

		client.post(sb.toString(), new TextHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
			}
		});
	}


	private static void interpretPost(String response, final Class out, final iService finish) {
		WS_Result result;
		if (! Globals.IsNullOrEmpty(response)) {
			if (response.startsWith("ERROR#") || response.contains("ERROR#")) {
				result = new WS_Result(WS_Result.enumWSResult.ERROR, response, null, null);
			}
			else {
				if (out == null) {
					response = response.replace("SUCCESS#", "");
					response = response.replace("\"", "");
					result = new WS_Result(WS_Result.enumWSResult.SUCCESS, response, null, null);
				}
				else if (out.getSuperclass() == ObjectBase.class) {
					ObjectBase ob = null;
					try {
						ob = (ObjectBase)out.newInstance();
					}
					catch (InstantiationException | IllegalAccessException ex) {
						Log.d("ISK_Demo", Objects.requireNonNull(ex.getMessage()));
					}
					if (ob != null) {
						ob.initWithJSON(response);
						result = new WS_Result(WS_Result.enumWSResult.SUCCESS, "", null, ob);
					}
					else {
						result = new WS_Result(WS_Result.enumWSResult.ERROR, "Could Not Create Object", null, null);
					}
				}
				else if (out == String.class) {
					response = response.replace("SUCCESS#", "");
					response = response.replace("\"", "");
					result = new WS_Result(WS_Result.enumWSResult.SUCCESS, response, null, null);
				}
				else if (out == Boolean.class) {
					result = new WS_Result(WS_Result.enumWSResult.SUCCESS, "", null, null);
				}
				else {
					result = new WS_Result(WS_Result.enumWSResult.SUCCESS, "", null, null);
				}
			}
		}
		else {
			result = new WS_Result(WS_Result.enumWSResult.NOVALUE, "", null, null);
		}

		finish.complete(result);
	}

	//endregion

}
