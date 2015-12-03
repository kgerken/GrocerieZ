//
//  GlanceController.swift
//  GrocerieZ WatchKit Extension
//
//  Created by kage on 02.12.15.
//  Copyright © 2015 Zühlke Engineering. All rights reserved.
//

import WatchKit
import Foundation


class GlanceController: WKInterfaceController {

    @IBOutlet var itemsCountLabel: WKInterfaceLabel!
    var timer:NSTimer!
    let extensionDelegate = WKExtension.sharedExtension().delegate as! ExtensionDelegate
    
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
    }

    override func willActivate() {
        super.willActivate()
        refresh()
        timer = NSTimer.scheduledTimerWithTimeInterval(3.0, target: self, selector: "refresh", userInfo: nil, repeats: true)
    }

    override func didDeactivate() {
        super.didDeactivate()
        timer.invalidate()
    }
    
    private func refresh() {
        itemsCountLabel.setText(String(extensionDelegate.items.count))
    }

}
