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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * A FileDecoder parses all log events from a log file
 * 
 * @author Anthony Trinh
 */
public class FileDecoder extends Decoder {
  private final Logger logger;
  
  /**
   * Constructs a FileDecoder
   * 
   * @param path path to log file
   */
  public FileDecoder() {
    super();
    logger = LoggerFactory.getLogger(FileDecoder.class);
  }
  
  /**
   * Parses a file for log events
   *  
   * @param file the log file to decode
   * @return a list of log events
   */
  public List<ILoggingEvent> decode(File file) {
    
    List<ILoggingEvent> eventList = new LinkedList<ILoggingEvent>();
    BufferedReader reader = null;
    
    try {
      
      // Open the file and attempt to read the pattern header from it.
      // If the header is missing, the layout pattern must be preset
      // (e.g., from command line) before this call or else an exception 
      // occurs.
      reader = new BufferedReader(new FileReader(file));
      readLayoutPattern(reader);
      
      // TODO: Devise a more accurate way to determine the event delimiter
      // based on the given layout pattern. For now, assume log events are 
      // delimited by a new line since that's pretty common. However, this
      // won't always hold true.
      int lineNum = 0;
      while(reader.ready()) {
        lineNum++;
        String inputLine = reader.readLine();
        ILoggingEvent event = super.decode(inputLine + "\n");
        if (event == null) {
          logger.warn("Line {}: Could not decode", lineNum);
          logger.trace("Line {}: Could not decode: \"{}\"", lineNum, inputLine);
        }
        eventList.add(event);
      }
      
    } catch (FileNotFoundException e) {
      
      logger.error(e.toString());
      System.err.println("Can't process file: " + e.getLocalizedMessage());
      
    } catch (IOException e) {
      
      logger.error(e.toString());
      System.err.println("Can't process file: " + e.getLocalizedMessage());
      
    } finally {
      // always close file stream
      try {
        if (reader != null) reader.close();
      } catch (Exception e) { /* ignore */ }
    }
    
    return eventList;
  }
  
  /**
   * Reads the layout pattern header from the given reader
   * and sets this decoder's layout pattern accordingly.
   * If the pattern is not found and a layout pattern was
   * not already specified, this throws an exception.
   * 
   * @param reader the reader from which to read the pattern
   * @throws IOException
   * @throws UnknownLayoutPatternException pattern not found
   * from the reader, and no other pattern was specified
   */
  private void readLayoutPattern(BufferedReader reader) throws IOException {
    String layout = getLayout(reader);
    if ( (layout == null) && (getLayoutPattern() == null) ) {
      throw new UnknownLayoutPatternException("Layout pattern not found. Set layout pattern (e.g., from command line).");
    }
    setLayoutPattern(layout);
  }
  
  /**
   * Attempts to read the layout pattern from the given reader.
   * If the pattern is found in the first line read, the reader is
   * advanced to the next line. Otherwise, the reader is reset to
   * the first line so that it can be processed as a logging event.
   * 
   * @param reader reader of the log file
   * @return the layout pattern if found; otherwise <code>null</code>
   * @throws IOException
   */
  static private String getLayout(BufferedReader reader) throws IOException {
    
    String layout = null;
    final int READ_AHEAD_LIMIT = 1024; // max length of pattern line
    
    // mark the current position so we can reset later if we don't
    // find the pattern header
    reader.mark(READ_AHEAD_LIMIT);
    String line = reader.readLine();
    
    if (line.startsWith(PatternLayout.HEADER_PREFIX)) {
      layout = line.substring(PatternLayout.HEADER_PREFIX.length());
    } else {
      // pattern not found, so reset the reader to the previous line
      // to allow parsing the line as a logging event
      reader.reset();
    }
    return layout;
  }
}
