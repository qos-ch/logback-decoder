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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Provides application entry point
 */
public class Main {
  static private final String VERSION_NUMBER = "0.1.0";
  static private final String APPNAME = "logback-decoder";
  static private final String VERSION_STRING = APPNAME + " (Version " + VERSION_NUMBER + ")";
  static private final Options options = createOptions();

  /**
   * Constructor not called
   */
  private Main() {
  }
  
  /**
   * Entry point for command-line interface
   * 
   * @param args the command-line parameters
   */
  static public void main(String[] args) {
    parseArgs(args);
    System.exit(0);
  }
  
  /**
   * Creates the options for the command-line arguments
   * 
   * @return the newly created options
   */
  @SuppressWarnings("static-access")
  static private final Options createOptions() {
    Options opts = new Options();
    
    Option help = OptionBuilder
        .withDescription("Print this help message and exit")
        .withLongOpt("help")
        .create("h");
    opts.addOption(help);

    Option version = OptionBuilder
        .withDescription("Print version information and exit")
        .withLongOpt("version")
        .create("v");
    opts.addOption(version);

    Option infile = OptionBuilder
                              .withArgName("path")
                              .hasArg()
                              .withDescription("Log file to parse")
                              .withLongOpt("input-file")
                              .create("f");
    opts.addOption(infile);
    
    Option debug = OptionBuilder
                              .withDescription("Enable debug mode")
                              .withLongOpt("debug")
                              .create("d");
    opts.addOption(debug);
    
    Option verbose = OptionBuilder
                                .withDescription("Be verbose when printing information")
                                .withLongOpt("verbose")
                                .create();
    opts.addOption(verbose);
    
    Option property  = OptionBuilder.withArgName( "property=value" )
        .hasArgs(2)
        .withValueSeparator()
        .withDescription( "use value for given property" )
        .create( "D" );
    opts.addOption(property);
    
    return opts;
  }
  
  /**
   * Prints the usage string
   */
  static private final void printUsage() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(APPNAME, options);
  }
  
  /**
   * Prints the version string
   */
  static private final void printVersion() {
    System.out.println(VERSION_STRING);
  }
  
  /**
   * Parses the command-line arguments
   * 
   * @param args the arguments to evaluate
   */
  static private void parseArgs(String... args) {
    CommandLineParser parser = new GnuParser();
    try {
        CommandLine line = parser.parse( options, args );
        
        if (line.hasOption("help")) {
          printUsage();
          return;
        } else if (line.hasOption("version")) {
          printVersion();
          return;
        }
    } catch (ParseException exp) {
        System.err.println("Error: " + exp.getMessage());
    }
  }
}
