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
import WatchConnectivity


class InterfaceController: WKInterfaceController, WCSessionDelegate {

    @IBOutlet var itemsTable: WKInterfaceTable!
    let extensionDelegate = WKExtension.sharedExtension().delegate as! ExtensionDelegate
    
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
        setTitle("GrocerieZ")
        
        // Start Watch Connectivity session for communication with iPhone
        WCSession.defaultSession().delegate = self
        WCSession.defaultSession().activateSession()
    }

    override func willActivate() {
        super.willActivate()
        loadTableData()
    }

    override func didDeactivate() {
        super.didDeactivate()
    }
    
    // Display a voice input controller on force touch
    @IBAction func showAddItem() {
        presentTextInputControllerWithSuggestions(nil, allowedInputMode: .Plain, completion: {
            (results) -> Void in
            if (results != nil && results!.count > 0) {
                let item: String = results?.first as! String
                self.extensionDelegate.addItem(item)
                self.loadTableData()
                self.updateHistory()
                self.sendMessageToPhone(["ADD": item])
            }
        })
    }
    
    // Delete an item on selection
    override func table(table: WKInterfaceTable, didSelectRowAtIndex rowIndex: Int) {
        let item: String = extensionDelegate.items[rowIndex]
        extensionDelegate.items.removeAtIndex(rowIndex)
        loadTableData()
        updateHistory()
        sendMessageToPhone(["REMOVE": item])
    }
    
    // MARK: - Data loading
    
    private func loadTableData() {
        let items = extensionDelegate.items
        itemsTable.setNumberOfRows(items.count, withRowType: "groceriezCell")
        for i in 0..<items.count {
            let row = itemsTable.rowControllerAtIndex(i) as! GrocerieZRowController
            row.cellLabel.setText(items[i])
        }
    }
    
    // MARK: - WCSession delegate
    
    /* These messages are received on a background thread,
       return to the main thread to update the UI */
    
    func session(session: WCSession, didReceiveMessage message: [String : AnyObject]) {
        processMessageFromPhone(message)
    }
    
    func session(session: WCSession, didReceiveUserInfo userInfo: [String : AnyObject]) {
        processMessageFromPhone(userInfo)
    }
    
    // MARK: - Helper methods
    
    // Save an entry for the complication history and update the complications
    private func updateHistory() {
        extensionDelegate.history.insert(HistoryEntry(withAmount: String(extensionDelegate.items.count), onDate: NSDate()), atIndex: 0)
        let server: CLKComplicationServer = CLKComplicationServer.sharedInstance()
        for complication in server.activeComplications {
            server.reloadTimelineForComplication(complication)
        }
    }
    
    private func sendMessageToPhone(message: [String : AnyObject]) {
        if (WCSession.defaultSession().reachable) {
            // Send data directly
            WCSession.defaultSession().sendMessage(message, replyHandler: nil, errorHandler: errorHandler);
        } else {
            // Send data in background
            WCSession.defaultSession().transferUserInfo(message)
        }
    }

    private func processMessageFromPhone(message: [String : AnyObject]) {
        for key: String in message.keys {
            if (key == "ADD") {
                extensionDelegate.addItem(message[key]! as! String)
            } else if (key == "REMOVE") {
                extensionDelegate.removeItem(message[key]! as! String)
            }
        }
        updateHistory()
        dispatch_async(dispatch_get_main_queue()) {
            self.loadTableData()
        }
    }
    
    private func errorHandler(error: NSError) {
        print(error.localizedDescription)
    }
    
}
