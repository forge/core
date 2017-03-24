/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.jboss.forge.addon.maven.util.MavenJDOMWriter;
import org.jboss.forge.addon.parser.xml.resources.AbstractXMLResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.util.Streams;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenModelResourceImpl extends AbstractXMLResource implements MavenModelResource
{
   private Model currentModel;

   public MavenModelResourceImpl(final ResourceFactory factory, final File file)
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
      Model model = getCurrentModel();
      List<Resource<?>> children = new ArrayList<>();
      listDependencies(model, children);
      listProfiles(model, children);
      listRepositories(model, children);
      return children;
   }

   private void listRepositories(Model model, List<Resource<?>> children)
   {
      List<Repository> repositories = model.getRepositories();
      for (Repository repository : repositories)
      {
         children.add(new MavenRepositoryResourceImpl(getResourceFactory(), getParent(), repository));
      }
   }

   private void listDependencies(Model model, List<Resource<?>> children)
   {
      for (Dependency dep : model.getDependencies())
      {
         children.add(new MavenDependencyResourceImpl(getResourceFactory(), this, dep));
      }
   }

   private void listProfiles(Model model, List<Resource<?>> children)
   {
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
      return currentModel.clone();
   }

   @Override
   public MavenModelResource setCurrentModel(final Model pom)
   {
      Document document;
      try (InputStream is = getResourceInputStream())
      {
         document = new SAXBuilder().build(is);
      }
      catch (JDOMException e)
      {
         throw new RuntimeException("Could not parse POM file: " + getFullyQualifiedName(), e);
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not read POM file: " + getFullyQualifiedName(), e);
      }
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (OutputStreamWriter os = new OutputStreamWriter(baos))
      {
         MavenJDOMWriter writer = new MavenJDOMWriter();
         writer.write(pom, document, "UTF-8", os);
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not write POM file: " + getFullyQualifiedName(), e);
      }
      try (OutputStream resourceOutputStream = getResourceOutputStream())
      {
         Streams.write(new ByteArrayInputStream(baos.toByteArray()), resourceOutputStream);
      }
      catch (IOException e)
      {
         throw new RuntimeException("Error while writing to resource stream: " + getFullyQualifiedName(), e);
      }

      return this;
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      return new MavenModelResourceImpl(getResourceFactory(), file);
   }

   private void initialize()
   {
      if (isStale() || currentModel == null)
      {
         try (InputStream stream = getResourceInputStream())
         {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            currentModel = reader.read(stream);
            currentModel.setPomFile(getUnderlyingResourceObject());

            // FORGE-2273: Making properties sortable
            SortedProperties sortedProps = new SortedProperties();
            sortedProps.putAll(currentModel.getProperties());
            currentModel.setProperties(sortedProps);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not parse POM file: " + getFullyQualifiedName(), e);
         }
         finally
         {
            refresh();
         }
      }
   }
}
