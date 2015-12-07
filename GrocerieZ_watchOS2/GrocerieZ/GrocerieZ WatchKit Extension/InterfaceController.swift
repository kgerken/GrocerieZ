//
//  InterfaceController.swift
//  GrocerieZ WatchKit Extension
//
//  Created by kage on 02.12.15.
//  Copyright © 2015 Zühlke Engineering. All rights reserved.
//

import WatchKit
import Foundation
import ClockKit


class InterfaceController: WKInterfaceController {

    @IBOutlet var itemsTable: WKInterfaceTable!
    let extensionDelegate = WKExtension.sharedExtension().delegate as! ExtensionDelegate
    
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
        setTitle("GrocerieZ")
    }

    override func willActivate() {
        super.willActivate()
        loadTableData()
    }

    override func didDeactivate() {
        super.didDeactivate()
    }
    
    override func table(table: WKInterfaceTable, didSelectRowAtIndex rowIndex: Int) {
        extensionDelegate.items.removeAtIndex(rowIndex)
        loadTableData()
        updateHistory()
    }
    
    @IBAction func showAddItem() {
        presentTextInputControllerWithSuggestions(nil, allowedInputMode: .Plain, completion: {
            (results) -> Void in
            self.extensionDelegate.items.append(results?.first as! String)
            self.loadTableData()
            self.updateHistory()
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
    
    private func updateHistory() {
        extensionDelegate.history.insert(HistoryEntry(withAmount: String(extensionDelegate.items.count), onDate: NSDate()), atIndex: 0)
        let server: CLKComplicationServer = CLKComplicationServer.sharedInstance()
        for complication in server.activeComplications {
            server.reloadTimelineForComplication(complication)
        }
    }
    
}
