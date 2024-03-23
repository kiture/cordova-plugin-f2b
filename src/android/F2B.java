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
		} else if (action.equals("getAvailableRamSize")) {
			callbackContext.success(this.getAvailableRamSize());
		} else if (action.equals("getRamTotalSize")) {
			callbackContext.success(this.getRamTotalSize());
		} else {
			return false;
		}
		return true;
	}

	private int isAccessibility() {		
		AccessibilityManager manager = (AccessibilityManager) cordova.getActivity().getSystemService(Context.ACCESSIBILITY_SERVICE);
		return manager.isEnabled() ? 1 : 0;
	}

	private int getRamTotalSize() {
		ActivityManager actManager = (ActivityManager) cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
		actManager.getMemoryInfo(memInfo);
		return (int) (memInfo.totalMem / 1024 / 1024);
	}

	private int getAvailableRamSize() {
		ActivityManager am = (ActivityManager) this.cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		return (int) (mi.availMem / 1024 / 1024);
	}


}

