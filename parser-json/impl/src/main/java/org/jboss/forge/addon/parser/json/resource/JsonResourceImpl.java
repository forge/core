/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.json.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonWriter;

import org.jboss.forge.addon.resource.AbstractFileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.ResourceFactory;

public class JsonResourceImpl extends AbstractFileResource<JsonResource> implements JsonResource
{
   public JsonResourceImpl(final ResourceFactory factory, final File file)
   {
      super(factory, file);
   }

   @Override
   public JsonStructure getJsonStructure()
   {
      try
      {
         try (JsonReader reader = Json.createReader(getResourceInputStream()))
         {
            return reader.read();
         }
      }
      catch (Exception e)
      {
         throw new ResourceException("Error while getting the Json structure", e);
      }
   }

   @Override
   public JsonArray getJsonArray()
   {
      try
      {
         try (JsonReader reader = Json.createReader(getResourceInputStream()))
         {
            return reader.readArray();
         }
      }
      catch (Exception e)
      {
         throw new ResourceException("Error while getting the Json array", e);
      }
   }

   @Override
   public JsonObject getJsonObject()
   {
      try
      {
         try (JsonReader reader = Json.createReader(getResourceInputStream()))
         {
            return reader.readObject();
         }
      }
      catch (Exception e)
      {
         throw new ResourceException("Error while getting the Json object", e);
      }
   }

   @Override
   public JsonResource setContents(InputStream data)
   {
      if (data == null)
         throw new IllegalArgumentException("InputStream must not be null.");
      try
      {
         try (JsonReader reader = Json.createReader(data))
         {
            setContents(reader.readObject());
         }
      }
      catch (Exception e)
      {
         throw new ResourceException("Error while setting the Json contents", e);
      }
      return this;
   }

   @Override
   public JsonResource setContents(JsonStructure structure)
   {
      if (structure == null)
         throw new IllegalArgumentException("JsonStructure must not be null.");
      try
      {
         if (!exists())
         {
            getParent().mkdirs();
            if (!createNewFile())
            {
               throw new IOException("Failed to create file: " + getUnderlyingResourceObject());
            }
         }

         try (OutputStream out = getFileOperations().createOutputStream(getUnderlyingResourceObject());
                  JsonWriter writer = Json.createWriter(out))
         {
            writer.write(structure);
         }
      }
      catch (IOException e)
      {
         throw new ResourceException("Error while setting the Json contents", e);
      }
      return this;
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      return new JsonResourceImpl(getResourceFactory(), file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }
}
