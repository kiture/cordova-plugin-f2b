/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

#import "CDVViewController+Orientation.h"
#import "CDVOrientation.h"
#import <objc/runtime.h>

@implementation CDVViewController (Orientation)

+ (void)load
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        Class class = [self class];
        
        SEL originalSelector = @selector(supportedInterfaceOrientations);
        SEL swizzledSelector = @selector(cdv_supportedInterfaceOrientations);
        
        Method originalMethod = class_getInstanceMethod(class, originalSelector);
        Method swizzledMethod = class_getInstanceMethod(class, swizzledSelector);
        
        BOOL didAddMethod = class_addMethod(class,
                                            originalSelector,
                                            method_getImplementation(swizzledMethod),
                                            method_getTypeEncoding(swizzledMethod));
        
        if (didAddMethod) {
            class_replaceMethod(class,
                                swizzledSelector,
                                method_getImplementation(originalMethod),
                                method_getTypeEncoding(originalMethod));
        } else {
            method_exchangeImplementations(originalMethod, swizzledMethod);
        }
        
        // Also swizzle shouldAutorotate
        SEL originalAutorotateSelector = @selector(shouldAutorotate);
        SEL swizzledAutorotateSelector = @selector(cdv_shouldAutorotate);
        
        Method originalAutorotateMethod = class_getInstanceMethod(class, originalAutorotateSelector);
        Method swizzledAutorotateMethod = class_getInstanceMethod(class, swizzledAutorotateSelector);
        
        BOOL didAddAutorotateMethod = class_addMethod(class,
                                                      originalAutorotateSelector,
                                                      method_getImplementation(swizzledAutorotateMethod),
                                                      method_getTypeEncoding(swizzledAutorotateMethod));
        
        if (didAddAutorotateMethod) {
            class_replaceMethod(class,
                                swizzledAutorotateSelector,
                                method_getImplementation(originalAutorotateMethod),
                                method_getTypeEncoding(originalAutorotateMethod));
        } else {
            method_exchangeImplementations(originalAutorotateMethod, swizzledAutorotateMethod);
        }
    });
}

- (UIInterfaceOrientationMask)cdv_supportedInterfaceOrientations
{
    // Try to get the orientation plugin
    id orientationPlugin = [self getCommandInstance:@"CDVOrientation"];
    
    if (orientationPlugin && [orientationPlugin respondsToSelector:@selector(supportedInterfaceOrientations)]) {
        return [orientationPlugin supportedInterfaceOrientations];
    }
    
    // Fall back to the original implementation
    return [self cdv_supportedInterfaceOrientations];
}

- (BOOL)cdv_shouldAutorotate
{
    // Always return YES to allow rotation based on supported orientations
    return YES;
}

@end

