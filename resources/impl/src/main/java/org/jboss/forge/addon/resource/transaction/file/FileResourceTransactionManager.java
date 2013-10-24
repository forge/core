/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.transaction.file;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.cdi.events.Local;
import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.xadisk.bridge.proxies.interfaces.XAFileSystem;
import org.xadisk.bridge.proxies.interfaces.XAFileSystemProxy;
import org.xadisk.filesystem.standalone.StandaloneFileSystemConfiguration;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class FileResourceTransactionManager
{
   private Logger logger = Logger.getLogger(getClass().getName());

   private XAFileSystem fileSystem;

   private FileResourceTransactionImpl transaction;

   public void startup(@Observes @Local PostStartup startup) throws Exception
   {
      File xaDiskHome = OperatingSystemUtils.createTempDir();
      StandaloneFileSystemConfiguration config = new StandaloneFileSystemConfiguration(
               xaDiskHome.getAbsolutePath(), "furnace-instance");
      config.setTransactionTimeout(600);
      // XADISK-95
      if (OperatingSystemUtils.isWindows())
      {
         config.setSynchronizeDirectoryChanges(Boolean.FALSE);
      }
      this.fileSystem = XAFileSystemProxy.bootNativeXAFileSystem(config);
      this.fileSystem.waitForBootup(10000);
   }

   public void shutdown(@Observes @Local PreShutdown shutdown)
   {
      if (fileSystem != null)
         try
         {
            fileSystem.shutdown();
         }
         catch (IOException e)
         {
            logger.log(Level.SEVERE, "Error while shutting down XAFileSystem", e);
         }
   }

   @Produces
   public FileResourceTransactionImpl getCurrentTransaction(ResourceFactory resourceFactory)
   {
      Assert.notNull(fileSystem, "FileSystem was not yet initialized. Is the Furnace container running?");
      if (transaction == null)
      {
         transaction = new FileResourceTransactionImpl(fileSystem, resourceFactory);
      }
      return transaction;
   }

}
