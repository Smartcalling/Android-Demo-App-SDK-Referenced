package com.isk.iskdemo.Classes.WebService.Classes.T;

//region ** Imports **

import android.util.Log;
import com.isk.iskdemo.Classes.WebService.Classes.Base.ObjectBase;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;

//endregion


public class ToucanObject extends ObjectBase {

	//region ** Variables **

	public String Toucan;
	public int UserID;

	//endregion



	//region ** Constructors **

	public ToucanObject() {
		super();
	}

	//endregion


	//region ** JSON **

	@Override
	public void initWithJSON(String source) {
		try {
			JSONObject reader = new JSONObject(source);
			this.IsEmpty = false;

			this.UserID = reader.getInt("UserId");

			this.Toucan = reader.getString("access_token");
		}
		catch (JSONException ex) {
			this.IsEmpty = true;
			Log.d("ISK_Demo", Objects.requireNonNull(ex.getMessage()));
		}
	}

	//endregion


	//region ** Overrides **

	@Override
	public String serialize() {
		String result;

		JSONObject json = new JSONObject();
		try {
			json.put("Token", this.Toucan);
		}
		catch (JSONException ex) {
			Log.d("ISK_Demo", Objects.requireNonNull(ex.getMessage()));
		}

		result = json.toString();

		return result;
	}

	//endregion

}
