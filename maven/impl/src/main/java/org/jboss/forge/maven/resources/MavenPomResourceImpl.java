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
import org.jboss.forge.resource.AbstractFileResource;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenPomResourceImpl extends AbstractFileResource<MavenPomResource> implements MavenPomResource
{
   private Model currentModel;

   public MavenPomResourceImpl(final ResourceFactory factory)
   {
      super(factory, null);
   }

   public MavenPomResourceImpl(final ResourceFactory factory, final File file)
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
         children.add(new MavenRepositoryResourceImpl(getResourceFactory(), parent, repository));
      }
   }

   private void listDependencies(List<Resource<?>> children)
   {
      Model model = getCurrentModel();
      for (Dependency dep : model.getDependencies())
      {
         children.add(new MavenDependencyResourceImpl(this, dep));
      }
   }

   private void listProfiles(List<Resource<?>> children)
   {
      Model model = getCurrentModel();
      List<Profile> profiles = model.getProfiles();
      for (Profile profile : profiles)
      {
         children.add(new MavenProfileResourceImpl(getResourceFactory(), this, profile));
      }
   }

   @Override
   public Model getCurrentModel()
   {
      initialize();
      return currentModel;
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      return new MavenPomResourceImpl(resourceFactory, file);
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
