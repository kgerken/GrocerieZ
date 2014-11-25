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
    @IBOutlet weak var amountSlider: WKInterfaceSlider!

    let items:GKItems = GKItems()
    let selectedIndex:Int!
    
    override init(context: AnyObject?) {
        // Initialize variables here.
        super.init(context: context)
        setTitle("GrocerieZ")
        selectedIndex = context as Int
        NSLog("%@ init", self)
        itemLabel.setText(items.getItemName(context as Int))
        
        let savedAmount = items.getAmount(selectedIndex)
        amountSlider.setValue(Float(savedAmount))
        amountLabel.setText("\(savedAmount)")

    }
    
    @IBAction func deleteButtonPressed() {
        items.removeAtIndex(selectedIndex)
        popController()
    }
    
    func sliderChanged(value: Int) {
        println("Slider changed \(value)")
        amountLabel.setText("\(value)")
        items.setAmount(value, forIndex: selectedIndex)
        items.save()
    }
}
