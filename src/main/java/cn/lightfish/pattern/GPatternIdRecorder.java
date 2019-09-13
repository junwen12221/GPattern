/**
 * Copyright (C) <2019>  <chen junwen>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If
 * not, see <http://www.gnu.org/licenses/>.
 */
package cn.lightfish.pattern;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public interface GPatternIdRecorder {
    void startRecordTokenChar(int startOffset);

    void append(int c);

    void endRecordTokenChar(int endOffset);

    GPatternToken createConstToken(Object o);

    GPatternToken toCurToken();

    GPatternIdRecorder createCopyRecorder();

    public void load(Map<String, Object> map);

    GPatternToken getConstToken(String a);

    public class GPatternIdRecorderImpl implements GPatternIdRecorder {
        final static int WORD_LENGTH = 64;
        final byte[] words = new byte[WORD_LENGTH];
        final Map<Integer, GPatternToken> longTokenHashMap = new HashMap<>();
        final Map<String, GPatternToken> tokenMap = new HashMap<>();
        int offset = 0;
        int hash = 0;
        ///////////position//////////
        int tokenStartOffset;
        int tokenEndOffset;
        final GPatternToken tmp = new GPatternToken(0, null, null);

        final StringBuilder debugBuffer;

        public GPatternIdRecorderImpl(boolean debug) {
            this.debugBuffer = debug ? new StringBuilder() : null;
        }

        public GPatternIdRecorder createCopyRecorder() {
            GPatternIdRecorderImpl idRecorder = new GPatternIdRecorderImpl(false);
            idRecorder.longTokenHashMap.putAll(this.longTokenHashMap);
            idRecorder.tokenMap.putAll(this.tokenMap);
            return idRecorder;
        }

        public void load(Map<String, Object> map) {
            Objects.requireNonNull(map);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                startRecordTokenChar(0);
                byte[] key = entry.getKey().getBytes(StandardCharsets.UTF_8);
                if (key.length > WORD_LENGTH)
                    throw new GPatternException.TooLongConstTokenException("{0}", entry.getKey());
                for (byte b : key) {
                    append(b);
                }
                endRecordTokenChar(key.length);
                createConstToken(entry.getValue());
            }
        }

        @Override
        public GPatternToken getConstToken(String a) {
            return tokenMap.get(a);
        }

        private void addToken(String keyword, GPatternToken token) {
            if (keyword.length() > WORD_LENGTH) throw new UnsupportedOperationException();
            if (longTokenHashMap.containsKey(token.hash)) throw new UnsupportedOperationException();
            longTokenHashMap.put(token.hash, token);
            tokenMap.put(keyword, token);
        }

        @Override
        public void append(int b) {
            debugAppend(b);
            words[offset] = (byte) b;
            hash = 31 * hash + b;
            offset = Math.min(++offset, WORD_LENGTH - 1);
        }

        private static boolean equal(long curHash, int length, byte[] word, GPatternToken constToken) {
            if (curHash == constToken.hash) {
                String symbol = constToken.getSymbol();
                if (length != symbol.length()) {
                    return false;
                }
                for (int i = 0; i < length; i++) {
                    if (symbol.charAt(i) != word[i]) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public void startRecordTokenChar(int startOffset) {
            this.debugClear();
            this.offset = 0;
            this.hash = 0;
            this.tokenStartOffset = startOffset;
            this.tokenEndOffset = startOffset;
        }

        @Override
        public void endRecordTokenChar(int endOffset) {
            hash();
            this.tokenEndOffset = endOffset;
        }

        private void hash() {
            if (this.hash == 0) {
                int h = 0;
                for (int i = 0; i < offset; i++) h = 31 * h + words[i];
                hash = h;
            }
        }

        public boolean isToken(GPatternToken token) {
            return equal(hash, offset, words, token);
        }

        ///////////////////////////debug/////////////////////////////////
        private void debugAppend(int c) {
            if (this.debugBuffer != null) this.debugBuffer.append((char) c);
        }

        private void debugClear() {
            if (this.debugBuffer != null) this.debugBuffer.setLength(0);
        }

        public GPatternToken createConstToken(Object attr) {
            int length = tokenEndOffset - tokenStartOffset;
            if (length > WORD_LENGTH) throw new GPatternException.TooLongConstTokenException("{0}", new String(words));
            GPatternToken keyword = longTokenHashMap.get(hash);
            if (keyword != null && equal(hash, length, words, keyword)) {
                return keyword;
            } else if (keyword == null) {
                for (int i = 0; i < length; i++) {
                    if (0 > words[i])
                        throw new GPatternException.NonASCIICharsetConstTokenException("{0}", new String(words, 0, WORD_LENGTH));
                }
                String symbol = new String(this.words, 0, tokenEndOffset - tokenStartOffset).intern();
                GPatternToken token = new GPatternToken(this.hash, symbol, attr);
                addToken(symbol, token);
                return token;
            } else {
                throw new GPatternException.ConstTokenHashConflictException(" {0} {1}", keyword.getSymbol(), new String(words, 0, WORD_LENGTH));
            }
        }

        public GPatternToken toCurToken() {
            hash();
            tmp.startOffset = this.tokenStartOffset;
            tmp.endOffset = this.tokenEndOffset;
            tmp.hash = this.hash;
            tmp.attr = null;
            tmp.symbol = null;
            GPatternToken keyword = longTokenHashMap.get(hash);
            if ((keyword != null) && equal(hash, offset, words, keyword)) {
                tmp.attr = keyword.attr;
                tmp.symbol = keyword.getSymbol();
            }
            return tmp;
        }
    }
}