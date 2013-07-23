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

import java.io.File;

import ch.qos.logback.decoder.FileDecoder;

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
    } catch (RuntimeException e) {
      System.err.println(e.getMessage());
      return;
    }

    if (mainArgs.queriedHelp()) {
      mainArgs.printUsage();
    } else if (mainArgs.queriedVersion()) {
      mainArgs.printVersion();
    } else {
      FileDecoder decoder = new FileDecoder();
      decoder.setLayoutPattern(mainArgs.getLayoutPattern());
      decoder.decode(new File(mainArgs.getInputFile()));
    }
  }
}
