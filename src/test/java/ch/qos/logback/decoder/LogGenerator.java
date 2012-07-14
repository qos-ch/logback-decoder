/**
 * Copyright (C) 2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.decoder;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.CoreConstants;

/**
 * Utility to generate log files for testing
 */
public class LogGenerator {
  // Harvard Sentences as log event messages
  static private final String[] EVENT_MSGS = {
    "Oak is strong and also gives shade.",
    "Cats and dogs each hate the other.",
    "The pipe began to rust while new.",
    "Open the crate but don't break the glass.",
    "Add the sum to the product of these three.",
    "Thieves who rob friends deserve jail.",
    "The ripe taste of cheese improves with age.",
    "Act on these orders with great speed.",
    "The hog crawled under the high fence.",
    "Move the vat over the hot fire.",
  };
  
  static private final String[] EVENT_LEVELS = {
    "TRACE",
    "DEBUG",
    "WARN",
    "INFO",
    "ERROR",
  };
  
  /**
   * Generates a log file for testing
   * 
   * @param path file destination
   * @param numEvents number of events to generate (one event per line)
   */
  static public void generateFile(String path, int numEvents) {
    final String LAYOUT = "%d [%level] - %msg%n";
    DateFormat dateFormat = new SimpleDateFormat(CoreConstants.ISO8601_PATTERN);
    Calendar now = Calendar.getInstance();
    BufferedWriter writer = null;
    
    try {
      OutputStream file = new FileOutputStream(path);
      writer = new BufferedWriter(new OutputStreamWriter(file));
      
      // write the log pattern at the beginning
      writer.write(PatternLayout.HEADER_PREFIX + LAYOUT + "\n");
      
      for (int i = 0; i < numEvents; i++) {
        
        now.add(Calendar.SECOND, 1);  
        String eventLine = String.format("%1$s [%2$s] - %3$s\n",
            dateFormat.format(now.getTime()),
            EVENT_LEVELS[i % EVENT_LEVELS.length],
            EVENT_MSGS[i % EVENT_MSGS.length]
        );
        
        writer.write(eventLine);
      }
      
    } catch (FileNotFoundException e) {
      System.err.println("Can't generate log: " + e.getLocalizedMessage());
    } catch (IOException e) {
      System.err.println("Can't generate log: " + e.getLocalizedMessage());
    } finally {
      // always close file stream
      try {
        if (writer != null) writer.close();
      } catch (Exception e) { /* ignore */ }
    }
  }
}
