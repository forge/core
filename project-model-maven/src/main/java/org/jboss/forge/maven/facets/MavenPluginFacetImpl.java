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

package org.jboss.forge.maven.facets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.facets.exceptions.PluginNotFoundException;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */

@Dependent
@Alias("forge.maven.MavenPluginFacet")
@RequiresFacet(MavenCoreFacet.class)
public class MavenPluginFacetImpl extends BaseFacet implements MavenPluginFacet, Facet {
    private static final String DEFAULT_GROUPID = "org.apache.maven.plugins";

    @Override
    public boolean install() {
        return true;
    }

    @Override
    public boolean isInstalled() {
        try {
            project.getFacet(MavenCoreFacet.class);
            return true;
        } catch (FacetNotFoundException e) {
            return false;
        }
    }

    @Override
    public List<MavenPlugin> listConfiguredPlugins() {
        MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
        Build build = mavenCoreFacet.getPOM().getBuild();
        List<MavenPlugin> plugins = new ArrayList<MavenPlugin>();
        if (build != null) {
            List<org.apache.maven.model.Plugin> pomPlugins = build.getPlugins();

            if (pomPlugins != null) {
                for (org.apache.maven.model.Plugin plugin : pomPlugins) {
                    MavenPluginAdapter adapter = new MavenPluginAdapter(plugin);
                    MavenPluginBuilder pluginBuilder = MavenPluginBuilder
                            .create()
                            .setDependency(
                                    DependencyBuilder.create().setGroupId(plugin.getGroupId())
                                            .setArtifactId(plugin.getArtifactId()).setVersion(plugin.getVersion()))

                            .setConfiguration(adapter.getConfig());

                    plugins.add(pluginBuilder);
                }
            }
        }

        return plugins;
    }

    @Override
    public void addPlugin(final MavenPlugin plugin) {
        MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
        Model pom = mavenCoreFacet.getPOM();
        Build build = pom.getBuild();
        if (build == null) {
            build = new Build();
        }

        build.addPlugin(new MavenPluginAdapter(plugin));
        pom.setBuild(build);
        mavenCoreFacet.setPOM(pom);
    }

    @Override
    public boolean hasPlugin(final Dependency dependency) {
        try {
            getPlugin(dependency);
            return true;
        } catch (PluginNotFoundException ex) {
            return false;
        }
    }

    @Override
    public MavenPlugin getPlugin(final Dependency dependency) {
        String groupId = dependency.getGroupId();

        if ((groupId == null) || groupId.equals("")) {
            groupId = DEFAULT_GROUPID;
        }

        for (MavenPlugin mavenPlugin : listConfiguredPlugins()) {
            Dependency temp = mavenPlugin.getDependency();
            if (DependencyBuilder.areEquivalent(temp, DependencyBuilder.create(dependency).setGroupId(groupId))) {
                return mavenPlugin;
            }
        }

        throw new PluginNotFoundException(groupId, dependency.getArtifactId());

    }

    @Override
    public void removePlugin(final Dependency dependency) {
        MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);

        Build build = mavenCoreFacet.getPOM().getBuild();
        if (build != null) {
            List<org.apache.maven.model.Plugin> pomPlugins = build.getPlugins();
            if (pomPlugins != null) {
                for (org.apache.maven.model.Plugin pomPlugin : pomPlugins) {
                    Dependency pluginDep = DependencyBuilder.create().setGroupId(pomPlugin.getGroupId())
                            .setArtifactId(pomPlugin.getArtifactId());
                    
                    if (DependencyBuilder.areEquivalent(pluginDep, dependency)) {
                        Model pom = mavenCoreFacet.getPOM();
                        pom.getBuild().removePlugin(pomPlugin);
                        mavenCoreFacet.setPOM(pom);
                    }
                }
            }
        }
    }

    @Override
    public void addPluginRepository(final KnownRepository repository) {
        addPluginRepository(repository.name(), repository.getUrl());
    }

    @Override
    public void addPluginRepository(final String name, final String url) {
        if (!hasPluginRepository(url)) {
            MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
            Model pom = maven.getPOM();
            Repository repo = new Repository();
            repo.setId(name);
            repo.setUrl(url);
            pom.getPluginRepositories().add(repo);
            maven.setPOM(pom);
        }
    }

    @Override
    public boolean hasPluginRepository(final KnownRepository repository) {
        return hasPluginRepository(repository.getUrl());
    }

    @Override
    public boolean hasPluginRepository(final String url) {
        if (url != null) {
            MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
            Model pom = maven.getPOM();
            List<Repository> repositories = pom.getPluginRepositories();
            if (repositories != null) {
                for (Repository repo : repositories) {
                    if (repo.getUrl().trim().equals(url.trim())) {
                        repositories.remove(repo);
                        maven.setPOM(pom);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public DependencyRepository removePluginRepository(final String url) {
        if (url != null) {
            MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
            Model pom = maven.getPOM();
            List<Repository> repos = pom.getPluginRepositories();
            for (Repository repo : repos) {
                if (repo.getUrl().equals(url.trim())) {
                    repos.remove(repo);
                    maven.setPOM(pom);
                    return new DependencyRepositoryImpl(repo.getId(), repo.getUrl());
                }
            }
        }
        return null;
    }

    @Override
    public List<DependencyRepository> getPluginRepositories() {
        List<DependencyRepository> results = new ArrayList<DependencyRepository>();
        MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
        Model pom = maven.getPOM();
        List<Repository> repos = pom.getPluginRepositories();

        if (repos != null) {
            for (Repository repo : repos) {
                results.add(new DependencyRepositoryImpl(repo.getId(), repo.getUrl()));
            }
        }
        return Collections.unmodifiableList(results);
    }
}
