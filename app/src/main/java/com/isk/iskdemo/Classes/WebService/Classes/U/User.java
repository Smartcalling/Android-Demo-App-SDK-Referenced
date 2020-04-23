package com.isk.iskdemo.Classes.WebService.Classes.U;

//region ** Imports **

import android.util.Log;
import com.isk.iskdemo.Classes.Globals.Globals;
import com.isk.iskdemo.Classes.WebService.Classes.Base.ObjectBase;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;

//endregion


public class User extends ObjectBase {

	//region ** Properties **

	public int UserID;
	public String UserName;
	public String EmailAddress;
	public String FirstName;
	public String LastName;
	public String Phone;
	public String UniqueId;

	//endregion


	//region ** Constructors **

	public User() {
		super();
	}

	//endregion


	//region ** JSON **

	@Override
	public void initWithJSON(String source) {
		try {
			JSONObject reader = new JSONObject(source);

			this.IsEmpty = false;
			this.UserID = reader.getInt("Id");

			this.UserName = reader.getString("UserName");
			this.EmailAddress = reader.getString("Email");
			this.FirstName = reader.getString("FirstName");
			this.LastName = reader.getString("LastName");
			this.Phone = reader.getString("Phone");
			this.UniqueId = reader.getString("UniqueId");
		}
		catch (JSONException ex) {
			this.IsEmpty = true;
			Log.d("ISK_Demo", Objects.requireNonNull(ex.getMessage()));
		}
	}

	//endregion


	//region ** Misc **

	public String getFullName() {
		String result = "";

		if (!Globals.IsNullOrEmpty(this.FirstName)) {
			result = this.FirstName;
		}
		if (!Globals.IsNullOrEmpty(this.LastName)) {
			if (Globals.IsNullOrEmpty(result)) {
				result = this.LastName;
			}
			else {
				result += " " + this.LastName;
			}
		}

		if (Globals.IsNullOrEmpty(result)) {
			result = "< No Name >";
		}

		return result;
	}

	//endregion

}
