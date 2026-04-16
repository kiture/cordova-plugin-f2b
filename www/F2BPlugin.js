var exec = require('cordova/exec');
"use strict";

var F2B = {};

F2B.download = function (success, fail, path, fileName, dirPath) {
  exec(success, fail, "F2B", "download", [path, fileName, dirPath]);
} 

F2B.startServer = function (success, fail, path) {
  exec(success, fail, "F2B", "startServer", [path]);
} 

F2B.isAccessibility = function (success, fail) {
  exec(success, fail, "F2B", "isAccessibility", []);
}

F2B.getAvailableRamSize = function(success, fail) {
  exec(success, fail, "F2B", "getAvailableRamSize", []);
}

F2B.getRamTotalSize = function(success, fail) {
  exec(success, fail, "F2B", "getRamTotalSize", []);
}

/**
 * iOS only.
 * Activates an exclusive AVAudioSession (AVAudioSessionCategoryPlayback) so
 * iOS interrupts / silences audio playing in all other applications such as
 * Music, Spotify or podcasts.
 *
 * Call restoreAudioSession() when the app no longer needs exclusive focus so
 * interrupted apps can resume their playback.
 *
 * @param {Function} success - called with no arguments on success
 * @param {Function} fail    - called with an error message string on failure
 */
F2B.silenceOtherApps = function(success, fail) {
  exec(success, fail, "F2B", "silenceOtherApps", []);
}

/**
 * iOS only.
 * Deactivates the exclusive AVAudioSession and notifies other applications
 * (e.g. Music, Spotify) that they may resume their playback.
 *
 * @param {Function} success - called with no arguments on success
 * @param {Function} fail    - called with an error message string on failure
 */
F2B.restoreAudioSession = function(success, fail) {
  exec(success, fail, "F2B", "restoreAudioSession", []);
}

F2B.listExternalSdFiles = function(success, fail) {
  exec(success, fail, "F2B", "listExternalSdFiles", []);
}

F2B.checkAllFilesAccess = function(success, fail) {
  exec(success, fail, "F2B", "checkAllFilesAccess", []);
}

F2B.requestAllFilesAccess = function(success, fail) {
  exec(success, fail, "F2B", "requestAllFilesAccess", []);
}

module.exports = F2B;
