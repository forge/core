/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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
