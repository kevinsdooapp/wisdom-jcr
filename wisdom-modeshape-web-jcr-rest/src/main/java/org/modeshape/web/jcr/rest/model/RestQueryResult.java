/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2013 - 2015 Wisdom Framework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/*
 * ModeShape (http://www.modeshape.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.modeshape.web.jcr.rest.model;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.modeshape.common.util.StringUtil;
import org.wisdom.api.content.Json;

import java.util.*;

/**
 * A REST representation of a {@link javax.jcr.query.QueryResult}
 *
 * @author Horia Chiorean (hchiorea@redhat.com)
 */
public final class RestQueryResult implements JSONAble {
    private final Map<String, String> columns;
    private final List<RestRow> rows;

    /**
     * Creates an empty instance
     */
    public RestQueryResult() {
        columns = new LinkedHashMap<String, String>();
        rows = new ArrayList<RestRow>();
    }

    /**
     * Adds a new column to this result.
     *
     * @param name a {@code non-null} string, the name of the column
     * @param type a {@code non-null} string, the type of the column
     * @return this instance
     */
    public RestQueryResult addColumn(String name,
                                     String type) {
        if (!StringUtil.isBlank(name)) {
            columns.put(name, type);
        }
        return this;
    }

    /**
     * Adds a new row to this result
     *
     * @param row a {@code non-null} {@link RestRow}
     * @return this instance
     */
    public RestQueryResult addRow(RestRow row) {
        rows.add(row);
        return this;
    }

    @Override
    public ObjectNode toJSON(Json json) {
        ObjectNode result = json.newObject();
        if (!columns.isEmpty()) {
            ObjectNode columnsNode =  result.putObject("columns");
            for (String column: columns.keySet()) {
                columnsNode.put(column, columns.get(column));
            }
        }
        if (!rows.isEmpty()) {
            ArrayNode rows = result.putArray("rows");
            for (RestRow row : this.rows) {
                rows.add(row.toJSON(json));
            }
        }
        return result;
    }

    public class RestRow implements JSONAble {
        private final Map<String, String> values;

        public RestRow() {
            this.values = new LinkedHashMap<String, String>();
        }

        public void addValue(String name,
                             String value) {
            if (!StringUtil.isBlank(name) && !StringUtil.isBlank(value)) {
                values.put(name, value);
            }
        }

        @Override
        public ObjectNode toJSON(Json json) {
            ObjectNode objectNode = json.newObject();
            for (String key : values.keySet()) {
                objectNode.put(key, values.get(key));
            }
            return objectNode;
        }
    }
}
