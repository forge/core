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
    private List<Dependency> pluginDependencies = new ArrayList<Dependency>();

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
    public List<Dependency> getDirectDependencies() {
       return pluginDependencies;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("<plugin>");
        appendDependency(b, dependency);

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
        
        if (pluginDependencies.size() > 0) {
           b.append("<dependencies>");
           for (Dependency pluginDependency : pluginDependencies) {
               b.append("<dependency>");
               appendDependency(b, pluginDependency);
               b.append("</dependency>");
           }
           b.append("</dependencies>");
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
    
    public void addPluginDependency(final Dependency dependency) {
       pluginDependencies.add(dependency);
    }
    
    private void appendDependency(StringBuilder buffer, Dependency appendDependency) {
       if (appendDependency.getGroupId() != null) {
          buffer.append("<groupId>").append(appendDependency.getGroupId()).append("</groupId>");
      }

      if (appendDependency.getArtifactId() != null) {
         buffer.append("<artifactId>").append(appendDependency.getArtifactId()).append("</artifactId>");
      }

      if (appendDependency.getVersion() != null) {
         buffer.append("<version>").append(appendDependency.getVersion()).append("</version>");
      }
    }

}
