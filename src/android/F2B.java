package com.myf2b;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActivityManager;
import android.content.Context;

public class F2B extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		if (action.equals("isAccessibility")) {
			callbackContext.success(this.isAccessibility());
		} else {
			return false;
		}
		return true;
	}

	private Boolean isAccessibility() {		
		return false;
	}


}

