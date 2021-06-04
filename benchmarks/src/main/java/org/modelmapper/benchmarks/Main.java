package org.modelmapper.benchmarks;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.CommandLineOptions;

public class Main {

  public static void main(String[] args) throws CommandLineOptionException, RunnerException {
    new Runner(new CommandLineOptions(args)).run();
  }
}
