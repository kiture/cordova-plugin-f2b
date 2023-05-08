//
//  F2B.m
#include <sys/sysctl.h>
#include <net/if.h>
#include <net/if_dl.h>
#import <sys/sysctl.h>
#import <mach/mach.h>
#import "F2B.h"
#import <Cordova/CDVPluginResult.h>
#import "GCDWebServer.h"
#import "GCDWebServerDataResponse.h"

@implementation F2B

- (void)startServer: (CDVInvokedUrlCommand *)command
{
    CDVPluginResult* pluginResult =  NULL;
    NSString* path = [command.arguments objectAtIndex:0];

    GCDWebServer* _webServer = [[GCDWebServer alloc] init];

    [_webServer addGETHandlerForBasePath:@"/" directoryPath:path indexFilename:@"index" cacheAge:3600 allowRangeRequests:YES];
    [_webServer startWithPort:8080 bonjourName:nil];

    NSString *url = [NSString stringWithFormat:@"%@", _webServer.serverURL];

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString: path];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)download: (CDVInvokedUrlCommand *)command
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        CDVPluginResult* pluginResult =  NULL;
        NSString* path = [command.arguments objectAtIndex:0];
        NSString* fileName = [command.arguments objectAtIndex:1];
        NSString* dirPath = [command.arguments objectAtIndex:2];

        NSURL  *url = [NSURL URLWithString:path];
        NSData *urlData = [NSData dataWithContentsOfURL:url];
        if ( urlData )
        {
            NSError *error;
            NSString *filePath = [NSString stringWithFormat:@"%@%@", dirPath,fileName];

            BOOL saveResult = [urlData writeToFile:filePath options:NSDataWritingAtomic error:&error];

            NSFileManager *man = [NSFileManager defaultManager];
            NSDictionary *attrs = [man attributesOfItemAtPath: filePath error: NULL];
            UInt32 result = [attrs fileSize];
            NSString *str = [NSString stringWithFormat:@"%d", result];

            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString: str];
        } 
        else 
        {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"Error downloading file"];
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    });
}


@end
