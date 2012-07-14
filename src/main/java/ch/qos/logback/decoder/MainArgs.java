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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to parse and hold arguments from the command line 
 */
public class MainArgs {
  static private final String VERSION_NUMBER = "0.1.0";
  static private final String APPNAME = "logback-decoder";
  static private final String VERSION_STRING = APPNAME + " (Version " + VERSION_NUMBER + ")";
  
  private final Logger logger;
  private String layoutPattern;
  private String inputFile;
  private Options options;
  
  public MainArgs(String[] args) {
    logger = LoggerFactory.getLogger(MainArgs.class);
    options = createOptions();
    parseArgs(args);
  }
  
  /**
   * Gets the path to the log file for parsing
   * 
   * @return the file path
   */
  public String getInputFile() { return inputFile; }
  
  /**
   * Gets the layout pattern to use to parse the log file
   * (only used if file does not contain pattern header)
   * 
   * @return the layout pattern
   */
  public String getLayoutPattern() { return layoutPattern; }
  
  /**
   * Prints the usage string
   */
  private final void printUsage() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(APPNAME, options);
  }
  
  /**
   * Prints the version string
   */
  private final void printVersion() {
    System.out.println(VERSION_STRING);
  }
  
  /**
   * Creates the options for the command-line arguments
   * 
   * @return the newly created options
   */
  @SuppressWarnings("static-access")
  private final Options createOptions() {
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
                              .isRequired(true)
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
        .create("D");
    opts.addOption(property);
    
    return opts;
  }
  
  /**
   * Parses the command-line arguments. If help or version info is requested
   * this exits the process immediately after printing them. If a parsing
   * error occurs (including missing args), this exits with an error code.
   * 
   * @param args the arguments to evaluate
   */
  public void parseArgs(String... args) {
    CommandLineParser parser = new GnuParser();
    try {
      CommandLine line = parser.parse( options, args );
      
      if (line.hasOption("help")) {
        printUsage();
        System.exit(0);
      } else if (line.hasOption("version")) {
        printVersion();
        System.exit(0);
      }
      
      if (line.hasOption("input-file")) {
        inputFile = line.getOptionValue("input-file");
      }
    } catch (ParseException exp) {
      logger.error("Failed to parse command-line arguments: {}", exp);
      System.err.println("Error: " + exp.getMessage());
      System.exit(1);
    }
  }
}

