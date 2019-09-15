package cn.lightfish.pattern;

import com.alibaba.fastsql.util.FnvHash;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class GPatternIdRecorderImpl implements GPatternIdRecorder {
    final static int WORD_LENGTH = 64;
    final LongObjectHashMap<GPatternToken> longTokenHashMap = LongObjectHashMap.newMap();
    final Map<String, GPatternToken> tokenMap = new HashMap<>();
    public final static long BASIC = 0xcbf29ce484222325L;
    public final static long PRIME = 0x100000001b3L;
    final GPatternToken tmp = new GPatternToken(0, 0,null, null);
    private GPatternUTF8Lexer lexer;

    public GPatternIdRecorderImpl(boolean debug) {

    }

    public GPatternIdRecorder createCopyRecorder() {
        GPatternIdRecorderImpl idRecorder = new GPatternIdRecorderImpl(false);
        idRecorder.longTokenHashMap.putAll(this.longTokenHashMap);
        idRecorder.tokenMap.putAll(this.tokenMap);
        return idRecorder;
    }

    public void load(Set<String> map) {
        Objects.requireNonNull(map);
        for (String entry : map) {
            startRecordTokenChar(0);
            byte[] key = entry.getBytes(StandardCharsets.UTF_8);
            if (key.length > WORD_LENGTH)
                throw new GPatternException.TooLongConstTokenException("{0}", entry);
            for (byte b : key) {
                append(b);
            }
            endRecordTokenChar(key.length);
            createConstToken(entry);
        }
    }

    @Override
    public GPatternToken getConstToken(String a) {
        return tokenMap.get(a);
    }

    @Override
    public void setLexer(GPatternUTF8Lexer lexer) {
        this.lexer = lexer;
        this.tmp.lexer = lexer;
    }


    private void addToken(String keyword, GPatternToken token) {
        if (keyword.length() > WORD_LENGTH) throw new UnsupportedOperationException();
        if (longTokenHashMap.containsKey(token.hash)) {
            throw new UnsupportedOperationException();
        }
        longTokenHashMap.put(token.hash, token);
        tokenMap.put(keyword, token);
    }

    @Override
    public final void append(int b) {
        int hash = tmp.hash;
//        hash^= b;
        hash  =hash* 31+b;
        tmp.hash = hash;
    }

    @Override
    public void startRecordTokenChar(int startOffset) {
        tmp.hash = 0;
        tmp.startOffset = startOffset;
    }

    @Override
    public void endRecordTokenChar(int endOffset) {
        tmp.endOffset = endOffset;
        tmp.length = endOffset - tmp.startOffset;

    }

//    @Override
//    public void rangeRecordTokenChar(int startOfffset, int endOffset) {
//        ByteBuffer buffer = lexer.buffer;
//        startRecordTokenChar(startOfffset);
//        for (int i = startOfffset; i <endOffset ; i++) {
//            append(buffer.get(i));
//        }
//        endRecordTokenChar(endOffset);
//    }

    public GPatternToken createConstToken(String keywordText) {
        int hash = fnv1a_64(keywordText);
        byte[] words = keywordText.getBytes(StandardCharsets.UTF_8);
        if (words.length > WORD_LENGTH) throw new GPatternException.TooLongConstTokenException("{0}", keywordText);
        GPatternToken keyword = longTokenHashMap.get(hash);
        if (keyword != null) {
            return keyword;
        } else {
            for (byte word : words) {
                if (0 > word) throw new GPatternException.NonASCIICharsetConstTokenException("{0}", keywordText);
            }
            GPatternToken token = new GPatternToken(hash,words.length, keywordText, lexer);
            addToken(keywordText, token);
            return token;
        }
    }
    public static int fnv1a_64(String input) {
        if (input == null) {
            return 0;
        }

        int hash = 0;
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            hash = hash*31+c;
        }
        return hash;
    }
    public GPatternToken toCurToken() {
        return tmp;
    }
}