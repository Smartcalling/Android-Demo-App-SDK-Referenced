package com.isk.iskdemo.Classes.WebService.Classes.C;

//region ** Imports **

import android.util.Log;

import com.isk.iskdemo.Classes.Globals.Globals;
import com.isk.iskdemo.Classes.WebService.Classes.Base.ObjectBase;
import com.isk.iskdemo.Classes.WebService.General.WS_Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

//endregion


public class CommResult extends ObjectBase {

	//region ** Properties **

	public WS_Result.enumCommResult ResultValue;
	public String ResultMessage;
	public String APIMethod;
	public String InnerException;
	public String EntityValidation;
	public int ResultId;
	public String FunctionName;

	//endregion


	//region ** Constructors **

	public CommResult() {
		super();
	}

	//endregion


	//region ** JSON **

	@Override
	public void initWithJSON(String source) {
		try {
			JSONObject reader = new JSONObject(source);

			this.IsEmpty = false;

			this.ResultValue = WS_Result.enumCommResult.fromId(reader.getInt("ResultValue"));
			this.ResultId = reader.getInt("ResultId");

			this.ResultMessage = reader.getString("ResultMessage");
			this.APIMethod = reader.getString("APIMethod");
			this.InnerException = reader.getString("InnerException");
			this.EntityValidation = reader.getString("EntityValidation");
			this.FunctionName = reader.getString("FunctionName");
		}
		catch (JSONException ex) {
			this.IsEmpty = true;
			Log.d("ISK_Demo", Objects.requireNonNull(ex.getMessage()));
		}
	}

	//endregion


	//region ** Misc **

	//endregion

}
