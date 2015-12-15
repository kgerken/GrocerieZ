//
//  HistoryEntry.swift
//  GrocerieZ
//
//  Created by kage on 04.12.15.
//  Copyright © 2015 Zühlke Engineering. All rights reserved.
//

import UIKit

class HistoryEntry: NSObject {
    
    // Date of the entry
    var date: NSDate
    // Number of items on the shopping list
    var amount: String
    
    init(withAmount amount: String, onDate date: NSDate) {
        self.date = date
        self.amount = amount
        super.init()
    }

}
