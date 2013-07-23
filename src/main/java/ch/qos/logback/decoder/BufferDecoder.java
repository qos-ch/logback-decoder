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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * A BufferDecoder parses all log events from a stream
 *
 * @author Anthony Trinh
 */
public class BufferDecoder extends Decoder {
  private final Logger logger;

  /**
   * Constructs a BufferDecoder
   *
   * @param path path to log file
   */
  public BufferDecoder() {
    super();
    logger = LoggerFactory.getLogger(BufferDecoder.class);
  }

  /**
   * Parses log events from a buffer
   *
   * @param reader buffer containing log strings, delimited by a new-line character
   * @return a list of log events
   * @throws IOException an error occurred while reading buffer
   */
  public List<ILoggingEvent> decode(BufferedReader reader) throws IOException {

    List<ILoggingEvent> eventList = new LinkedList<ILoggingEvent>();

    // If pattern not specified from command-line, read the pattern
    // from the given buffer.
    if (getLayoutPattern() == null) {
      readLayoutPattern(reader);
    }

    int lineNum = 0;
    String inputLine;
    while ((inputLine = reader.readLine()) != null) {
      lineNum++;
      ILoggingEvent event = super.decode(inputLine + "\n");
      if (event == null) {
        logger.trace("line {}: cannot decode: \"{}\"", lineNum, inputLine);
      }
      eventList.add(event);
    }

    return eventList;
  }

  /**
   * Reads the layout pattern header from the given reader
   * and sets this decoder's layout pattern accordingly.
   * If the pattern is not found, this throws an exception.
   *
   * @param reader the reader from which to read the pattern
   * @throws IOException
   * @throws UnknownLayoutPatternException pattern not found
   * from the reader, and no other pattern was specified
   */
  private void readLayoutPattern(BufferedReader reader) throws IOException {
    String layout = getLayout(reader);
    if (layout == null) {
      throw new UnknownLayoutPatternException("layout pattern not specified");
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
   * @return the layout pattern if found; otherwise {@code null}
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
