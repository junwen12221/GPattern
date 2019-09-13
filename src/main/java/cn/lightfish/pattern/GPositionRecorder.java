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

import java.util.HashMap;

public class GPositionRecorder {
    final HashMap<String, GPatternPosition> map = new HashMap<>();
    GPatternPosition currentPosition;
    String name;

    public void startRecordName(String name, int startOffset) {
        if (this.name == null || !this.name.equals(name)) {
            this.currentPosition = map.get(name);
            this.name = name;
        }
        if (currentPosition == null) {
            map.put(name, currentPosition = new GPatternPosition());
            currentPosition.start = Integer.MAX_VALUE;
        }
        currentPosition.start = Math.min(currentPosition.start, startOffset);
    }

    public void record(int endOffset) {
        currentPosition.end = Math.max(currentPosition.end, endOffset);
    }

}
