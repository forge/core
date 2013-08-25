/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.Root;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenFacetsTest;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.integration.BufferManager;
import org.jboss.forge.shell.util.ResourceUtil;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ProjectModelTest {

    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar").addPackages(true, Root.class.getPackage())
                .addClass(ResourceFactory.class)
                .addAsManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"))
                .addAsManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension");
    }

    private static final String PKG = MavenFacetsTest.class.getSimpleName().toLowerCase();
    private static List<File> tempFolders = new ArrayList<File>();

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private ResourceFactory resourceFactory;
    
    @Inject
    private Shell dumbShell;

    protected static Project project;

    @Before
    @SuppressWarnings("unchecked")
    public void before() throws IOException {
        if (project == null) {
            project = createProject(MavenCoreFacet.class, JavaSourceFacet.class, ResourceFacet.class, WebResourceFacet.class,
                    DependencyFacet.class, PackagingFacet.class);
        }
        
        dumbShell.registerBufferManager(new BufferManager()
        {
           @Override
           public void bufferOnlyMode()
           {

           }

           @Override
           public void directWriteMode()
           {
           }

           @Override
           public void flushBuffer()
           {
           }

           @Override
           public void write(final int b)
           {
           }

           @Override
           public void write(final byte b)
           {
           }

           @Override
           public void write(final byte[] b)
           {
           }

           @Override
           public void write(final byte[] b, final int offset, final int length)
           {
           }

           @Override
           public void write(final String s)
           {
           }

           @Override
           public void directWrite(final String s)
           {
           }

           @Override
           public void setBufferPosition(final int row, final int col)
           {
           }

           @Override
           public int getHeight()
           {
              return 0;
           }

           @Override
           public int getWidth()
           {
              return 0;
           }
        });
    }

    protected Project createProject(Class<? extends Facet>... facets) throws IOException {
        File tempFolder = File.createTempFile(PKG, null);
        tempFolder.delete();
        tempFolder.mkdirs();
        tempFolders.add(tempFolder);

        return projectFactory.createProject(ResourceUtil.getContextDirectory(resourceFactory.getResourceFrom(tempFolder)),
                facets);
    }

    @After
    public void after() {
        for (File tempFolder : tempFolders) {
            tempFolder.delete();
            // Hint the JVM that the file handle can be closed.
            tempFolder = null;
        }
        System.gc();
        project = null;
    }

    protected Project getProject() {
        return project;
    }

}
