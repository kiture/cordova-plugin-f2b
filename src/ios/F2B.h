//
//  F2B.h
//


#ifndef F2B_h
#define F2B_h


#endif /* F2B_h */
#import <Cordova/CDVPlugin.h>
#import <GCDWebServer/GCDWebServer.h>
#import <GCDWebServer/GCDWebServerDataRequest.h>
#import <GCDWebServer/GCDWebServerDataResponse.h>
#import <GCDWebServer/GCDWebServerFileResponse.h>
#import <GCDWebServer/GCDWebServerResponse.h>
#import <GCDWebServer/GCDWebServerRequest.h>

@interface F2B : CDVPlugin

- (void)download:(CDVInvokedUrlCommand*)command;

@end
