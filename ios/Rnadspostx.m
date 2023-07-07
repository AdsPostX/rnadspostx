//
//  AdsPostXWrapper.m
//  GoingNative
//
//  Created by shivang vyas on 25/11/22.
//

#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"
#import "React/RCTEventEmitter.h"

@interface RCT_EXTERN_REMAP_MODULE(adsPostXPlugin, Rnadspostx, NSObject)
RCT_EXTERN_METHOD(initWith: (NSString)accountId callback: (RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(load: (NSDictionary)attributes completion: (RCTResponseSenderBlock)completion)
RCT_EXTERN_METHOD(show:(int)presentationStyle isTransparent:(BOOL)isTransparent topMargin: (NSInteger)topMargin rightMargin:(NSInteger)rightMargin bottomMargin:(NSInteger)bottomMargin leftMargin:(NSInteger)leftMargin)

RCT_EXTERN_METHOD(setDebugLog:(int)isenabled)
RCT_EXTERN_METHOD(setTimeOut:(double)seconds)
RCT_EXTERN_METHOD(setEnvironment:(int)environment)
RCT_EXTERN_METHOD(getAttributes:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(getEnvironment:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(getOffers: (NSString)apiKey parameters: (NSDictionary)parameters completion: (RCTResponseSenderBlock)completion)

@end

