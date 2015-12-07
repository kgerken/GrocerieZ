//
//  HistoryEntry.swift
//  GrocerieZ
//
//  Created by kage on 04.12.15.
//  Copyright © 2015 Zühlke Engineering. All rights reserved.
//

import UIKit

class HistoryEntry: NSObject {
    
    var date: NSDate
    var amount: String
    
    init(withAmount amount: String, onDate date: NSDate) {
        self.date = date
        self.amount = amount
        super.init()
    }

}
