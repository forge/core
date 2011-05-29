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

import java.util.ArrayList;
import java.util.List;

public class ExecutionImpl implements Execution {
    private String id;
    private String phase;
    private List<String> goals = new ArrayList<String>();
    private Configuration configuration;

    @Override public String getId() {
        return id;
    }

    @Override public String getPhase() {
        return phase;
    }

    @Override public List<String> getGoals() {
        return goals;
    }

    @Override public Configuration getConfig() {
        return configuration;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void addGoal(String goal) {
        goals.add(goal);
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("<execution>")
                .append("<id>").append(id).append("</id>")
                .append("<phase>").append(phase).append("</phase>");
        for (String goal : goals) {
            b.append("<goal>").append(goal).append("</goal>");
        }

        if(configuration != null) {
            b.append(configuration.toString());
        }

        b.append("</execution>");


        return b.toString();
    }
}
