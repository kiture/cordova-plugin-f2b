var exec = require('cordova/exec');
"use strict";

var F2B = {};

F2B.download = function (success, fail, path, fileName, dirPath) {
  exec(success, fail, "F2B", "download", [path, fileName, dirPath]);
} 

module.exports = F2B;