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

package org.jboss.seam.forge.maven.plugins;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;

import java.io.ByteArrayInputStream;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */

public class MavenPluginAdapter extends Plugin implements MavenPlugin {
    public MavenPluginAdapter(MavenPlugin mavenPlugin) {
        Dependency dependency = mavenPlugin.getDependency();

        setGroupId(dependency.getGroupId());
        setArtifactId(dependency.getArtifactId());
        setVersion(dependency.getVersion());
        setConfiguration(parseConfig(mavenPlugin));
    }

    private Xpp3Dom parseConfig(MavenPlugin mavenPlugin) {
        if (mavenPlugin.getConfig() == null) {
            return null;
        }

        try {
            return Xpp3DomBuilder.build(
                    new ByteArrayInputStream(
                            mavenPlugin.getConfig().toString().getBytes()), "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException("Exception while parsing configuration", ex);
        }
    }

    public MavenPluginAdapter(Plugin plugin) {
        Plugin clone = plugin.clone();

        setGroupId(clone.getGroupId());
        setArtifactId(clone.getArtifactId());
        setVersion(clone.getVersion());
        setConfiguration(plugin.getConfiguration());
    }

    @Override
    public MavenPluginConfiguration getConfig() {
        Xpp3Dom dom = (Xpp3Dom) super.getConfiguration();


        return new MavenPluginConfigurationImpl(dom);
    }

    @Override
    public Dependency getDependency() {
        return DependencyBuilder.create()
                .setGroupId(getGroupId())
                .setArtifactId(getArtifactId())
                .setVersion(getVersion());
    }
}
