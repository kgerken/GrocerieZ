//
//  ViewController.swift
//  GrocerieZ
//
//  Created by kage on 02.12.15.
//  Copyright © 2015 Zühlke Engineering. All rights reserved.
//

import UIKit
import WatchConnectivity

class ViewController: UITableViewController, WCSessionDelegate {
    
    /* Data is only stored in memory,
       implement Core Data for persistence */
    
    // Current shopping list items
    var items = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Start Watch Connectivity session for communication with watch
        if (WCSession.isSupported()) {
            WCSession.defaultSession().delegate = self
            WCSession.defaultSession().activateSession()
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    // Display an alert for entering a new item
    @IBAction func addItem(sender: AnyObject) {
        var inputTextField: UITextField?
        let alert = UIAlertController(title: "Enter a new item", message: nil, preferredStyle: UIAlertControllerStyle.Alert)
        alert.addTextFieldWithConfigurationHandler({(textField: UITextField!) in
            inputTextField = textField
            textField.autocapitalizationType = .Sentences
        })
        alert.addAction(UIAlertAction(title: "Cancel", style: .Cancel, handler: nil))
        alert.addAction(UIAlertAction(title: "Save", style: .Default, handler: { (action) -> Void in
            let item: String = inputTextField!.text!
            self.addItem(item)
            self.sendMessageToWatch(["ADD": item])
        }))
        self.presentViewController(alert, animated: true, completion: nil)
    }
    
    // MARK: - Table view data source
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("groceriezCell", forIndexPath: indexPath) as UITableViewCell
        cell.textLabel!.text = items[indexPath.row]
        return cell
    }
    
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            let item: String = items[indexPath.row]
            items.removeAtIndex(indexPath.row)
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
            sendMessageToWatch(["REMOVE": item])
        }
    }
    
    // MARK: - WCSession delegate
    
    /* These messages are received on a background thread,
       return to the main thread to update the UI */
    
    func session(session: WCSession, didReceiveMessage message: [String : AnyObject]) {
        processMessageFromWatch(message)
    }
    
    func session(session: WCSession, didReceiveUserInfo userInfo: [String : AnyObject]) {
        processMessageFromWatch(userInfo)
    }
    
    // MARK: - Helper methods
    
    private func addItem(item: String) {
        if (item.characters.count > 0 && !items.contains(item)) {
            items.append(item)
            dispatch_async(dispatch_get_main_queue()) {
                self.tableView.reloadData()
            }
        }
    }
    
    private func removeItem(item: String) {
        if (items.contains(item)) {
            let index = items.indexOf(item)
            items.removeAtIndex(index!)
            dispatch_async(dispatch_get_main_queue()) {
                self.tableView.deleteRowsAtIndexPaths([NSIndexPath(forRow: index!, inSection: 0)], withRowAnimation: .Fade)
            }
        }
    }
    
    private func sendMessageToWatch(message: [String : AnyObject]) {
        if (WCSession.isSupported()) {
            if (WCSession.defaultSession().reachable) {
                // Send data directly
                WCSession.defaultSession().sendMessage(message, replyHandler: nil, errorHandler: errorHandler);
            } else {
                // Send data in background
                WCSession.defaultSession().transferUserInfo(message)
            }
        }
    }
    
    private func processMessageFromWatch(message: [String : AnyObject]) {
        for key: String in message.keys {
            if (key == "ADD") {
                addItem(message[key]! as! String)
            } else if (key == "REMOVE") {
                removeItem(message[key]! as! String)
            }
        }
    }
    
    private func errorHandler(error: NSError) {
        print(error.localizedDescription)
    }

}

