package com.isk.iskdemo.Classes.WebService.General;

//region ** Imports **

import com.isk.iskdemo.Classes.WebService.Classes.Base.ObjectBase;
import java.util.ArrayList;

//endregion


public class WS_Result {

	//region ** Variables & Enums **

	@SuppressWarnings("SpellCheckingInspection")
	public enum enumWSResult {
		ERROR,
		SUCCESS,
		INVALIDLOGIN,
		NOVALUE,
		RETRY
	}

	public enum enumCommResult {
		NoValue(-1),
		Success(1),
		NoAccess(2),
		ItemExists(4),
		InvalidParameter(5),
		ItemDoesNotExist(6),
		ItemInUse(7),
		NoConnection(8),
		TooManyRecords(9),
		ErrorOccurred(99);

		private final int value;

		enumCommResult(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static enumCommResult fromId(int value) {
			for(enumCommResult iType : values()) {
				if (iType.value == value) {
					return iType;
				}
			}
			return NoValue;
		}
	}


	public final enumWSResult result;
	public final String xData;
	public final ArrayList<?> xArray;
	public final ObjectBase xObject;

	//endregion



	//region ** Constructor **

	public WS_Result(enumWSResult res, String data, ArrayList<?> array, ObjectBase object) {
		result = res;
		xData = data;
		xArray = array;
		xObject = object;
	}

	//endregion

}
