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

import lombok.ToString;

@ToString
public class GPatternToken implements Cloneable, GPatternSeq {
    int hash;
    String symbol;
    Object attr;
    int startOffset = -1;
    int endOffset = -1;
    GPatternUTF8Lexer lexer;

    public GPatternToken(int hash, String symbol, Object attr) {
        this.hash = hash;
        this.symbol = symbol;
        this.attr = attr;
    }

    public String getSymbol() throws NullPointerException {
        if (symbol != null){
           return symbol;
        }
        if (lexer!=null){
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
        return symbol != null ? symbol.equals(token.symbol) : token.symbol == null;
    }

    @Override
    public int hashCode() {
        int result = hash;
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        return result;
    }

    @Override
    protected GPatternToken clone() throws CloneNotSupportedException {
        return (GPatternToken) super.clone();
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setLexer(GPatternUTF8Lexer lexer) {
        this.lexer = lexer;
    }

    public <T> T getAttr() {
        return (T)attr;
    }
}