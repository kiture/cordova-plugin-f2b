//
//  F2B.h
//


#ifndef F2B_h
#define F2B_h


#endif /* F2B_h */
#import <Cordova/CDVPlugin.h>
#import "GCDWebServer.h"

@interface F2B : CDVPlugin

- (void)download:(CDVInvokedUrlCommand*)command;

- (void)startServer:(CDVInvokedUrlCommand*)command;

- (void)isAccessibility:(CDVInvokedUrlCommand*)command;

- (void)getAvailableRamSize:(CDVInvokedUrlCommand*)command;

- (void)getRamTotalSize:(CDVInvokedUrlCommand*)command;

@end
