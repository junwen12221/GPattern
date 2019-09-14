package cn.lightfish.pattern.benchmarks;

import cn.lightfish.pattern.*;
import org.openjdk.jmh.annotations.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BenchMark2 {
    @State(Scope.Benchmark)
    public static class ExecutionPlan {


        public int iterations;

        public static final ByteBuffer password = StandardCharsets.UTF_8.encode("select id from travelrecord where user_id = 1;select id from travelrecord where user_id = 1;select id from travelrecord where user_id = 1;");
        private static final GPattern pattern;

        static {
            GPatternBuilder builder = new GPatternBuilder(0);
            int i = builder.addRule("select id from travelrecord where user_id = 1;select id from travelrecord where user_id = 1;select id from travelrecord where user_id = {1};");
            int i2 = builder.addRule("select id from travelrecord where jjjj");
            pattern =  builder.createGroupPattern();
        }

        @Setup(Level.Invocation)
        public void setUp() {

        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 1)
    public void benchMurmur3_128(ExecutionPlan plan) {
        GPattern pattern = plan.pattern;
        GPatternMatcher matcher = pattern.matcher(plan.password);
        if (!matcher.acceptAll()){
            throw new UnsupportedOperationException();
        }
    }

}