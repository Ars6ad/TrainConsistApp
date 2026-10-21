package com.railconsist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * High-precision performance profiler benchmarking iterative loops against Java Stream pipelines (UC13).
 */
public class PerformanceBenchmarkService {

    public record BenchmarkResult(
            int dataSize,
            long iterativeLoopNanos,
            long streamPipelineNanos,
            double loopMilliseconds,
            double streamMilliseconds,
            String fasterEngine,
            double speedupFactor
    ) {}

    public BenchmarkResult runFilterBenchmark(int sampleSize) {
        Random random = new Random(42);
        List<Double> carWeights = new ArrayList<>(sampleSize);
        for (int i = 0; i < sampleSize; i++) {
            carWeights.add(25.0 + random.nextDouble() * 115.0); // 25 to 140 tons
        }

        // Warm up JIT
        for (int i = 0; i < 5; i++) {
            List<Double> warmup = new ArrayList<>();
            for (Double w : carWeights) {
                if (w > 80.0) warmup.add(w);
            }
        }

        // Measure Iterative Loop
        long startLoop = System.nanoTime();
        List<Double> loopFiltered = new ArrayList<>();
        for (Double w : carWeights) {
            if (w > 80.0) {
                loopFiltered.add(w);
            }
        }
        long endLoop = System.nanoTime();
        long loopNanos = endLoop - startLoop;

        // Measure Stream Pipeline
        long startStream = System.nanoTime();
        List<Double> streamFiltered = carWeights.stream()
                .filter(w -> w > 80.0)
                .collect(Collectors.toList());
        long endStream = System.nanoTime();
        long streamNanos = endStream - startStream;

        double loopMs = loopNanos / 1_000_000.0;
        double streamMs = streamNanos / 1_000_000.0;

        String faster = loopNanos <= streamNanos ? "Classic Iterative Loop" : "Java Stream Pipeline";
        double factor = loopNanos <= streamNanos ?
                (double) streamNanos / Math.max(1, loopNanos) :
                (double) loopNanos / Math.max(1, streamNanos);

        return new BenchmarkResult(
                sampleSize,
                loopNanos,
                streamNanos,
                Math.round(loopMs * 1000.0) / 1000.0,
                Math.round(streamMs * 1000.0) / 1000.0,
                faster,
                Math.round(factor * 10.0) / 10.0
        );
    }
}
