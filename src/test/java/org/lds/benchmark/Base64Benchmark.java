package org.lds.benchmark;

import org.lds.Base64;
import org.lds.ByteArrayUtil;
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
public class Base64Benchmark {
    public static void main(String[] args) throws Exception {
        new Runner(new OptionsBuilder().include(Base64Benchmark.class.getSimpleName()).build()).run();
    }

    private byte[] data;
    private String base64String;

    @Setup
    public void setup() {
        data = ByteArrayUtil.random(255);
        base64String = Base64.STANDARD.encode(data);
    }

    @Benchmark
    public void base64Encode() {
        Base64.STANDARD.encode(data);
    }

    @Benchmark
    public void base64Decode() {
        Base64.decode(base64String);
    }

}
