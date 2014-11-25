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
    private var itemAmounts: [Int] = []
    private let userDefaults: NSUserDefaults = NSUserDefaults(suiteName: "group.com.zuehlke.GrocerieZWatchTest")!
    
    public var count: Int {
        return items.count
    }
    
    public var description: String {
        return items.description
    }
    
    public init() {
        refreshItems()
    }
    
    public func refreshItems() {
        items.removeAll(keepCapacity: false)
        itemAmounts.removeAll(keepCapacity: false)

        if let defaultsItems = userDefaults.arrayForKey("items")? {
            for defaultItem in defaultsItems {
                items.append(defaultItem as String)
            }
        }
        if let defaultsItems = userDefaults.arrayForKey("itemAmounts")? {
            for defaultItem in defaultsItems {
                itemAmounts.append(defaultItem as Int)
            }
        }
    }
    
    public func setAmount(amount: Int, forIndex index: Int) {
        itemAmounts[index] = amount
    }
    
    public func getAmount(index: Int) -> Int {
        return itemAmounts[index]
    }
    
    public func getItemName(index: Int) -> String {
        return items[index]
    }
    
    public subscript(index: Int) -> String {
        get {
            var result:String = items[index]
            let amount = getAmount(index)
            if amount > 1 {
                result = "\(amount) x \(result)"
            }
            return result
        }
        set(newValue) {
            items[index] = newValue
        }
    }
    
    public func add(item: String) {
        items.append(item)
        itemAmounts.append(1)
    }
    
    public func removeAtIndex(index: Int) {
        items.removeAtIndex(index)
        itemAmounts.removeAtIndex(index)
        self.save()
    }
    
    public func save() {
        userDefaults.setObject(items, forKey: "items")
        userDefaults.setObject(itemAmounts, forKey: "itemAmounts")
        userDefaults.synchronize()
    }
    
}