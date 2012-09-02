/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.project.services.ResourceFactory;

/**
 * Represents an URL
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class URLResource extends AbstractResource<URL>
{

   private URL url;

   public URLResource(final ResourceFactory factory, final URL url)
   {
      super(factory, null);
      this.url = url;
      setFlag(ResourceFlag.Leaf);
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getName()
   {
      return url.toString();
   }

   @Override
   public Resource<URL> createFrom(URL url)
   {
      return new URLResource(resourceFactory, url);
   }

   @Override
   public URL getUnderlyingResourceObject()
   {
      return url;
   }

   @Override
   public InputStream getResourceInputStream()
   {
      try
      {
         return url.openStream();
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not open stream", e);
      }
   }

   @Override
   public Resource<?> getChild(String name)
   {
      return null;
   }

   @Override
   public boolean exists()
   {
      HttpURLConnection connection;
      try
      {
         connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("HEAD");
         int responseCode = connection.getResponseCode();
         if (responseCode != 200)
         {
            return false;
         }
      }
      catch (IOException e)
      {
         return false;
      }
      return true;
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

}
