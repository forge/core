/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.resources;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.resource.AbstractFileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.ResourceFacet;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.util.ResourceUtil;
import org.jboss.forge.roaster.ParserException;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.EnumConstant;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.JavaEnum;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.Member;
import org.jboss.forge.roaster.model.MemberHolder;
import org.jboss.forge.roaster.model.Method;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.util.FormatterProfileReader;
import org.jboss.forge.roaster.spi.Streams;

/**
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JavaResourceImpl extends AbstractFileResource<JavaResource> implements JavaResource
{
   private final String formatterProfilePath;
   private final String formatterProfileName;

   private JavaType<?> javaType;
   private byte[] lastDigest;

   public JavaResourceImpl(final ResourceFactory factory, final File file, String formatterProfileName,
            String formatterProfilePath)
   {
      super(factory, file);
      this.formatterProfileName = formatterProfileName;
      this.formatterProfilePath = formatterProfilePath;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends JavaType<?>> T getJavaType() throws FileNotFoundException
   {
      if (isStale())
      {
         refresh();
      }

      return (T) javaType;
   }

   @Override
   public boolean isStale()
   {
      if (javaType == null || super.isStale()
               || (lastDigest != null && !Arrays.equals(lastDigest, ResourceUtil.getDigest(this))))
      {
         return true;
      }
      return false;
   }

   @Override
   public void refresh()
   {
      super.refresh();
      lastDigest = ResourceUtil.getDigest(this);
      javaType = Roaster.parse(getResourceInputStream());
   }

   @Override
   public Resource<?> getChild(final String name)
   {
      List<Resource<?>> children = doListResources();
      List<Resource<?>> subset = new ArrayList<>();

      for (Resource<?> child : children)
      {
         if ((name != null) && (child instanceof AbstractJavaMemberResource<?>))
         {
            String childName = child.getName();
            if (((Member<?>) child.getUnderlyingResourceObject()).getName().equals(name.trim())
                     || childName.equals(name))
            {
               subset.add(child);
            }
         }
      }

      if (subset.size() == 1)
      {
         return subset.get(0);
      }
      else if (subset.size() > 1)
      {
         throw new ResourceException("Ambiguous name [" + name + "], full type signature required");
      }
      else
      {
         return null;
      }
   }

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   protected synchronized List<Resource<?>> doListResources()
   {
      try
      {
         List<Resource<?>> list = new LinkedList<>();
         JavaType<?> javaType = getJavaType();
         if (javaType instanceof MemberHolder)
         {
            MemberHolder<?> memberHolder = (MemberHolder<?>) javaType;
            for (Member<?> member : memberHolder.getMembers())
            {
               if (member instanceof Field)
               {
                  list.add(new JavaFieldResourceImpl(getResourceFactory(), this, (Field<?>) member));
               }
               else if (member instanceof Method)
               {
                  list.add(new JavaMethodResourceImpl(getResourceFactory(), this, (Method<?, ?>) member));
               }
               else
               {
                  throw new UnsupportedOperationException("Unknown member type: " + member);
               }
            }

            if (memberHolder instanceof JavaEnum)
            {
               List<EnumConstant<?>> enumConstants = ((JavaEnum) memberHolder).getEnumConstants();
               for (EnumConstant<?> e : enumConstants)
               {
                  list.add(new EnumConstantResourceImpl(getResourceFactory(), this, e));
               }
            }
         }
         return list;
      }
      catch (ParserException e)
      {
         return Collections.emptyList();
      }
      catch (FileNotFoundException e)
      {
         return Collections.emptyList();
      }
   }

   @Override
   public JavaResourceImpl setContents(final JavaSource<?> source)
   {
      setContents(source.toString());
      return this;
   }

   @Override
   public JavaResourceImpl createFrom(final File file)
   {
      return new JavaResourceImpl(getResourceFactory(), file, formatterProfileName, formatterProfilePath);
   }

   @Override
   public String toString()
   {
      try
      {
         return getJavaType().getQualifiedName();
      }
      catch (FileNotFoundException e)
      {
         throw new ResourceException("Error while fetching the qualified name", e);
      }
      catch (Exception e)
      {
         return getName();
      }
   }

   @Override
   public boolean supports(ResourceFacet type)
   {
      return false;
   }

   @Override
   public JavaResource setContents(final InputStream data)
   {
      if (formatterProfilePath != null)
      {
         try (FileInputStream fis = new FileInputStream(formatterProfilePath))
         {
            FormatterProfileReader reader = FormatterProfileReader.fromEclipseXml(fis);
            Properties formatterProfileProperties = reader.getProfileNames().contains(formatterProfileName) ? reader
                     .getPropertiesFor(formatterProfileName) : reader.getDefaultProperties();
            return setContents(data, formatterProfileProperties);
         }
         catch (IOException e)
         {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Error while reading formatter path", e);
         }
      }
      return super.setContents(data);
   }

   @Override
   public JavaResource setContents(InputStream data, Properties formatterProperties)
   {
      InputStream contents = data;
      if (formatterProperties != null)
      {
         String source = Streams.toString(data);
         String formattedSource = Roaster.format(formatterProperties, source);
         contents = new ByteArrayInputStream(formattedSource.getBytes());
      }
      return super.setContents(contents);
   }
}
