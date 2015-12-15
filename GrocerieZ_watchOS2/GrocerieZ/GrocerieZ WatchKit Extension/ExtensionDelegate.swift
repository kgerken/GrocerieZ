//
//  ExtensionDelegate.swift
//  GrocerieZ WatchKit Extension
//
//  Created by kage on 02.12.15.
//  Copyright © 2015 Zühlke Engineering. All rights reserved.
//

import WatchKit

class ExtensionDelegate: NSObject, WKExtensionDelegate {
    
    /* Data is only stored in memory,
       implement Core Data for persistence */

    // Current shopping list items
    var items = [String]()
    
    // Shopping list history entries
    var history = [HistoryEntry]()

    func applicationDidFinishLaunching() {
        // Perform any final initialization of your application.
    }

    func applicationDidBecomeActive() {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillResignActive() {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, etc.
    }
    
    func addItem(item: String) {
        if (!items.contains(item)) {
            items.append(item);
        }
    }
    
    func removeItem(item: String) {
        if (items.contains(item)) {
            items.removeAtIndex(items.indexOf(item)!)
        }
    }

}
