//
//  ComplicationController.swift
//  GrocerieZ WatchKit Extension
//
//  Created by kage on 02.12.15.
//  Copyright © 2015 Zühlke Engineering. All rights reserved.
//

import ClockKit
import WatchKit


class ComplicationController: NSObject, CLKComplicationDataSource {
    
    let extensionDelegate = WKExtension.sharedExtension().delegate as! ExtensionDelegate
    
    // MARK: - Timeline Configuration
    
    func getSupportedTimeTravelDirectionsForComplication(complication: CLKComplication, withHandler handler: (CLKComplicationTimeTravelDirections) -> Void) {
        handler([.Backward])
    }
    
    func getTimelineStartDateForComplication(complication: CLKComplication, withHandler handler: (NSDate?) -> Void) {
        handler(extensionDelegate.history.last?.date)
    }
    
    func getTimelineEndDateForComplication(complication: CLKComplication, withHandler handler: (NSDate?) -> Void) {
        handler(nil)
    }
    
    func getPrivacyBehaviorForComplication(complication: CLKComplication, withHandler handler: (CLKComplicationPrivacyBehavior) -> Void) {
        handler(.ShowOnLockScreen)
    }
    
    // MARK: - Timeline Population
    
    func getCurrentTimelineEntryForComplication(complication: CLKComplication, withHandler handler: ((CLKComplicationTimelineEntry?) -> Void)) {
        switch complication.family {
        case .UtilitarianSmall:
            let template = CLKComplicationTemplateUtilitarianSmallFlat()
            template.imageProvider = CLKImageProvider(onePieceImage: UIImage(named: "Complication/Utilitarian")!)
            template.textProvider = CLKSimpleTextProvider(text: String(extensionDelegate.items.count))
            let entry = CLKComplicationTimelineEntry(date: NSDate(), complicationTemplate: template)
            handler(entry)
            break
        default:
            handler(nil)
            break
        }
    }
    
    func getTimelineEntriesForComplication(complication: CLKComplication, beforeDate date: NSDate, limit: Int, withHandler handler: (([CLKComplicationTimelineEntry]?) -> Void)) {
        let items = extensionDelegate.history
        var entries = [CLKComplicationTimelineEntry]()
        for item in items {
            if (date.compare(item.date) == .OrderedDescending) {
                entries.append(CLKComplicationTimelineEntry(date: item.date, complicationTemplate: getUtilitarianSmallFlatTemplateForHistoryEntry(item)))
                if (entries.count == limit) {
                    break;
                }
            }
        }
        handler(entries)
    }
    
    func getTimelineEntriesForComplication(complication: CLKComplication, afterDate date: NSDate, limit: Int, withHandler handler: (([CLKComplicationTimelineEntry]?) -> Void)) {
        // Call the handler with the timeline entries after to the given date
        handler(nil)
    }
    
    // MARK: - Update Scheduling
    
    func getNextRequestedUpdateDateWithHandler(handler: (NSDate?) -> Void) {
        // Call the handler with the date when you would next like to be given the opportunity to update your complication content
        handler(nil);
    }
    
    // MARK: - Placeholder Templates
    
    func getPlaceholderTemplateForComplication(complication: CLKComplication, withHandler handler: (CLKComplicationTemplate?) -> Void) {
        switch complication.family {
        case .UtilitarianSmall:
            let template = CLKComplicationTemplateUtilitarianSmallFlat()
            template.imageProvider = CLKImageProvider(onePieceImage: UIImage(named: "Complication/Utilitarian")!)
            template.textProvider = CLKSimpleTextProvider(text: "--")
            handler(template)
            break
        default:
            handler(nil)
            break
        }
    }
    
    // MARK: - Helper Methods
    
    private func getUtilitarianSmallFlatTemplateForHistoryEntry(entry: HistoryEntry) -> CLKComplicationTemplateUtilitarianSmallFlat {
        let template = CLKComplicationTemplateUtilitarianSmallFlat()
        template.imageProvider = CLKImageProvider(onePieceImage: UIImage(named: "Complication/Utilitarian")!)
        template.textProvider = CLKSimpleTextProvider(text: entry.amount)
        return template;
    }
    
}
