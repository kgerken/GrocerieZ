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

    @IBOutlet weak var itemsTable: WKInterfaceTable!
    
    var items:GKItems = GKItems()
    
    override init(context: AnyObject?) {
        // Initialize variables here.
        super.init(context: context)
        setTitle("GocerieZ")
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
        
        loadTableData()
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        NSLog("%@ did deactivate", self)
        super.didDeactivate()
    }
    
    private func loadTableData() {
        itemsTable.setNumberOfRows(items.count, withRowType: "groceriezWatchCell")
        for i in 0...items.count - 1 {
            let row = itemsTable.rowControllerAtIndex(i) as GrocerieZRowController
            row.cellLabel.setText(items[i])
        }
    }
    
    override func table(table: WKInterfaceTable, didSelectRowAtIndex rowIndex: Int) {
        items.removeAtIndex(rowIndex)
        items.save()
        loadTableData()
    }

}
