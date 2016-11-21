/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.maven.model.building.ModelSource2;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.util.Assert;

/**
 * Wraps a {@link FileResource} as a model source.
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class FileResourceModelSource implements ModelSource2
{

   private final FileResource<?> fileResource;

   public FileResourceModelSource(FileResource<?> fileResource)
   {
      Assert.notNull(fileResource, "POM Resource may not be null");
      this.fileResource = fileResource;
   }

   @Override
   public InputStream getInputStream() throws IOException
   {
      return fileResource.getResourceInputStream();
   }

   @Override
   public String getLocation()
   {
      return fileResource.getUnderlyingResourceObject().getPath();
   }

   @Override
   public ModelSource2 getRelatedSource(String relPath)
   {
      relPath = relPath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
      FileResource<?> relatedPom = fileResource.getParent().getChild(relPath).reify(FileResource.class);
      if (relatedPom != null && relatedPom.isDirectory())
      {
         // TODO figure out how to reuse ModelLocator.locatePom(File) here
         relatedPom = relatedPom.getChild("pom.xml").reify(FileResource.class);
      }

      if (relatedPom != null && relatedPom.exists())
      {
         return new FileResourceModelSource(relatedPom);
      }

      return null;
   }

   @Override
   public URI getLocationURI()
   {
      return fileResource.getUnderlyingResourceObject().toURI();
   }

}
