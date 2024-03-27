//
//  RNChangeIconModule.m
//  change_app_icon
//
//  Created by Bach Pham on 27/03/2024.
//

#import <Foundation/Foundation.h>
#import "RNChangeIconModule.h"

@implementation RNChangeIconModule
RCT_EXPORT_MODULE(RNChangeIcon); // The name we'll be using at JS side

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

RCT_REMAP_METHOD(getIcon, resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSString *currentIcon = [[UIApplication sharedApplication] alternateIconName];
        // Return the value as is.
        // - string: alternate app icon
        // - nil: primary (a.k.a. AppIcon) app icon
      if (currentIcon) {
        resolve(currentIcon); // AppIcon-Black, AppIcon-Premium
      } else {
        resolve(@"AppIcon");
      }
    });
}

RCT_REMAP_METHOD(changeIcon, iconName:(NSString *)iconName resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSError *error = nil;

        if ([[UIApplication sharedApplication] supportsAlternateIcons] == NO) {
            reject(@"Error", @"IOS:NOT_SUPPORTED", error);
            return;
        }

        NSString *currentIcon = [[UIApplication sharedApplication] alternateIconName];

        if ([iconName isEqualToString:currentIcon]) {
            reject(@"Error", @"IOS:ICON_ALREADY_USED", error);
            return;
        }

        NSString *newIconName;
        if (iconName == nil || [iconName length] == 0 || [iconName isEqualToString:@"Default"]) {
            newIconName = nil;
            resolve(@"Default");
        } else {
            newIconName = iconName;
            resolve(newIconName);
        }

        [[UIApplication sharedApplication] setAlternateIconName:newIconName completionHandler:^(NSError * _Nullable error) {
            return;
        }];
    });
}

@end


