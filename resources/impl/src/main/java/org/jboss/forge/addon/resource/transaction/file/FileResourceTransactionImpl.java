/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.transaction.file;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.resource.FileOperations;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.events.ResourceCreated;
import org.jboss.forge.addon.resource.events.ResourceDeleted;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.events.ResourceModified;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionException;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionListener;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.xadisk.additional.XAFileInputStreamWrapper;
import org.xadisk.additional.XAFileOutputStreamWrapper;
import org.xadisk.bridge.proxies.interfaces.Session;
import org.xadisk.bridge.proxies.interfaces.XADiskBasicIOOperations.PermissionType;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileOutputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileSystem;
import org.xadisk.bridge.proxies.interfaces.XAFileSystemProxy;
import org.xadisk.filesystem.FileSystemStateChangeEvent;
import org.xadisk.filesystem.NativeSession;
import org.xadisk.filesystem.exceptions.DirectoryNotEmptyException;
import org.xadisk.filesystem.exceptions.FileAlreadyExistsException;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.InsufficientPermissionOnFileException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;
import org.xadisk.filesystem.standalone.StandaloneFileSystemConfiguration;

/**
 * Implementation of the {@link ResourceTransaction} interface for files
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FileResourceTransactionImpl implements ResourceTransaction, FileOperations, Closeable
{
   private static final Logger log = Logger.getLogger(FileResourceTransactionImpl.class.getName());

   private final FileResourceTransactionManager manager;
   private final ResourceFactory resourceFactory;

   private XAFileSystem fileSystem;

   private volatile Session session;
   private volatile boolean started;
   private int timeout = 0;

   public FileResourceTransactionImpl(FileResourceTransactionManager manager,
            ResourceFactory resourceFactory)
   {
      this.manager = manager;
      this.resourceFactory = resourceFactory;
   }

   @Override
   public void begin() throws ResourceTransactionException
   {
      this.session = getFileSystem().createSessionForLocalTransaction();
      this.started = true;
      if (timeout > 0)
      {
         this.session.setTransactionTimeout(timeout);
      }

      for (ResourceTransactionListener listener : manager.getTransactionListeners())
      {
         try
         {
            listener.transactionStarted(this);
         }
         catch (Exception e)
         {
            log.log(Level.SEVERE, "Error encountered while notifying ResourceTransactionListener: ["
                     + listener + "]", e);
         }
      }
   }

   @Override
   public void commit() throws ResourceTransactionException
   {
      assertSessionCreated();
      try
      {
         this.started = false;
         Set<ResourceEvent> changeSet = getChangeSet();
         session.commit();

         for (ResourceTransactionListener listener : manager.getTransactionListeners())
         {
            try
            {
               listener.transactionCommitted(this, changeSet);
            }
            catch (Exception e)
            {
               log.log(Level.SEVERE, "Error encountered while notifying ResourceTransactionListener: ["
                        + listener + "]", e);
            }
         }
      }
      catch (NoTransactionAssociatedException e)
      {
         throw new ResourceTransactionException("No transaction is associated", e);
      }
      finally
      {
         session = null;
      }
   }

   @Override
   public void rollback() throws ResourceTransactionException
   {
      assertSessionCreated();
      try
      {
         this.started = false;
         session.rollback();

         for (ResourceTransactionListener listener : manager.getTransactionListeners())
         {
            try
            {
               listener.transactionRolledBack(this);
            }
            catch (Exception e)
            {
               log.log(Level.SEVERE, "Error encountered while notifying ResourceTransactionListener: ["
                        + listener + "]", e);
            }
         }
      }
      catch (NoTransactionAssociatedException e)
      {
         throw new ResourceTransactionException("No transaction is associated", e);
      }
      finally
      {
         session = null;
      }
   }

   @Override
   public boolean isStarted()
   {
      return started;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Set<ResourceEvent> getChangeSet()
   {
      assertSessionCreated();
      Set<ResourceEvent> changes = new LinkedHashSet<>();
      try
      {
         // Using reflection, since the field is unavailable
         Field declaredField = NativeSession.class.getDeclaredField("fileStateChangeEventsToRaise");
         declaredField.setAccessible(true);
         List<FileSystemStateChangeEvent> events = (List<FileSystemStateChangeEvent>) declaredField.get(session);
         for (FileSystemStateChangeEvent changeEvent : events)
         {
            File file = changeEvent.getFile();
            Resource<File> resource = resourceFactory.create(file);
            switch (changeEvent.getEventType())
            {
            case CREATED:
               changes.add(new ResourceCreated(resource));
               break;
            case DELETED:
               changes.add(new ResourceDeleted(resource));
               break;
            case MODIFIED:
               changes.add(new ResourceModified(resource));
               break;
            }
         }
      }
      catch (Exception e)
      {
         // Do nothing
      }
      return Collections.unmodifiableSet(changes);
   }

   @Override
   public boolean fileExists(File f)
   {
      assertSessionCreated();
      try
      {
         return session.fileExists(f);
      }
      catch (InsufficientPermissionOnFileException pe)
      {
         if (pe.getMissingPermission() == PermissionType.READ_DIRECTORY && !pe.getPath().equals(f.getAbsolutePath()))
         {
            // see https://java.net/jira/browse/XADISK-120
            return false;
         }
         throw new ResourceTransactionException("Error while checking if file exists", pe);
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while checking if file exists", e);
      }
   }

   @Override
   public boolean fileExistsAndIsDirectory(File f)
   {
      assertSessionCreated();
      try
      {
         return session.fileExistsAndIsDirectory(f);
      }
      catch (InsufficientPermissionOnFileException pe)
      {
         if (pe.getMissingPermission() == PermissionType.READ_DIRECTORY && !pe.getPath().equals(f.getAbsolutePath()))
         {
            // see https://java.net/jira/browse/XADISK-120
            return false;
         }
         throw new ResourceTransactionException("Error while checking if file exists and is a directory", pe);
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while checking if file exists and is a directory", e);
      }
   }

   @Override
   public File[] listFiles(File file)
   {
      assertSessionCreated();
      try
      {
         String[] fileList = session.listFiles(file);
         File[] files = new File[fileList.length];
         for (int i = 0; i < fileList.length; i++)
         {
            files[i] = new File(file, fileList[i]);
         }
         return files;
      }
      catch (FileNotExistsException fne)
      {
         return new File[0];
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while listing files", e);
      }
   }

   @Override
   public long getFileLength(File f)
   {
      assertSessionCreated();
      try
      {
         return session.getFileLength(f);
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while getting file length", e);
      }
   }

   @Override
   public boolean renameFile(File src, File dest)
   {
      assertSessionCreated();
      try
      {
         session.moveFile(src, dest);
         return true;
      }
      catch (FileAlreadyExistsException fae)
      {
         return false;
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while renaming file from " + src + " to " + dest, e);
      }
   }

   @Override
   public void copyFile(File src, File dest) throws IOException
   {
      assertSessionCreated();
      try
      {
         session.copyFile(src, dest);
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while copying file from " + src + " to " + dest, e);
      }
   }

   @Override
   public boolean deleteFile(File f)
   {
      assertSessionCreated();
      try
      {
         session.deleteFile(f);
         return true;
      }
      catch (FileNotExistsException fne)
      {
         return false;
      }
      catch (DirectoryNotEmptyException dne)
      {
         return false;
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while deleting file " + f, e);
      }
   }

   @Override
   public boolean createNewFile(File file) throws IOException
   {
      assertSessionCreated();
      try
      {
         session.createFile(file, false);
         return true;
      }
      catch (FileAlreadyExistsException fae)
      {
         return false;
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while creating new file in " + file, e);
      }
   }

   @Override
   public boolean mkdir(File file)
   {
      assertSessionCreated();
      try
      {
         session.createFile(file, true);
         return true;
      }
      catch (FileAlreadyExistsException fae)
      {
         return false;
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while making directory " + file, e);
      }
   }

   @Override
   public boolean mkdirs(File file)
   {
      assertSessionCreated();
      try
      {
         // Must create the whole structure
         LinkedList<File> stack = new LinkedList<>();
         File parent = file;
         while (parent != null && !fileExistsAndIsDirectory(parent))
         {
            stack.push(parent);
            parent = parent.getParentFile();
         }
         while (!stack.isEmpty())
         {
            session.createFile(stack.pop(), true);
         }
         return true;
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while making directories " + file, e);
      }
   }

   @Override
   public OutputStream createOutputStream(File f) throws IOException
   {
      return createOutputStream(f, false);
   }

   @Override
   public OutputStream createOutputStream(File file, boolean append) throws IOException
   {
      assertSessionCreated();
      try
      {
         // This is the behavior of append = false in FileOutputStream
         if (!append)
            session.truncateFile(file, 0L);
         XAFileOutputStream xaStream = session.createXAFileOutputStream(file, false);
         return new XAFileOutputStreamWrapper(xaStream);
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while creating output stream for " + file, e);
      }
   }

   @Override
   public InputStream createInputStream(File f) throws IOException
   {
      assertSessionCreated();
      try
      {
         long fileLength = session.getFileLength(f);
         XAFileInputStream xaStream = session.createXAFileInputStream(f);
         XAFileInputStreamWrapper xaWrapper = new XAFileInputStreamWrapper(xaStream);
         return new AvailableInputStreamWrapper(xaWrapper, (int) fileLength);
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while creating input stream for " + f, e);
      }
   }

   @Override
   public File move(File src, File dest) throws IOException
   {
      assertSessionCreated();
      try
      {
         session.moveFile(src, dest);
         return dest;
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException("Error while moving file from " + src + " to " + dest, e);
      }
   }

   @Override
   public void setTransactionTimeout(int seconds)
   {
      if (seconds < 0)
      {
         throw new ResourceTransactionException("Timeout cannot be a negative value");
      }
      this.timeout = seconds;
   }

   @Override
   public int getTransactionTimeout()
   {
      return this.session == null ? timeout : this.session.getTransactionTimeout();
   }

   @Override
   public void deleteFileOnExit(File file)
   {
      file.deleteOnExit();
   }

   @Override
   public boolean deleteFile(File file, boolean recursive)
   {
      if (recursive)
      {
         return this.deleteRecursive(file);
      }
      return this.deleteFile(file);
   }

   private boolean deleteRecursive(File file)
   {
      if (OperatingSystemUtils.isWindows())
      {
         System.gc(); // ensure no lingering handles that would prevent deletion
      }

      if (file == null)
      {
         return false;
      }

      File[] children = this.listFiles(file);
      if (children != null)
      {
         for (File sf : children)
         {
            if (this.fileExistsAndIsDirectory(sf))
            {
               deleteFile(sf, false);
            }
            else
            {
               if (!this.deleteFile(sf))
               {
                  throw new RuntimeException("failed to delete: " + sf.getAbsolutePath());
               }
            }
         }
      }
      return this.deleteFile(file);
   }

   private void assertSessionCreated()
   {
      Assert.notNull(session, "Transaction is not started");
   }

   private XAFileSystem getFileSystem()
   {
      if (fileSystem == null)
      {
         File xaDiskHome = OperatingSystemUtils.createTempDir();
         StandaloneFileSystemConfiguration config = new StandaloneFileSystemConfiguration(
                  xaDiskHome.getAbsolutePath(), "furnace-instance");
         config.setTransactionTimeout(3600);
         // XADISK-95
         if (OperatingSystemUtils.isWindows())
         {
            config.setSynchronizeDirectoryChanges(Boolean.FALSE);
         }
         this.fileSystem = XAFileSystemProxy.bootNativeXAFileSystem(config);
         try
         {
            this.fileSystem.waitForBootup(10000);
         }
         catch (InterruptedException e)
         {
         }
      }
      return fileSystem;
   }

   @Override
   public void close() throws IOException
   {
      if (session != null)
      {
         try
         {
            session.rollback();
         }
         catch (NoTransactionAssociatedException e)
         {
            // Ignored
         }
      }
      if (fileSystem != null)
      {
         fileSystem.shutdown();
      }
   }
}
