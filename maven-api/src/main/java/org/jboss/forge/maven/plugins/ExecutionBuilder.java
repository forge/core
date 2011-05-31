/*
 *
 *  * JBoss, Home of Professional Open Source
 *  * Copyright 2011, Red Hat, Inc., and individual contributors
 *  * by the @authors tag. See the copyright.txt in the distribution for a
 *  * full listing of individual contributors.
 *  *
 *  * This is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU Lesser General Public License as
 *  * published by the Free Software Foundation; either version 2.1 of
 *  * the License, or (at your option) any later version.
 *  *
 *  * This software is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this software; if not, write to the Free
 *  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.jboss.forge.maven.plugins;

import java.util.List;

public class ExecutionBuilder implements Execution{
    private ExecutionImpl execution;

    private ExecutionBuilder() {
        execution = new ExecutionImpl();
    }

    private ExecutionBuilder(ExecutionImpl execution) {
        this.execution = execution;
    }

    public static ExecutionBuilder create() {
        return new ExecutionBuilder();
    }

    public static ExecutionBuilder create(ExecutionImpl execution) {
        return new ExecutionBuilder(execution);
    }

    public ExecutionBuilder setId(String id) {
        execution.setId(id);
        return this;
    }

    public ExecutionBuilder setPhase(String phase) {
        execution.setPhase(phase);
        return this;
    }

    public ExecutionBuilder addGoal(String goal) {
        execution.addGoal(goal);
        return this;
    }

    @Override public String getId() {
        return execution.getId();
    }

    @Override public String getPhase() {
        return execution.getPhase();
    }

    @Override public List<String> getGoals() {
        return execution.getGoals();
    }

    @Override public String toString() {
        return execution.toString();
    }

    @Override public Configuration getConfig() {
        return execution.getConfig();
    }

    public ExecutionBuilder setConfig(Configuration configuration) {
        execution.setConfiguration(configuration);
        return this;
    }
}
