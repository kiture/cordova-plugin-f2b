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

/**
 * Activates an exclusive AVAudioSession so iOS silences / interrupts audio
 * playing in all other applications. Call restoreAudioSession when the app
 * no longer needs exclusive audio focus.
 */
- (void)silenceOtherApps:(CDVInvokedUrlCommand*)command;

/**
 * Deactivates the exclusive AVAudioSession and notifies other applications
 * (e.g. Music, Spotify) that they may resume playback.
 */
- (void)restoreAudioSession:(CDVInvokedUrlCommand*)command;

@end
