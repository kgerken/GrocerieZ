//
//  InterfaceController.swift
//  GrocerieZ WatchKit Extension
//
//  Created by kage on 02.12.15.
//  Copyright © 2015 Zühlke Engineering. All rights reserved.
//

import WatchKit
import Foundation


class InterfaceController: WKInterfaceController {

    @IBOutlet var itemsTable: WKInterfaceTable!
    let extensionDelegate = WKExtension.sharedExtension().delegate as! ExtensionDelegate
    
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
            }

    override func willActivate() {
        super.willActivate()
        loadTableData()
    }

    override func didDeactivate() {
        super.didDeactivate()
    }
    
    
    @IBAction func showAddItem() {
        presentTextInputControllerWithSuggestions(nil, allowedInputMode: .Plain, completion: {
            (results) -> Void in
            let extensionDelegate = WKExtension.sharedExtension().delegate as! ExtensionDelegate
            extensionDelegate.items.append(results?.first as! String)
            self.loadTableData()
        })
    }
    
    private func loadTableData() {
        let items = extensionDelegate.items
        itemsTable.setNumberOfRows(items.count, withRowType: "groceriezCell")
        for i in 0..<items.count {
            let row = itemsTable.rowControllerAtIndex(i) as! GrocerieZRowController
            row.cellLabel.setText(items[i])
        }
    }
    
    override func table(table: WKInterfaceTable, didSelectRowAtIndex rowIndex: Int) {
        extensionDelegate.items.removeAtIndex(rowIndex)
        loadTableData()
    }
}
