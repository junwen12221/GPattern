package cn.lightfish.pattern.benchmarks;

import cn.lightfish.pattern.*;
import org.openjdk.jmh.annotations.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class BenchMark2 {
    public static final ByteBuffer password = StandardCharsets.UTF_8.encode("SELECT a FROM ab             , ee.ff AS f,(SELECT a FROM `schema_bb`.`tbl_bb`,(SELECT a FROM ccc AS c, `dddd`));");

    @State(Scope.Benchmark)
    public static class ExecutionPlan {


        public int iterations;

        private static final GPattern pattern;

        static {
            GPatternBuilder builder = new GPatternBuilder(0);
          //  int i = builder.addRule("SELECT a FROM ab             , ee.ff AS f,(SELECT a FROM `schema_bb`.`tbl_bb`,(SELECT a FROM ccc AS c, `dddd`));");
            int i2 = builder.addRule("SELECT a FROM ab             , ee.ff AS f,(SELECT a FROM `schema_bb`.`tbl_bb`,(SELECT a FROM ccc AS c, `dddd`));");
            pattern =  builder.createGroupPattern();
        }

        @Setup(Level.Invocation)
        public void setUp() {

        }
    }

    public static void main(String[] args) {
//        GPattern pattern = ExecutionPlan.pattern;
//        while (true) {
//            GPatternUTF8Lexer utf8Lexer = pattern.getUtf8Lexer();
//            GPatternIdRecorder idRecorder = pattern.getIdRecorder();
//
//            while (utf8Lexer.nextToken()){
//                GPatternToken gPatternToken = idRecorder.toCurToken();
//            }
//        }

        GPattern pattern = ExecutionPlan.pattern;
        GPatternUTF8Lexer utf8Lexer = pattern.getUtf8Lexer();
        GPatternIdRecorder idRecorder = pattern.getIdRecorder();
        GPatternMatcher matcher = pattern.getMatcher();
//        utf8Lexer.init(password, 0, password.limit());
//        matcher.reset();
//        while (utf8Lexer.nextToken()){
//         idRecorder.toCurToken();
//        }
        while (true) {
            GPatternMatcher matcher1 = pattern.matcher(password);

            if (!matcher1.acceptAll()) {
                System.out.println("-------------------------------------");
            }
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 1)
    public void benchMurmur3_128(ExecutionPlan plan) throws CloneNotSupportedException {
        GPattern pattern = ExecutionPlan.pattern;
        GPatternUTF8Lexer utf8Lexer = pattern.getUtf8Lexer();
        GPatternIdRecorder idRecorder = pattern.getIdRecorder();
        GPatternMatcher matcher = pattern.getMatcher();
//        utf8Lexer.init(password, 0, password.limit());
//        matcher.reset();
//        while (utf8Lexer.nextToken()){
//    idRecorder.toCurToken();
//        }
        GPatternMatcher matcher1 = pattern.matcher(password);

        if (!matcher1.acceptAll()){
            System.out.println("-------------------------------------");
        }
    }

}