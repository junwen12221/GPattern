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
public final class GPatternToken implements Cloneable, GPatternSeq {
    int hash;
    int startOffset = -1;
    int endOffset = -1;
    int length;
    final String symbolText;
    final byte[] symbol;
    GPatternUTF8Lexer lexer;

    public GPatternToken(int hash, int length, String symbolText, GPatternUTF8Lexer lexer) {
        this.hash = hash;
        this.length = length;
        this.symbolText = symbolText;
        this.symbol = this.symbolText != null ? symbolText.getBytes(StandardCharsets.UTF_8) : null;
        this.lexer = lexer;
    }

    public String getSymbol() throws NullPointerException {
        if (symbolText != null) {
            return symbolText;
        }
        return lexer.getString(startOffset, endOffset);
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
    public boolean equals(Object oo) {
        GPatternToken o = (GPatternToken) oo;
        return lexer.equals(startOffset, endOffset, o.symbol);
    }

    @Override
    public boolean equalsLength(GPatternToken oo) {
        return this.length == oo.length;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public GPatternToken clone() throws CloneNotSupportedException {
        return (GPatternToken) super.clone();
    }

    @Override
    public String toString() {
        return "GPatternToken{" +
                "hash=" + hash +
                ", startOffset=" + startOffset +
                ", endOffset=" + endOffset +
                ", symbolText='" + symbolText + '\'' +
                ", symbol=" + Arrays.toString(symbol) +
                ", lexer=" + lexer +
                '}';
    }
}