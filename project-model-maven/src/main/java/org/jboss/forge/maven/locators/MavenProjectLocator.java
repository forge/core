/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.locators;

import java.io.File;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.ProjectImpl;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.locator.ProjectLocator;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;

/**
 * Locate a Maven project starting in the current directory, and progressing up the chain of parent directories until a
 * project is found, or the root directory is found. If a project is found, return the {@link File} referring to the
 * directory containing that project, or return null if no projects were found.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenProjectLocator implements ProjectLocator
{
    private final ProjectFactory factory;

    private final Instance<MavenCoreFacet> coreFacetInstance;

    @Inject
    public MavenProjectLocator(final ProjectFactory factory, @Any final Instance<MavenCoreFacet> coreFacet)
    {
        this.factory = factory;
        this.coreFacetInstance = coreFacet;
    }

    @Override
    public Project createProject(final DirectoryResource dir)
    {
        Project result = new ProjectImpl(factory, dir);
        MavenCoreFacet maven = coreFacetInstance.get();
        maven.setProject(result);
        if (!maven.isInstalled())
        {
            result.installFacet(maven);
        }
        else
            result.registerFacet(maven);
        
        if(!result.hasFacet(MavenCoreFacet.class))
        {
            throw new IllegalStateException("Could not create Maven project [MavenCoreFacet could not be installed.]");
        }

        return result;
    }

    @Override
    public boolean containsProject(final DirectoryResource dir)
    {
        Resource<?> pom = dir.getChild("pom.xml");
        return pom.exists();
    }
}
