package com.jshubham.ftpservice.cliclient;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SampleCLIClient {
    private static final Logger logger = LogManager.getLogger(SampleCLIClient.class);
    private static final String ADD = "add";
    private static final String OUT = "out";
    private static final String DEL = "del";
    private static final String SERVER = "server";

    private void addFile(String server, String fileToAdd, String destFilename) {
        logger.info("addFile method called to file {} with the destination filename {}", fileToAdd, destFilename);
    }

    private void deletedFile(String server, String fileToDelete) {
        logger.info("delete file method called to delete file {}", fileToDelete);
    }

    public static void main(String[] args) {
        boolean failed = false;
        Options options = new Options();

        options.addOption(SERVER, true, "server address");
        options.addOption(ADD, true, "Add a file to the server's destination directory");
        options.addOption(DEL, true, "Delete a file in the server's destination directory");
        options.addOption(OUT, true, "destination filename when adding a file");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmdLine = parser.parse(options, args);

            int optionsCount = cmdLine.getOptions().length;
            String server;

            if(cmdLine.hasOption(SERVER)) {
                server = cmdLine.getOptionValue(SERVER);
            }else {
                throw new ParseException("Missing server address");
            }

            if(cmdLine.hasOption(ADD) && optionsCount == 3) {
                String fileToAdd = cmdLine.getOptionValue(ADD);
                String outputFileName = cmdLine.getOptionValue(OUT);
                (new SampleCLIClient()).addFile(server, fileToAdd, outputFileName);
            } else if (cmdLine.hasOption(DEL) && optionsCount == 2) {
                String fileToDelete = cmdLine.getOptionValue(DEL);
                (new SampleCLIClient()).deletedFile(server, fileToDelete);
            } else {
                // provided command-line arguments are incorrect or insufficient
                // in this case we set the flag
                failed = true;
            }

        }catch (ParseException pe) {
            logger.info(pe.toString());
            failed = true;
        }

        if(failed) {
            printUsage();
        }
    }
    private static void printUsage() {
        // Print the formatted help message using apache commons CLI
        System.out.println("To add a file");
        System.out.println("java -cp ftp-service-cli-client-1.0.0.jar  io.datajek.ftpservice.cliclient.SampleCLIClient -add <file-path-to-add>  -server <server-address> -out <destination-filename>");
        System.out.println("for example: java -cp ftp-service-cli-client-1.0.0.jar  io.datajek.ftpservice.cliclient.SampleCLIClient -add /tmp/hiThere.py  -server localhost -out destFile.py");
        System.out.println("\n");
        System.out.println("To delete a file");
        System.out.println("java -cp ftp-service-cli-client-1.0.0.jar  io.datajek.ftpservice.cliclient.SampleCLIClient -del <filename-to-delete>   -server <server-address>");
        System.out.println("for example: java -cp ftp-service-cli-client-1.0.0.jar  io.datajek.ftpservice.cliclient.SampleCLIClient -del \"destFile.py\"   -server localhost");
    }
}
