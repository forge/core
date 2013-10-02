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

import org.jboss.forge.addon.resource.FileResourceOperations;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionException;
import org.jboss.forge.furnace.util.Assert;
import org.xadisk.additional.XAFileInputStreamWrapper;
import org.xadisk.additional.XAFileOutputStreamWrapper;
import org.xadisk.bridge.proxies.interfaces.Session;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileOutputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileSystem;
import org.xadisk.filesystem.exceptions.DirectoryNotEmptyException;
import org.xadisk.filesystem.exceptions.FileAlreadyExistsException;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

/**
 * Implementation of the {@link ResourceTransaction} interface for files
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class FileResourceTransactionImpl implements ResourceTransaction, FileResourceOperations
{
   private final XAFileSystem fileSystem;

   private volatile Session session;

   public FileResourceTransactionImpl(XAFileSystem fileSystem)
   {
      this.fileSystem = fileSystem;
   }

   @Override
   public void begin() throws ResourceTransactionException
   {
      this.session = fileSystem.createSessionForLocalTransaction();
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
   public void moveFile(File src, File dest) throws IOException
   {
      assertSessionCreated();
      try
      {
         session.moveFile(src, dest);
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
         session.createFile(file.getParentFile(), true);
         return true;
      }
      catch (FileAlreadyExistsException fae)
      {
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
   public void deleteFileOnExit(File file)
   {
      file.deleteOnExit();
   }

   private void assertSessionCreated()
   {
      Assert.notNull(session, "Transaction is not started");
   }

}
