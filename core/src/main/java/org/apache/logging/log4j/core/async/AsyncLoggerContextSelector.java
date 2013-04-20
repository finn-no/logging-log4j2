/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.async;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.selector.ContextSelector;

/**
 * {@code ContextSelector} that returns the singleton {@code AsyncLoggerContext}.
 */
public class AsyncLoggerContextSelector implements ContextSelector {

    private static final AsyncLoggerContext CONTEXT = new AsyncLoggerContext(
            "AsyncLoggerContext");

    public LoggerContext getContext(String fqcn, ClassLoader loader,
            boolean currentContext) {
        return CONTEXT;
    }

    public List<LoggerContext> getLoggerContexts() {
        List<LoggerContext> list = new ArrayList<LoggerContext>();
        list.add(CONTEXT);
        return Collections.unmodifiableList(list);
    }

    public LoggerContext getContext(String fqcn, ClassLoader loader,
            boolean currentContext, URI configLocation) {
        return CONTEXT;
    }

}