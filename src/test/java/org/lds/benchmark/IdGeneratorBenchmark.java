package org.lds.benchmark;

import org.lds.Util;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
public class IdGeneratorBenchmark {
    public static void main(String[] args) throws Exception {
        new Runner(new OptionsBuilder().include(IdGeneratorBenchmark.class.getSimpleName()).build()).run();
    }

    @Benchmark
    public void generateId() {
        Util.generateId();
    }
}
