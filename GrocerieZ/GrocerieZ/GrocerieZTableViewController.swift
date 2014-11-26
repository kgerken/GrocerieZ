//
//  GrocerieZTableViewController.swift
//  GrocerieZ
//
//  Created by kage on 23.11.14.
//  Copyright (c) 2014 Zühlke Engineering. All rights reserved.
//

import UIKit
import GrocerieZKit

class GrocerieZTableViewController: UITableViewController, UITextFieldDelegate {
    
    let timerInterval = 1.0
    let items:GKItems = GKItems()
    var timer:NSTimer!
    var addMode = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        timer = NSTimer.scheduledTimerWithTimeInterval(timerInterval, target: self, selector: "refresh", userInfo: nil, repeats: true)
    }


    // MARK: - Table view data source
    
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> GrocerieZTableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("groceriezCell", forIndexPath: indexPath) as GrocerieZTableViewCell
        cell.textField.text = items[indexPath.row]
        cell.textField.tag = indexPath.row
        if (addMode && items.count > 0 && indexPath.row == items.count-1) {
            cell.textField.becomeFirstResponder()
            addMode = false
        }
        return cell
    }

    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }

    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            items.removeAtIndex(indexPath.row)
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
            timer = NSTimer.scheduledTimerWithTimeInterval(timerInterval, target: self, selector: "refresh", userInfo: nil, repeats: true)
        }
    }
    
    override func tableView(tableView: UITableView, willBeginEditingRowAtIndexPath indexPath: NSIndexPath) {
        timer.invalidate()
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 50
    }

    // MARK: UITextField delegate
    
    func textFieldDidBeginEditing(textField: UITextField) {
        timer.invalidate()
    }
    
    func textFieldDidEndEditing(textField: UITextField!) {
        items[textField.tag] = textField.text
        items.save()
        timer = NSTimer.scheduledTimerWithTimeInterval(timerInterval, target: self, selector: "refresh", userInfo: nil, repeats: true)
    }
    
    func textFieldShouldReturn(textField: UITextField!) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    
    func refresh() {
        //println("timer refresh")
        items.refreshItems()
        tableView.reloadData()
    }
    
    // MARK: Custom methods
    
    @IBAction func addItem(sender: AnyObject) {
        timer.invalidate()
        items.add("")
        addMode = true
        tableView.reloadData()
    }
}
