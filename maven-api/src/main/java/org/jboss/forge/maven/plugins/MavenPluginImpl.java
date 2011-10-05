/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.maven.plugins;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.project.dependencies.Dependency;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class MavenPluginImpl implements MavenPlugin {
    private Dependency dependency;
    private Configuration configuration;
    private final List<Execution> executions = new ArrayList<Execution>();
    private boolean extensions;

    public MavenPluginImpl() {
    }

    public MavenPluginImpl(final MavenPlugin plugin) {
        this.dependency = plugin.getDependency();
        this.configuration = plugin.getConfig();
    }

    @Override
    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(final Dependency dependency) {
        this.dependency = dependency;
    }

    @Override
    public Configuration getConfig() {
        if (configuration == null) {
            configuration = ConfigurationBuilder.create();
        }
        return configuration;
    }

    @Override
    public List<Execution> listExecutions() {
        return executions;
    }

    @Override
    public boolean isExtensionsEnabled() {
        return extensions;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("<plugin>");
        if (dependency.getGroupId() != null) {
            b.append("<groupId>").append(dependency.getGroupId()).append("</groupId>");
        }

        if (dependency.getArtifactId() != null) {
            b.append("<artifactId>").append(dependency.getArtifactId()).append("</artifactId>");
        }

        if (dependency.getVersion() != null) {
            b.append("<version>").append(dependency.getVersion()).append("</version>");
        }

        if(extensions) {
            b.append("<extensions>true</extensions>");
        }

        if (configuration != null) {
            b.append(configuration.toString());
        }

        if (executions.size() > 0) {
            b.append("<executions>");
            for (Execution execution : executions) {
                b.append(execution.toString());
            }
            b.append("</executions>");
        }

        b.append("</plugin>");
        return b.toString();
    }

    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }

    public void addExecution(final Execution execution) {
        executions.add(execution);
    }

    public void setExtenstions(boolean extenstions) {
        this.extensions = extenstions;
    }
}
