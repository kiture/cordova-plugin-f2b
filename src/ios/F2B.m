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

- (void)isAccessibility: (CDVInvokedUrlCommand *)command
{
    BOOL accessibilityOn = UIAccessibilityIsVoiceOverRunning();
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool: accessibilityOn];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getRamTotalSize: (CDVInvokedUrlCommand *)command
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt: ([NSProcessInfo processInfo].physicalMemory / 1024 / 1024)];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getAvailableRamSize: (CDVInvokedUrlCommand *)command
{
    mach_port_t host_port;  
    mach_msg_type_number_t host_size;  
    vm_size_t pagesize;  
    host_port = mach_host_self();  
    host_size = sizeof(vm_statistics_data_t) / sizeof(integer_t);  
    host_page_size(host_port, &pagesize);  
    vm_statistics_data_t vm_stat;  
    if (host_statistics(host_port, HOST_VM_INFO, (host_info_t)&vm_stat, &host_size) != KERN_SUCCESS)  
    {  
        NSLog(@"Failed to fetch vm statistics");  
    }  
    natural_t mem_free = vm_stat.free_count * pagesize; 

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt: (mem_free / 1024 / 1024)];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

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
