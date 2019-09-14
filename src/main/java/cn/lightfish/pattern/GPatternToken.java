/**
 * Copyright (C) <2019>  <chen junwen>
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program.  If
 * not, see <http://www.gnu.org/licenses/>.
 */
package cn.lightfish.pattern;

import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@ToString
public class GPatternToken implements Cloneable, GPatternSeq {
    int hash;
    byte[] symbol;
    final String symbolText;
    Object attr;
    int startOffset = -1;
    int endOffset = -1;
    GPatternUTF8Lexer lexer;

    public GPatternToken(int hash, String symbolText, Object attr) {
        this.hash = hash;
        this.symbolText = symbolText;
        if (this.symbolText != null) {
            this.symbol = symbolText.getBytes(StandardCharsets.UTF_8);
        }
        this.attr = attr;
    }

    public String getSymbol() throws NullPointerException {
        if (symbolText != null) {
            return symbolText;
        }
        if (lexer != null) {
            return lexer.getString(startOffset, endOffset);
        }
        return null;
    }

    @Override
    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public int getEndOffset() {
        return endOffset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GPatternToken token = (GPatternToken) o;
        if (hash != token.hash) return false;
        return symbol != null ? Arrays.equals(symbol, token.symbol) : token.symbol == null;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    protected GPatternToken clone() throws CloneNotSupportedException {
        return (GPatternToken) super.clone();
    }

    public void setLexer(GPatternUTF8Lexer lexer) {
        this.lexer = lexer;
    }

    public <T> T getAttr() {
        return (T) attr;
    }
}