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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.wisdom.api.content.Json;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A representation of an {@link Exception} which is used by the REST service to signal clients that a server-side exception
 * has occurred.
 *
 * @author Horia Chiorean (hchiorea@redhat.com)
 */
public final class RestException implements JSONAble {
    private final String message;
    private final String stackTrace;

    /**
     * Creates a new exception, using only a message
     *
     * @param message a {@code non-null} string
     */
    public RestException( String message ) {
        this.message = message;
        this.stackTrace = null;
    }

    /**
     * Creates a new exception, based on an existing {@link Throwable}
     *
     * @param t a {@code non-null} {@link Exception}
     */
    public RestException( Throwable t ) {
        this.message = t.getMessage();
        StringWriter stringWriter = new StringWriter();
        for (StackTraceElement trace: t.getStackTrace()) {
            stringWriter.append(trace.toString());
        }
        this.stackTrace = stringWriter.toString();
    }

    @Override
    public ObjectNode toJSON(Json json) {
        ObjectNode object = json.newObject();
        object.put("exception", message);
        if (stackTrace != null) {
            object.put("stacktrace", stackTrace);
        }
        return object;
    }
}
