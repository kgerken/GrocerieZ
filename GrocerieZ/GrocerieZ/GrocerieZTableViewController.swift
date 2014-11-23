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
    
    let items:GKItems = GKItems()

    override func viewDidLoad() {
        super.viewDidLoad()
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
        return cell
    }

    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }

    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            items.removeAtIndex(indexPath.row)
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
        }
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 50
    }

    // MARK: UITextField delegate
    
    func textFieldDidEndEditing(textField: UITextField!) {
        items[textField.tag] = textField.text
        items.save()
    }
    
    func textFieldShouldReturn(textField: UITextField!) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    // MARK: Custom methods
    
    @IBAction func addItem(sender: AnyObject) {
        items.add("")
        tableView.reloadData()
        let cell = self.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: items.count - 1, inSection: 0)) as GrocerieZTableViewCell
        cell.textField.becomeFirstResponder()
    }
}
