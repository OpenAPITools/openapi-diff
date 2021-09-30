package org.openapitools.openapidiff.cli;

import io.swagger.v3.parser.core.models.AuthorizationValue;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.ConsoleRender;
import org.openapitools.openapidiff.core.output.HtmlRender;
import org.openapitools.openapidiff.core.output.JsonRender;
import org.openapitools.openapidiff.core.output.MarkdownRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    Options options = new Options();
    options.addOption(Option.builder("h").longOpt("help").desc("print this message").build());
    options.addOption(
        Option.builder().longOpt("version").desc("print the version information and exit").build());
    options.addOption(
        Option.builder()
            .longOpt("state")
            .desc("Only output diff state: no_changes, incompatible, compatible")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("fail-on-incompatible")
            .desc("Fail only if API changes broke backward compatibility")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("fail-on-changed")
            .desc("Fail if API changed but is backward compatible")
            .build());
    options.addOption(Option.builder().longOpt("trace").desc("be extra verbose").build());
    options.addOption(
        Option.builder().longOpt("debug").desc("Print debugging information").build());
    options.addOption(
        Option.builder().longOpt("info").desc("Print additional information").build());
    options.addOption(Option.builder().longOpt("warn").desc("Print warning information").build());
    options.addOption(Option.builder().longOpt("error").desc("Print error information").build());
    options.addOption(Option.builder().longOpt("off").desc("No information printed").build());
    options.addOption(
        Option.builder("l")
            .longOpt("log")
            .hasArg()
            .argName("level")
            .desc("use given level for log (TRACE, DEBUG, INFO, WARN, ERROR, OFF). Default: ERROR")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("header")
            .hasArgs()
            .numberOfArgs(2)
            .valueSeparator()
            .argName("property=value")
            .desc("use given header for authorisation")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("query")
            .hasArgs()
            .numberOfArgs(2)
            .valueSeparator()
            .argName("property=value")
            .desc("use query param for authorisation")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("markdown")
            .hasArg()
            .argName("file")
            .desc("export diff as markdown in given file")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("html")
            .hasArg()
            .argName("file")
            .desc("export diff as html in given file")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("text")
            .hasArg()
            .argName("file")
            .desc("export diff as text in given file")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("json")
            .hasArg()
            .argName("file")
            .desc("export diff as json in given file")
            .build());

    // create the parser
    CommandLineParser parser = new DefaultParser();
    try {
      // parse the command line arguments
      CommandLine line = parser.parse(options, args);
      if (line.hasOption("h")) { // automatically generate the help statement
        printHelp(options);
        System.exit(0);
      }
      String logLevel = "ERROR";
      if (line.hasOption("off")) {
        logLevel = "OFF";
      }
      if (line.hasOption("error")) {
        logLevel = "ERROR";
      }
      if (line.hasOption("warn")) {
        logLevel = "WARN";
      }
      if (line.hasOption("info")) {
        logLevel = "INFO";
      }
      if (line.hasOption("debug")) {
        logLevel = "DEBUG";
      }
      if (line.hasOption("trace")) {
        logLevel = "TRACE";
      }
      if (line.hasOption("log")) {
        logLevel = line.getOptionValue("log");
        if (!logLevel.equalsIgnoreCase("TRACE")
            && !logLevel.equalsIgnoreCase("DEBUG")
            && !logLevel.equalsIgnoreCase("INFO")
            && !logLevel.equalsIgnoreCase("WARN")
            && !logLevel.equalsIgnoreCase("ERROR")
            && !logLevel.equalsIgnoreCase("OFF")) {
          throw new ParseException(
              String.format(
                  "Invalid log level. Excepted: [TRACE, DEBUG, INFO, WARN, ERROR, OFF]. Given: %s",
                  logLevel));
        }
      }
      if (line.hasOption("state")) {
        logLevel = "OFF";
      }
      LogManager.getRootLogger().setLevel(Level.toLevel(logLevel));

      if (line.getArgList().size() < 2) {
        throw new ParseException("Missing arguments");
      }
      String oldPath = line.getArgList().get(0);
      String newPath = line.getArgList().get(1);

      List<AuthorizationValue> auths = null;
      if (line.hasOption("header")) {
        String[] headers = line.getOptionValues("header");
        auths = Collections.singletonList(new AuthorizationValue(headers[0], headers[1], "header"));
      }

      ChangedOpenApi result = OpenApiCompare.fromLocations(oldPath, newPath, auths);
      ConsoleRender consoleRender = new ConsoleRender();
      if (!logLevel.equals("OFF")) {
        System.out.println(consoleRender.render(result));
      }
      if (line.hasOption("html")) {
        HtmlRender htmlRender = new HtmlRender();
        String output = htmlRender.render(result);
        String outputFile = line.getOptionValue("html");
        writeOutput(output, outputFile);
      }
      if (line.hasOption("markdown")) {
        MarkdownRender mdRender = new MarkdownRender();
        String output = mdRender.render(result);
        String outputFile = line.getOptionValue("markdown");
        writeOutput(output, outputFile);
      }
      if (line.hasOption("text")) {
        String output = consoleRender.render(result);
        String outputFile = line.getOptionValue("text");
        writeOutput(output, outputFile);
      }
      if (line.hasOption("json")) {
        JsonRender jsonRender = new JsonRender();
        String output = jsonRender.render(result);
        String outputFile = line.getOptionValue("json");
        writeOutput(output, outputFile);
      }
      if (line.hasOption("state")) {
        System.out.println(result.isChanged().getValue());
        System.exit(0);
      } else if (line.hasOption("fail-on-incompatible")) {
        System.exit(result.isCompatible() ? 0 : 1);
      } else if (line.hasOption("fail-on-changed")) {
        System.exit(result.isUnchanged() ? 0 : 1);
      }
    } catch (ParseException e) {
      // oops, something went wrong
      System.err.println("Parsing failed. Reason: " + e.getMessage());
      printHelp(options);
      System.exit(2);
    } catch (Exception e) {
      System.err.println(
          "Unexpected exception. Reason: "
              + e.getMessage()
              + "\n"
              + ExceptionUtils.getStackTrace(e));
      System.exit(2);
    }
  }

  private static void writeOutput(String output, String outputFile) {
    File file = new File(outputFile);
    logger.debug("Output file: {}", file.getAbsolutePath());
    try {
      FileUtils.writeStringToFile(file, output, StandardCharsets.UTF_8);
    } catch (IOException e) {
      logger.error("Impossible to write output to file {}", outputFile, e);
      System.exit(2);
    }
  }

  public static void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("openapi-diff <old> <new>", options);
  }
}
