package org.zero.common.test;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zero
 * @since 2024/7/15
 */
@Fork(2)
@Threads(4)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1555, timeUnit = TimeUnit.MILLISECONDS, batchSize = 10)
@Measurement(iterations = 20, time = 1555, timeUnit = TimeUnit.MILLISECONDS)
public class JmhTests {
    @Benchmark
    public void testForLoop(Blackhole blackhole) {
        List<String> result = new ArrayList<>();
        for (String s : list) {
            result.add(s + BigInteger.ZERO);
        }
        blackhole.consume(result);
    }

    @Benchmark
    public void testForStream(Blackhole blackhole) {
        List<String> result = list.stream().map(s -> s + BigInteger.ZERO).collect(Collectors.toList());
        blackhole.consume(result);
    }

    @Benchmark
    public void testForParallelStream(Blackhole blackhole) {
        List<String> result = list.parallelStream().map(s -> s + BigInteger.ZERO).collect(Collectors.toList());
        blackhole.consume(result);
    }

    @Param({"1000", "10000", "100000", "1000000"})
    private int num;

    private List<String> list = new ArrayList<>();

    @Setup(Level.Trial)
    public void setup() {
        if (Objects.isNull(list)) {
            list = new ArrayList<>();
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < num; i++) {
            String string = random.ints(3, 1, 10)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining());
            list.add(string);
        }
    }

    @TearDown(Level.Trial)
    public void destroy() {
        list = null;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhTests.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
