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

module.exports = F2B;
