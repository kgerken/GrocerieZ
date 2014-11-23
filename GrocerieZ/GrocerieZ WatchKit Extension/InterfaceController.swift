//
//  InterfaceController.swift
//  GrocerieZ WatchKit Extension
//
//  Created by kage on 23.11.14.
//  Copyright (c) 2014 Zühlke Engineering. All rights reserved.
//

import WatchKit
import Foundation
import GrocerieZKit

class InterfaceController: WKInterfaceController {

    @IBOutlet weak var testLabel: WKInterfaceLabel!
    
    var items:GKItems = GKItems()
    
    override init(context: AnyObject?) {
        // Initialize variables here.
        super.init(context: context)
        //NSNotificationCenter.defaultCenter().addObserver(self, selector: "appItemsChanged:", name: NSUserDefaultsDidChangeNotification, object: nil)
        //userDefaults.addObserver(self, forKeyPath: "items", options: NSKeyValueObservingOptions.New, context: nil)
        // Configure interface objects here.
        NSLog("%@ init", self)
    }
    
    /*override func observeValueForKeyPath(keyPath: String, ofObject object: AnyObject, change: [NSObject : AnyObject], context: UnsafeMutablePointer<Void>) {
        NSLog("App items changed!!!")
    }*/
    
    func appItemsChanged(notification: NSNotification) {
        NSLog("App items changed!")
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
        NSLog("%@ will activate", self)
        
        testLabel.setText(items.description)
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        NSLog("%@ did deactivate", self)
        super.didDeactivate()
    }

}
