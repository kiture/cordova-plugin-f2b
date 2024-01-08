package com.myf2b;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActivityManager;
import android.content.Context;
import android.view.accessibility.AccessibilityManager;

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

	private int isAccessibility() {		
		AccessibilityManager manager = (AccessibilityManager) cordova.getActivity().getSystemService(Context.ACCESSIBILITY_SERVICE);
		return manager.isEnabled() ? 1 : 0;
	}


}

