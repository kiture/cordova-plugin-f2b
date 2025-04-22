package com.myf2b;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.os.Build;
import android.util.Log;
import android.database.Cursor;
import android.content.ContentResolver;
import android.provider.MediaStore;
import java.util.Set;

public class F2B extends CordovaPlugin {

	private static final String TAG = "F2BPlugin";
	private CallbackContext permissionCallbackContext;
	private static final int MANAGE_STORAGE_REQUEST_CODE = 199;

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		if (action.equals("isAccessibility")) {
			callbackContext.success(this.isAccessibility());
		} else if (action.equals("getAvailableRamSize")) {
			callbackContext.success(this.getAvailableRamSize());
		} else if (action.equals("getRamTotalSize")) {
			callbackContext.success(this.getRamTotalSize());
		} else if (action.equals("listExternalSdFiles")) {
			cordova.getThreadPool().execute(() -> listExternalSdFiles(callbackContext));
		} else if (action.equals("checkAllFilesAccess")) {
			checkAllFilesAccessPermission(callbackContext);
		} else if (action.equals("requestAllFilesAccess")) {
			requestAllFilesAccessPermission(callbackContext);
		} else {
			Log.w(TAG, "Nieznana akcja: " + action);
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

	private void listExternalSdFiles(CallbackContext callbackContext) {
		Context context = this.cordova.getActivity().getApplicationContext();
		ContentResolver contentResolver = context.getContentResolver();
		List<JSONObject> fileList = new ArrayList<>();
		Cursor cursor = null;
		String sdCardVolumeName = null;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			Set<String> volumeNames = MediaStore.getExternalVolumeNames(context);
			String primaryVolume = MediaStore.VOLUME_EXTERNAL_PRIMARY;
			for (String name : volumeNames) {
				if (!name.equals(primaryVolume)) {
					sdCardVolumeName = name;
					Log.d(TAG, "MediaStore: Found potential SD card volume (API 29+): " + sdCardVolumeName);
					break;
				}
			}
		}

		String selection = null;
		String[] selectionArgs = null;
		Uri queryUri;

		if (sdCardVolumeName != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			queryUri = MediaStore.Files.getContentUri(sdCardVolumeName);
			Log.d(TAG, "MediaStore: Using URI for volume: " + queryUri);
		} else {
			queryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
			Log.d(TAG, "MediaStore: Using general URI: " + queryUri);

			File sdCardRootDir = findSdCardRootPath(context);
			if (sdCardRootDir != null) {
				selection = MediaStore.Files.FileColumns.DATA + " LIKE ?";
				selectionArgs = new String[]{sdCardRootDir.getAbsolutePath() + "/%"};
				Log.d(TAG, "MediaStore: Using path filtering: " + selectionArgs[0]);
			} else {
				Log.w(TAG, "MediaStore: Cannot find root path of SD card to filter. All external files will be returned.");
			}
		}

		String[] projection = {
				MediaStore.Files.FileColumns._ID,
				MediaStore.Files.FileColumns.DISPLAY_NAME,
				MediaStore.Files.FileColumns.DATA,
				MediaStore.Files.FileColumns.MIME_TYPE,
				MediaStore.Files.FileColumns.SIZE,
				MediaStore.Files.FileColumns.DATE_ADDED
		};

		try {
			cursor = contentResolver.query(
					queryUri,
					projection,
					selection,
					selectionArgs,
					MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
			);

			if (cursor == null) {
				callbackContext.error("Cannot call MediaStore.");
				return;
			}

			int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
			int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
			int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
			int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
			int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
			int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED);

			while (cursor.moveToNext()) {
				JSONObject fileObject = new JSONObject();
				try {
					fileObject.put("id", cursor.getLong(idColumn));
					fileObject.put("name", cursor.getString(nameColumn));
					fileObject.put("path", cursor.getString(pathColumn));
					String mimeType = cursor.getString(mimeColumn);
					fileObject.put("mimeType", mimeType != null ? mimeType : "");
					fileObject.put("size", cursor.getLong(sizeColumn));
					fileObject.put("dateAdded", cursor.getLong(dateAddedColumn));
					fileList.add(fileObject);
				} catch (JSONException e) {
					Log.e(TAG, "MediaStore: Error creating JSON description.", e);
				}
			}

			JSONArray jsonFileList = new JSONArray(fileList);
			callbackContext.success(jsonFileList);

		} catch (Exception e) {
			Log.e(TAG, "MediaStore: Error during query.", e);
			callbackContext.error("Error during query to MediaStore: " + e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	private File findSdCardRootPath(Context context) {
		File[] externalDirs = context.getExternalFilesDirs(null);
		File sdCardAppSpecificDir = null;

		if (externalDirs == null || externalDirs.length == 0) return null;

		for (File dir : externalDirs) {
			if (dir != null) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					if (Environment.isExternalStorageRemovable(dir) && Environment.getExternalStorageState(dir).equals(Environment.MEDIA_MOUNTED)) {
						sdCardAppSpecificDir = dir;
						break;
					}
				} else {
					if (externalDirs.length > 1 && dir == externalDirs[1]) {
						if (externalDirs[0] == null || !dir.getAbsolutePath().equals(externalDirs[0].getAbsolutePath())) {
							if (dir.exists() && dir.canRead()) {
								sdCardAppSpecificDir = dir;
								break;
							}
						}
					}
				}
			}
		}

		if (sdCardAppSpecificDir != null) {
			String path = sdCardAppSpecificDir.getAbsolutePath();
			int androidDataIndex = path.indexOf("/Android/data");
			if (androidDataIndex != -1) {
				String rootPath = path.substring(0, androidDataIndex);
				File rootDir = new File(rootPath);
				if (rootDir.exists() && rootDir.isDirectory()) {
					Log.d(TAG, "Found root path of SD card: " + rootDir.getAbsolutePath());
					return rootDir;
				}
			}
			Log.w(TAG,"Cannot deduce root path from: " + path);
		}

		Log.w(TAG,"Cannot find root path of SD card.");
		return null;
	}

