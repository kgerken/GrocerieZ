//
//  GKItems.swift
//  GrocerieZ
//
//  Created by Zühlke on 23.11.14.
//  Copyright (c) 2014 Zühlke Engineering. All rights reserved.
//

import Foundation

public class GKItems {
    
    private var items: [String] = []
    private let userDefaults: NSUserDefaults = NSUserDefaults(suiteName: "group.com.zuehlke.GrocerieZWatchTest")!
    
    public var count: Int {
        return items.count
    }
    
    public var description: String {
        return items.description
    }
    
    public init() {
        if let defaultsItems = userDefaults.arrayForKey("items")? {
            for defaultItem in defaultsItems {
                items.append(defaultItem as String)
            }
        }
    }
    
    public subscript(index: Int) -> String {
        get {
            return items[index]
        }
        set(newValue) {
            items[index] = newValue
        }
    }
    
    public func add(item: String) {
        items.append(item)
    }
    
    public func removeAtIndex(index: Int) {
        items.removeAtIndex(index)
    }
    
    public func save() {
        userDefaults.setObject(items, forKey: "items")
        userDefaults.synchronize()
    }
    
}