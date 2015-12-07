//
//  ViewController.swift
//  GrocerieZ
//
//  Created by kage on 02.12.15.
//  Copyright © 2015 Zühlke Engineering. All rights reserved.
//

import UIKit

class ViewController: UITableViewController {
        
    var items = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    @IBAction func addItem(sender: AnyObject) {
        var inputTextField: UITextField?
        let alert = UIAlertController(title: "Enter a new item", message: nil, preferredStyle: UIAlertControllerStyle.Alert)
        alert.addTextFieldWithConfigurationHandler({(textField: UITextField!) in
            inputTextField = textField
        })
        alert.addAction(UIAlertAction(title: "Save", style: UIAlertActionStyle.Default, handler: { (action) -> Void in
            let newItem: String = inputTextField!.text!
            if (newItem.characters.count > 0 && !self.items.contains(newItem)) {
                self.items.append(newItem)
                self.tableView.reloadData()
            }
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
            items.removeAtIndex(indexPath.row)
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
        }
    }

}

