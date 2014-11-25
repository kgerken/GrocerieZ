//
//  AmountInterfaceController.swift
//  GrocerieZ
//
//  Created by kage on 25.11.14.
//  Copyright (c) 2014 Zühlke Engineering. All rights reserved.
//

import WatchKit
import GrocerieZKit

class AmountInterfaceController: WKInterfaceController {
 
    @IBOutlet weak var itemLabel: WKInterfaceLabel!
    @IBOutlet weak var amountLabel: WKInterfaceLabel!
    let items:GKItems = GKItems()
    let selectedIndex:Int!
    
    override init(context: AnyObject?) {
        // Initialize variables here.
        super.init(context: context)
        setTitle("GrocerieZ")
        selectedIndex = context as Int
        NSLog("%@ init", self)
        itemLabel.setText(items[context as Int])
        
    }
    
    func sliderChanged(value: Int) {
        if value > 1 {
            let oldItem = items[selectedIndex]
            amountLabel.setText("\(value)")
            items[selectedIndex] = "\(value) \(oldItem)"
            items.save()
        }
    }
}
