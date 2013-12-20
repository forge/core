/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.resources;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Vetoed;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.jboss.forge.addon.parser.xml.resources.XMLResourceImpl;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.util.Streams;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Vetoed
public class MavenPomResourceImpl extends XMLResourceImpl implements MavenPomResource
{
   private Model currentModel;

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
      List<Resource<?>> children = new ArrayList<>();
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
         children.add(new MavenDependencyResourceImpl(getResourceFactory(), this, dep));
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
         InputStream stream = null;
         try
         {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            stream = getResourceInputStream();
            currentModel = reader.read(stream);
            currentModel.setPomFile(getUnderlyingResourceObject());
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         finally
         {
            Streams.closeQuietly(stream);
         }
      }
   }
}
