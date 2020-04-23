package com.isk.iskdemo.Classes.WebService.Classes.Base;

//region ** Imports **

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Objects;

//endregion



public class ObjectBase {

	//region ** Properties **

	public Boolean IsEmpty = true;

	//endregion


	//region ** Constructors **

	public ObjectBase() {

	}

	//endregion



	//region ** Overridables **

	public void initWithJSON(String source) {

	}

	public String serialize() {
		return "";
	}

	//endregion



	//region ** Protected **

	public static ArrayList<?> getListOfObjects(Class outParam, JSONArray source) {
		ArrayList<ObjectBase> result = new ArrayList<>();

		for (int index = 0; index < source.length(); index++) {
			try {
				JSONObject object = source.getJSONObject(index);
				ObjectBase ob;
				try {
					ob = (ObjectBase)outParam.newInstance();
					if (ob != null) {
						ob.initWithJSON(object.toString());
						result.add(ob);
					}
				}
				catch (InstantiationException | IllegalAccessException ex) {
					Log.d("ISK_Demo", Objects.requireNonNull(ex.getMessage()));
				}
			}
			catch (JSONException e) {
				Log.d("ISK_Demo", Objects.requireNonNull(e.getMessage()));
			}
		}

		return result;
	}

	//endregion

}
