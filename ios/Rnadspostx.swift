//
//  AdsPostXWrapper.swift
//  GoingNative
//
//  Created by shivang vyas on 25/11/22.
//

import Foundation
import React
import AdsPostX

@objc(Rnadspostx)
class Rnadspostx: RCTEventEmitter {
  
  @objc override static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  override func supportedEvents() -> [String]! {
    return [Event.onShow, Event.onError,Event.onDismiss]
  }
  
  @objc func initWith(_ accountId: String,  callback: @escaping RCTResponseSenderBlock) {
   
    var jsonObject: [String: Any] = [:]
    
    AdsPostx.initWith(accountId: accountId) { result in
      switch(result) {
      case .success():
        jsonObject[ResponseKeys.status] = true
        callback([jsonObject])
      case .failure(let error):
        jsonObject[ResponseKeys.status] = false
        if let error = error as? AdsPostxError {
          jsonObject[ResponseKeys.error] = error.description
        }
        callback([jsonObject])
      }
    }
  }
  
  @objc func load(_ attributes: [String: Any], completion: @escaping RCTResponseSenderBlock) {
    var jsonObject: [String: Any] = [:]
    
    AdsPostx.load(attributes: attributes) { result in
      switch(result) {
      case .success():
        jsonObject[ResponseKeys.status] = true
        completion([jsonObject])
      case .failure(let error):
        jsonObject[ResponseKeys.status] = false
        if let error = error as? AdsPostxError {
          jsonObject[ResponseKeys.error] = error.description
        }
        completion([jsonObject])
      }
    }

  }
  
  @objc func show(_ presentationStyle: Int, isTransparent: Bool, topMargin: Int, rightMargin: Int, bottomMargin: Int, leftMargin: Int) {
    var style = OfferPresentationStyle.popup // default set to popup.
    if(presentationStyle == 1) {
      style = OfferPresentationStyle.fullScreen
    }
    
    AdsPostx.showOffers(presentationStyle: style, transparent: isTransparent,
                        margins:
                          (UInt(topMargin >= 0 ? topMargin: 0),
                           UInt(bottomMargin >= 0 ? bottomMargin: 0),
                           UInt(leftMargin >= 0 ? leftMargin: 0),
                           UInt(rightMargin >= 0 ? rightMargin: 0)
                          )) { [weak self] in
      // on show
      self?.sendEvent(withName: Event.onShow, body: [true])
    } onError: { [weak self] error in
      // on error
      self?.sendEvent(withName: Event.onError, body: [error.description])
    } onDismiss: { [weak self] in
      // on dismiss
      self?.sendEvent(withName: Event.onDismiss, body: [true])
    }
  }
  
  @objc func setDebugLog(_ isenabled: Bool) {
    AdsPostx.EnableDebugLog(isenabled)
  }
  
  @objc func setEnvironment(_ environment: Int) {
    if(environment == 1) {
      AdsPostx.SetEnvironment(AdPostxEnvironment.test)
    } else {
      AdsPostx.SetEnvironment(AdPostxEnvironment.live)
    }
  }
  
}

