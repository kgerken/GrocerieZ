//
//  GlanceController.swift
//  GrocerieZ WatchKit Extension
//
//  Created by kage on 23.11.14.
//  Copyright (c) 2014 Zühlke Engineering. All rights reserved.
//

import WatchKit
import Foundation
import GrocerieZKit


class GlanceController: WKInterfaceController {
    
    @IBOutlet weak var totalItemsLabel: WKInterfaceLabel!
    
    let items:GKItems = GKItems()
    
    var timer:NSTimer!

    override init(context: AnyObject?) {
        // Initialize variables here.
        super.init(context: context)
        // Configure interface objects here.
        NSLog("%@ init", self)
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
        NSLog("%@ will activate", self)
        refresh()
        timer = NSTimer.scheduledTimerWithTimeInterval(1.0, target: self, selector: "refresh", userInfo: nil, repeats: true)
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        NSLog("%@ did deactivate", self)
        timer.invalidate()
        super.didDeactivate()
    }
    
    func refresh() {
        //println("timer refresh")
        items.refreshItems()
        totalItemsLabel.setText(String(items.count))
    }

}
