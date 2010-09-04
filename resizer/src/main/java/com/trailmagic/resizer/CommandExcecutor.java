package com.trailmagic.resizer;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: oliver on Date: Aug 28, 2010 Time: 7:11:21 PM
 */
public class CommandExcecutor {    
    public List<String> exec(String command, String... args) throws CommandFailedException {
        CommandLine commandLine = CommandLine.parse(command);
        for (String arg : args) {
            commandLine.addArgument(arg);
        }
        DefaultExecutor executor = new DefaultExecutor();
        OutputCapturingExecuteStreamHandler outputHandler = new OutputCapturingExecuteStreamHandler(command);
        executor.setStreamHandler(outputHandler);
        try {
            int exitCode = executor.execute(commandLine);
            if (exitCode != 0) {
                throw new CommandFailedException("Failed to execute " + command + ", exit code was: " + exitCode);
            }
        } catch (IOException e) {
            throw new CommandFailedException("Failed to execute " + command + ": ", e);
        }
        return outputHandler.getOutputLines();
    }

    private static class OutputCapturingExecuteStreamHandler implements ExecuteStreamHandler {
        private List<String> outputLines;
        private String command;

        public OutputCapturingExecuteStreamHandler(String command) {
            this.command = command;
        }

        @Override
        public void setProcessInputStream(OutputStream os) throws IOException {

        }

        @Override
        public void setProcessErrorStream(InputStream is) throws IOException {

        }

        @Override
        public void setProcessOutputStream(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            try {
                outputLines = new ArrayList<String>();
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    outputLines.add(line);
                }
            } catch (IOException e) {
                throw new CommandFailedException("Error parsing output of command: " + command, e);
            }

        }

        @Override
        public void start() throws IOException {

        }

        @Override
        public void stop() {

        }

        public List<String> getOutputLines() {
            return outputLines;
        }
    }
}
