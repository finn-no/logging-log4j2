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
package org.apache.logging.log4j.core.appender.db.jpa;

import java.lang.reflect.Constructor;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttr;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 * This Appender writes logging events to a relational database using the Java Persistence API. It requires a
 * pre-configured JPA persistence unit and a concrete implementation of the abstract {@link LogEventWrapperEntity} class
 * decorated with JPA annotations.
 * 
 * @see LogEventWrapperEntity
 */
@Plugin(name = "Jpa", category = "Core", elementType = "appender", printObject = true)
public final class JPAAppender extends AbstractDatabaseAppender<JPADatabaseManager> {
    /**
     * Factory method for creating a JPA appender within the plugin manager.
     * 
     * @param name
     *            The name of the appender.
     * @param suppressExceptions
     *            {@code "true"} (default) if logging exceptions should be hidden from the application, false otherwise.
     * @param filter
     *            The filter, if any, to use.
     * @param bufferSize
     *            If an integer greater than 0, this causes the appender to buffer log events and flush whenever the
     *            buffer reaches this size.
     * @param entityClassName
     *            The fully qualified name of the concrete {@link LogEventWrapperEntity} implementation that has JPA
     *            annotations mapping it to a database table.
     * @param persistenceUnitName
     *            The name of the JPA persistence unit that should be used for persisting log events.
     * @return a new JPA appender.
     */
    @PluginFactory
    public static JPAAppender createAppender(@PluginAttr("name") final String name,
            @PluginAttr("suppressExceptions") final String suppressExceptions,
            @PluginElement("filter") final Filter filter, @PluginAttr("bufferSize") final String bufferSize,
            @PluginAttr("entityClassName") final String entityClassName,
            @PluginAttr("persistenceUnitName") final String persistenceUnitName) {
        if (entityClassName == null || entityClassName.length() == 0 || persistenceUnitName == null
                || persistenceUnitName.length() == 0) {
            LOGGER.error("Attributes entityClassName and persistenceUnitName are required for JPA Appender.");
            return null;
        }

        int bufferSizeInt;
        try {
            bufferSizeInt = bufferSize == null || bufferSize.length() == 0 ? 0 : Integer.parseInt(bufferSize);
        } catch (final NumberFormatException e) {
            LOGGER.warn("Buffer size [" + bufferSize + "] not an integer, using no buffer.");
            bufferSizeInt = 0;
        }

        final boolean handleExceptions = suppressExceptions == null || !Boolean.parseBoolean(suppressExceptions);

        try {
            @SuppressWarnings("unchecked")
            final Class<? extends LogEventWrapperEntity> entityClass = (Class<? extends LogEventWrapperEntity>) Class
                    .forName(entityClassName);

            if (!LogEventWrapperEntity.class.isAssignableFrom(entityClass)) {
                LOGGER.error("Entity class [{}] does not extend LogEventWrapperEntity.", entityClassName);
                return null;
            }

            try {
                entityClass.getConstructor();
            } catch (final NoSuchMethodException e) {
                LOGGER.error("Entity class [{}] does not have a no-arg constructor. The JPA provider will reject it.",
                        entityClassName);
                return null;
            }

            final Constructor<? extends LogEventWrapperEntity> entityConstructor = entityClass
                    .getConstructor(LogEvent.class);

            final String managerName = "jpaManager{ description=" + name + ", bufferSize=" + bufferSizeInt
                    + ", persistenceUnitName=" + persistenceUnitName + ", entityClass=" + entityClass.getName() + "}";

            final JPADatabaseManager manager = JPADatabaseManager.getJPADatabaseManager(managerName, bufferSizeInt,
                    entityClass, entityConstructor, persistenceUnitName);
            if (manager == null) {
                return null;
            }

            return new JPAAppender(name, filter, handleExceptions, manager);
        } catch (final ClassNotFoundException e) {
            LOGGER.error("Could not load entity class [{}].", entityClassName, e);
            return null;
        } catch (final NoSuchMethodException e) {
            LOGGER.error("Entity class [{}] does not have a constructor with a single argument of type LogEvent.",
                    entityClassName);
            return null;
        }
    }

    private final String description;

    private JPAAppender(final String name, final Filter filter, final boolean handleException,
            final JPADatabaseManager manager) {
        super(name, filter, handleException, manager);
        this.description = this.getName() + "{ manager=" + this.getManager() + " }";
    }

    @Override
    public String toString() {
        return this.description;
    }
}