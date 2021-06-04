package org.modelmapper.benchmarks;

import org.modelmapper.ModelMapper;
import org.modelmapper.benchmarks.model.GiantDto;
import org.modelmapper.benchmarks.model.GiantEntity;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class GiantModelBenchmark {

  @Benchmark
  public void measureSubclass() {
    Class<? extends GiantEntity> sourceType = new GiantEntity() {}.getClass();
    Class<? extends GiantDto> destType = new GiantDto() {}.getClass();
    new ModelMapper().createTypeMap(sourceType, destType);
  }

  @Benchmark
  public void measure() {
    new ModelMapper().createTypeMap(GiantEntity.class, GiantDto.class);
  }

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
        .include(GiantModelBenchmark.class.getSimpleName())
        .forks(1)
        .build();
    new Runner(options).run();
  }
}
