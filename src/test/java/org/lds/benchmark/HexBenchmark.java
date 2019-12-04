package org.lds.benchmark;

import org.lds.ByteArrayUtil;
import org.lds.Hex;
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
public class HexBenchmark {
    public static void main(String[] args) throws Exception {
        new Runner(new OptionsBuilder().include(HexBenchmark.class.getSimpleName()).build()).run();
    }

    private byte[] data;
    private String hexString;

    @Setup
    public void setup() {
        data = ByteArrayUtil.random(255);
        hexString = Hex.encode(data);
    }

    @Benchmark
    public void hexEncode() {
        Hex.encode(data);
    }

    @Benchmark
    public void hexDecode() {
        Hex.decode(hexString);
    }

}
