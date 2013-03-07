/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.resources;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.jboss.forge.resource.FileResource;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;

/**
 * Represents a 'pom.xml' {@link FileResource}.
 * <p>
 * May be used to retrieve and modify the underlying Maven {@link Model} and other information.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenPomResource extends FileResource<MavenPomResource>
{
   private Model currentModel;

   public MavenPomResource(final ResourceFactory factory)
   {
      super(factory, null);
   }

   public MavenPomResource(final ResourceFactory factory, final File file)
   {
      super(factory, file);
   }

   @Override
   public Resource<?> getChild(String name)
   {
      List<Resource<?>> chidren = listResources();

      for (Resource<?> child : chidren)
      {
         if (child.getName().trim().equals(name))
            return child;
      }

      return null;
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      List<Resource<?>> children = new ArrayList<Resource<?>>();
      listDependencies(children);
      listProfiles(children);
      listRepositories(children);
      return children;
   }

   private void listRepositories(List<Resource<?>> children)
   {
      List<Repository> repositories = getCurrentModel().getRepositories();
      for (Repository repository : repositories)
      {
         children.add(new MavenRepositoryResource(getResourceFactory(), parent, repository));
      }
   }

   private void listDependencies(List<Resource<?>> children)
   {
      Model model = getCurrentModel();
      for (Dependency dep : model.getDependencies())
      {
         children.add(new MavenDependencyResource(this, dep));
      }
   }

   private void listProfiles(List<Resource<?>> children)
   {
      Model model = getCurrentModel();
      List<Profile> profiles = model.getProfiles();
      for (Profile profile : profiles)
      {
         children.add(new MavenProfileResource(getResourceFactory(), this, profile));
      }
   }

   public Model getCurrentModel()
   {
      initialize();
      return currentModel;
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      return new MavenPomResource(resourceFactory, file);
   }

   private void initialize()
   {
      if (currentModel == null)
      {
         try
         {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            FileInputStream stream = new FileInputStream(getUnderlyingResourceObject());
            if (stream.available() > 0)
            {
               currentModel = reader.read(stream);
            }
            stream.close();

            currentModel.setPomFile(getUnderlyingResourceObject());
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   @Override
   public String toString()
   {
      return file.getName();
   }
}
