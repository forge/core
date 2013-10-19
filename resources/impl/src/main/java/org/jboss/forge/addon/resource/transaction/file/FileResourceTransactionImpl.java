/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.transaction.file;

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

import org.jboss.forge.addon.resource.FileResourceOperations;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.events.ResourceCreated;
import org.jboss.forge.addon.resource.events.ResourceDeleted;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.events.ResourceModified;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionException;
import org.jboss.forge.furnace.util.Assert;
import org.xadisk.additional.XAFileInputStreamWrapper;
import org.xadisk.additional.XAFileOutputStreamWrapper;
import org.xadisk.bridge.proxies.interfaces.Session;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileOutputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileSystem;
import org.xadisk.filesystem.FileSystemStateChangeEvent;
import org.xadisk.filesystem.NativeSession;
import org.xadisk.filesystem.exceptions.DirectoryNotEmptyException;
import org.xadisk.filesystem.exceptions.FileAlreadyExistsException;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.InsufficientPermissionOnFileException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

/**
 * Implementation of the {@link ResourceTransaction} interface for files
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class FileResourceTransactionImpl implements ResourceTransaction, FileResourceOperations
{
   private final XAFileSystem fileSystem;
   private final ResourceFactory resourceFactory;

   private volatile Session session;
   private int timeout;

   public FileResourceTransactionImpl(XAFileSystem fileSystem, ResourceFactory resourceFactory)
   {
      this.fileSystem = fileSystem;
      this.resourceFactory = resourceFactory;
   }

   @Override
   public void begin() throws ResourceTransactionException
   {
      this.session = fileSystem.createSessionForLocalTransaction();
      if (timeout != 0)
      {
         this.session.setTransactionTimeout(timeout);
      }
   }

   @Override
   public void commit() throws ResourceTransactionException
   {
      assertSessionCreated();
      try
      {
         session.commit();
      }
      catch (NoTransactionAssociatedException e)
      {
         throw new ResourceTransactionException(e);
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
         session.rollback();
      }
      catch (NoTransactionAssociatedException e)
      {
         throw new ResourceTransactionException(e);
      }
      finally
      {
         session = null;
      }
   }

   @Override
   public boolean isStarted()
   {
      return session != null;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Set<ResourceEvent> getChangeSet()
   {
      assertSessionCreated();
      Set<ResourceEvent> changes = new LinkedHashSet<ResourceEvent>();
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
      catch (Exception e)
      {
         throw new ResourceTransactionException(e);
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
      catch (Exception e)
      {
         throw new ResourceTransactionException(e);
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
         throw new ResourceTransactionException(e);
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
         throw new ResourceTransactionException(e);
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
         throw new ResourceTransactionException(e);
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
         throw new ResourceTransactionException(e);
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
         throw new ResourceTransactionException(e);
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
         throw new ResourceTransactionException(e);
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
         throw new ResourceTransactionException(e);
      }
   }

   @Override
   public boolean mkdirs(File file)
   {
      assertSessionCreated();
      try
      {
         // Must create the whole structure
         LinkedList<File> stack = new LinkedList<File>();
         File parent = file;
         while (parent != null)
         {
            try
            {
               if (session.fileExistsAndIsDirectory(parent))
               {
                  break;
               }
            }
            catch (InsufficientPermissionOnFileException ignore)
            {
               // This exception happens if there are nested directories not yet created.
               // Just ignore
            }
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
         throw new ResourceTransactionException(e);
      }
   }

   @Override
   public OutputStream createOutputStream(File f) throws IOException
   {
      assertSessionCreated();
      try
      {
         //This is the behavior of append = false in FileOutputStream
         session.truncateFile(f, 0L);
         XAFileOutputStream xaStream = session.createXAFileOutputStream(f, false);
         return new XAFileOutputStreamWrapper(xaStream);
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException(e);
      }
   }

   @Override
   public InputStream createInputStream(File f) throws IOException
   {
      assertSessionCreated();
      try
      {
         XAFileInputStream xaStream = session.createXAFileInputStream(f);
         return new XAFileInputStreamWrapper(xaStream);
      }
      catch (Exception e)
      {
         throw new ResourceTransactionException(e);
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
   public void deleteFileOnExit(File file)
   {
      file.deleteOnExit();
   }

   private void assertSessionCreated()
   {
      Assert.notNull(session, "Transaction is not started");
   }
}
