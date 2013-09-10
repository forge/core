/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.monitor;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.events.ResourceCreated;
import org.jboss.forge.addon.resource.events.ResourceDeleted;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.events.ResourceModified;

/**
 * Adapts a {@link ResourceListener} to a {@link FileAlterationListener}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("unchecked")
public class FileAlterationListenerAdapter extends FileAlterationListenerAdaptor
{
   private final ResourceFactory resourceFactory;
   private final ResourceListener listener;

   public FileAlterationListenerAdapter(ResourceFactory resourceFactory, ResourceListener listener)
   {
      super();
      this.resourceFactory = resourceFactory;
      this.listener = listener;
   }

   @Override
   public void onDirectoryChange(File directory)
   {
      DirectoryResource dir = resourceFactory.create(DirectoryResource.class, directory);
      fireEvent(new ResourceModified(dir));
   }

   @Override
   public void onDirectoryCreate(File directory)
   {
      DirectoryResource dir = resourceFactory.create(DirectoryResource.class, directory);
      fireEvent(new ResourceCreated(dir));
   }

   @Override
   public void onDirectoryDelete(File directory)
   {
      DirectoryResource dir = resourceFactory.create(DirectoryResource.class, directory);
      fireEvent(new ResourceDeleted(dir));
   }

   @Override
   public void onFileChange(File file)
   {
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      fireEvent(new ResourceModified(fileResource));
   }

   @Override
   public void onFileCreate(File file)
   {
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      fireEvent(new ResourceCreated(fileResource));
   }

   @Override
   public void onFileDelete(File file)
   {
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      fireEvent(new ResourceDeleted(fileResource));
   }

   private void fireEvent(ResourceEvent event)
   {
      listener.processEvent(event);
   }
}
