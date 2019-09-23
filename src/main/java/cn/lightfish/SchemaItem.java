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
package cn.lightfish;

import java.util.*;

/**
 * https://github.com/junwen12221/GPattern.git
 *
 * @author Junwen Chen
 **/
public class SchemaItem extends Item {
    final Set<SchemaTable> schemas;
    final HashMap<String, Collection<String>> tableMap = new HashMap<>();
    final int tableMapHash;

    public SchemaItem(Set<SchemaTable> schemas, String pettern, String code) {
        super(pettern, code);
        this.schemas = schemas;

        for (SchemaTable schema : schemas) {
            String schemaName = schema.schemaName.toUpperCase();
            String tableName = schema.tableName.toUpperCase();
            Collection<String> strings = tableMap.computeIfAbsent(schemaName, s -> new HashSet<>());
            strings.add(tableName);
        }
        this.tableMapHash = tableMap.hashCode();
    }

    public Set<SchemaTable> getSchemas() {
        return schemas;
    }

    public Map<String, Collection<String>> getTableMap() {
        return tableMap;
    }

    public int getTableMapHash() {
        return tableMapHash;
    }

    @Override
    public String toString() {
        return "SchemaItem{" +
                "schemas=" + schemas +
                ", pettern='" + pettern + '\'' +
                ", code='" + code + '\'' +
                ", instruction=" + instruction +
                '}';
    }
}