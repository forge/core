package org.jboss.forge.arquillian.util;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

public final class ShrinkWrapUtil
{
   private ShrinkWrapUtil()
   {
   }

   /**
    * Export an {@link Archive} to a {@link File}
    * 
    * @param archive Archive to export
    */
   public static void toFile(File target, final Archive<?> archive)
   {
      try
      {
         archive.as(ZipExporter.class).exportTo(target, true);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not export deployment to file [" + target.getAbsolutePath() + "]", e);
      }
   }

   /**
    * Creates a tmp folder and exports the file. Returns the URL for that file location.
    * 
    * @param archive Archive to export
    * @return
    */
   public static URL toURL(final Archive<?> archive)
   {
      // create a random named temp file, then delete and use it as a directory
      try
      {
         File root = File.createTempFile("arquillian", archive.getName());
         root.delete();
         root.mkdirs();

         File deployment = new File(root, archive.getName());
         deployment.deleteOnExit();
         archive.as(ZipExporter.class).exportTo(deployment, true);
         return deployment.toURI().toURL();
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not export deployment to temp", e);
      }
   }

   public static URL toURL(final Descriptor descriptor)
   {
      // create a random named temp file, then delete and use it as a directory
      try
      {
         File root = File.createTempFile("arquillian", descriptor.getDescriptorName());
         root.delete();
         root.mkdirs();

         File deployment = new File(root, descriptor.getDescriptorName());
         deployment.deleteOnExit();

         FileOutputStream stream = new FileOutputStream(deployment);
         try
         {
            descriptor.exportTo(stream);
         }
         finally
         {
            try
            {
               stream.close();
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
         }

         return deployment.toURI().toURL();
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not export deployment to temp", e);
      }
   }

}