	private void checkAllFilesAccessPermission(CallbackContext callbackContext) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			boolean hasPermission = Environment.isExternalStorageManager();
			Log.d(TAG, "checkAllFilesAccess: " + hasPermission);
			callbackContext.success(hasPermission ? 1 : 0);
		} else {
			Log.d(TAG, "checkAllFilesAccess: Permission not applicable to API < 30");
			callbackContext.success(1);
		}
	}

	private void requestAllFilesAccessPermission(CallbackContext callbackContext) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			if (Environment.isExternalStorageManager()) {
				Log.d(TAG, "requestAllFilesAccess: Permission already granted.");
				callbackContext.success(1);
				return;
			}

			try {
				Context context = this.cordova.getActivity().getApplicationContext();
				Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
				Uri uri = Uri.fromParts("package", context.getPackageName(), null);
				intent.setData(uri);

				this.permissionCallbackContext = callbackContext;
				this.cordova.startActivityForResult(this, intent, MANAGE_STORAGE_REQUEST_CODE);

			} catch (Exception e) {
				Log.e(TAG, "Error during request for MANAGE_EXTERNAL_STORAGE permission", e);
				callbackContext.error("Error during opening permission settings: " + e.getMessage());
				this.permissionCallbackContext = null;
			}
		} else {
			Log.d(TAG, "requestAllFilesAccess: Permission not applicable to API < 30");
			callbackContext.success(1);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MANAGE_STORAGE_REQUEST_CODE) {
			Log.d(TAG, "onActivityResult: Returned from MANAGE_EXTERNAL_STORAGE settings");
			if (this.permissionCallbackContext != null) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
					boolean hasPermission = Environment.isExternalStorageManager();
					Log.d(TAG, "onActivityResult: Checked permission again: " + hasPermission);
					this.permissionCallbackContext.success(hasPermission ? 1 : 0);
				} else {
					this.permissionCallbackContext.success(1);
				}
				this.permissionCallbackContext = null;
			} else {
				Log.w(TAG, "onActivityResult: permissionCallbackContext was null!");
			}
		}
	}

}

