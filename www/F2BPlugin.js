var exec = require('cordova/exec');
"use strict";

var F2B = {};

F2B.download = function (success, fail, path, fileName, dirPath) {
  exec(success, fail, "F2B", "download", [path, fileName, dirPath]);
} 

F2B.startServer = function (success, fail, path) {
  exec(success, fail, "F2B", "startServer", [path]);
} 

module.exports = F2B;