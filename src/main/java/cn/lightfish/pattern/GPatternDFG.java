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

import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public interface GPatternDFG {
    int addRule(Iterator<GPatternSeq> format);

    GPatternMatcher getMatcher();

    Map<String, GPatternPosition> getVariables();

    final class DFGImpl implements GPatternDFG {
        private final State rootState = new State(0);
        private final Map<String, GPatternPosition> variables = new HashMap<>();
        int identifierGenerator = 0;

        public DFGImpl(int identifierGenerator) {
            this.identifierGenerator = identifierGenerator;
        }

        public DFGImpl() {
            this(0);
        }

        @Override
        public int addRule(Iterator<GPatternSeq> format) {
            State state = this.rootState;
            State lastState = null;
            String lastName = null;
            GPatternSeq nextToken = null;
            for (; format.hasNext(); ) {
                lastState = state;
                GPatternSeq token = format.next();
                if ("{".equals(token.getSymbol())) {
                    if (!format.hasNext()) throw new GPatternException.NameSyntaxException("'{' name ends early");
                    String name = format.next().getSymbol().trim();
                    if (lastName != null)
                        throw new GPatternException.NameAdjacentException("'{'{0}'}' '{'{1}'}' is not allowed", lastName, name);
                    if (!variables.containsKey(name)) {
                        variables.put(name, null);
                    } else if ((!name.equals(state.name))) {
                        throw new GPatternException.NameAmbiguityException("'{'{0}'}' has already existed", name);
                    }
                    state.addWildcard(name, new State(state.depth + 1));
                    if (!format.hasNext())
                        throw new GPatternException.NameSyntaxException("'{'{0} ends early", name);
                    GPatternSeq last = format.next();
                    if ("}".equals(last.getSymbol())) {
                        if (state.matcher != null) {
                            state = state.matcher;
                        }
                        lastName = name;
                    } else
                        throw new GPatternException.NameSyntaxException("'{'{0} {1}   The name can only identify one", name, last.getSymbol());
                } else {
                    state = state.addState(token);
                    nextToken = token;
                    lastName = null;
                }
            }
            int i = identifierGenerator++;

            if (lastState != null && lastState.name != null && state.name == null && state.success.isEmpty() && state.matcher == null) {
                state.end(i);
                state = lastState;
            }
            if (!state.isEnd()) {
                state.end(i);
            }
            ////优化
            if (state.success.size() == 1 && nextToken != null) {
                state.nextToken = nextToken;
                state.nextState = state.success.getFirst();
                state.success.clear();
            }
            return i;
        }

        @Override
        public GPatternMatcher getMatcher() {
            return new MatcherImpl(this);
        }

        @Override
        public Map<String, GPatternPosition> getVariables() {
            return variables;
        }

        public static class State {
            final int depth;
            public GPatternSeq nextToken;
            public State nextState;
            private String name;
            private final IntObjectHashMap<State> success = IntObjectHashMap.newMap();
            private State matcher;
            private int id = Integer.MIN_VALUE;
            private boolean end = false;

            public State(int depth) {
                this.depth = depth;
            }

            public State addState(GPatternSeq next) {
                if (success.containsKey(next.hashCode())) {
                    return success.get(next.hashCode());
                } else {
                    State state = new State(depth + 1);
                    success.put(next.hashCode(), state);
                    return state;
                }
            }

            public void addWildcard(String name, State matcher) {
                if (this.name == null) {
                    this.name = name;
                    this.matcher = matcher;
                } else if (this.name.equals(name)) {

                } else
                    throw new GPatternException.NameLocationAmbiguityException("'{' {0} '}' '{' {1} '}' are ambiguous", this.name, name);
            }

            public State accept(GPatternSeq token, int startOffset, int endOffset, MatcherImpl map) {
                if (nextToken != null) {
                    if (token.equals(nextToken)) {
                        return nextState;
                    }
                }
                if (!success.isEmpty()) {
                    State state = success.get(token.hashCode());
                    if (state != null) {
                        return state;
                    }
                }
                if (name != null) {
                    return wildCardMatch(token, startOffset, endOffset, map);
                }
                return null;
            }

            @NotNull
            private State wildCardMatch(GPatternSeq token, int startOffset, int endOffset, MatcherImpl map) {
                State accept = matcher.accept(token, startOffset, endOffset, map);
                if (accept != null) {
                    return accept;
                } else {
                    map.context.startRecordName(name, startOffset);
                    map.context.record(endOffset);
                    return this;
                }
            }

            public void end(int id) {
                if (!end) {
                    this.id = id;
                    this.end = true;
                }
            }

            public boolean isEnd() {
                return end;
            }
        }
    }

    public final class MatcherImpl implements GPatternMatcher {
        private final DFGImpl.State rootState;
        private final GPositionRecorder context;
        private DFGImpl.State state;

        public MatcherImpl(DFGImpl dfg) {
            this.rootState = dfg.rootState;
            this.context = new GPositionRecorder(dfg.variables);
        }

        public boolean accept(GPatternSeq token) {
            if (this.state == null) return false;
            DFGImpl.State orign = this.state;
            this.state  = this.state.accept(token, token.getStartOffset(), token.getEndOffset(), this);
            return ((orign) != state);
        }

        public boolean acceptAll() {
            return state != null && state.isEnd();
        }

        @Override
        public int id() {
            return acceptAll() ? state.id : Integer.MIN_VALUE;
        }

        @Override
        public Map<String, GPatternPosition> context() {
            return context.map;
        }

        @Override
        public void reset() {
            this.state = rootState;
            this.context.name = null;
            this.context.currentPosition = null;
        }
    }
}