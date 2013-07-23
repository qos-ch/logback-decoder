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
package ch.qos.logback.decoder.cli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.decoder.BufferDecoder;

/**
 * Provides application entry point
 */
public final class Main {

  /**
   * Constructor not called
   */
  private Main() {}

  /**
   * Entry point for command-line interface
   *
   * @param args the command-line parameters
   */
  static public void main(String[] args) {
    MainArgs mainArgs = null;
    try {
      mainArgs = new MainArgs(args);

      // handle help and version queries
      if (mainArgs.queriedHelp()) {
        mainArgs.printUsage();
      } else if (mainArgs.queriedVersion()) {
        mainArgs.printVersion();

      // normal processing
      } else {
        if (mainArgs.isDebugMode()) {
          enableVerboseLogging();
        }

        BufferDecoder decoder = new BufferDecoder();
        decoder.setLayoutPattern(mainArgs.getLayoutPattern());

        BufferedReader reader = null;
        if (StringUtils.defaultString(mainArgs.getInputFile()).isEmpty()) {
          reader = new BufferedReader(new InputStreamReader(System.in));
        } else {
          reader = new BufferedReader(new FileReader(mainArgs.getInputFile()));
        }

        decoder.decode(reader);
      }
    } catch (Exception e) {
      System.err.println("error: " + e.getMessage());
    }
  }

  static private void enableVerboseLogging() {
    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(Level.TRACE);
  }
}
