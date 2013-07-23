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

import java.util.Properties;

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
  private boolean verbose;
  private boolean debugMode;
  private Properties props;
  private Options options;
  private boolean queriedHelp;
  private boolean queriedVersion;

  /**
   * Constructs a {@code MainArgs} with the given arguments
   *
   * @param args command-line arguments
   */
  public MainArgs(String[] args) {
    logger = LoggerFactory.getLogger(MainArgs.class);
    props = new Properties();
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
   * Determines whether debug mode was set
   *
   * @return true if in debug mode; false otherwise
   */
  public boolean isDebugMode() { return debugMode; }

  /**
   * Gets the verbose flag
   *
   * @return the verbose flag
   */
  public boolean isVerbose() { return verbose; }

  /**
   * Gets the defined properties
   *
   * @return the properties
   */
  public Properties getProperties() { return props; }

  /**
   * Determines whether help was requested
   *
   * @return true if help requested; false otherwise
   */
  public boolean queriedHelp() { return queriedHelp; }

  /**
   * Determines whether version info was requested
   *
   * @return true if version requested; false otherwise
   */
  public boolean queriedVersion() { return queriedVersion; }

  /**
   * Prints the usage string
   */
  public final void printUsage() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(APPNAME, options);
  }

  /**
   * Prints the version string
   */
  public final void printVersion() {
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


    Option layoutPattern = OptionBuilder
                              .withArgName("pattern")
                              .hasArg()
                              .withDescription("Layout pattern to use (overrides file's pattern)")
                              .withLongOpt("layout")
                              .create("p");
    opts.addOption(layoutPattern);

    Option infile = OptionBuilder
                              .withArgName("path")
                              .hasArg()
                              .withDescription("Log file to parse (default: stdin)")
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
        .create("D");
    opts.addOption(property);

    return opts;
  }

  /**
   * Parses the command-line arguments
   *
   * @param args the arguments to evaluate
   * @throws RuntimeException a parse error occurred
   */
  private void parseArgs(String... args) throws RuntimeException {
    CommandLineParser parser = new GnuParser();
    try {
      CommandLine line = parser.parse(options, args);

      queriedHelp = line.hasOption("help");
      queriedVersion = line.hasOption("version");
      verbose = Boolean.valueOf(line.hasOption("verbose"));
      debugMode = Boolean.valueOf(line.hasOption("debug"));
      layoutPattern = line.getOptionValue("layout");

      if (line.hasOption("input-file")) {
        inputFile = line.getOptionValue("input-file");
      }

      if (line.hasOption('D')) {
        props = line.getOptionProperties("D");
      }
    } catch (ParseException exp) {
      logger.error("Failed to parse command-line arguments: {}", exp);
      throw new RuntimeException(exp.getMessage(), exp);
    }
  }
}

