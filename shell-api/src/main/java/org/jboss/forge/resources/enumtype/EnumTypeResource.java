/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.resources.enumtype;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceException;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.resources.ResourceHandles;

/**
 * @author Ricardo Martinelli
 */
@ResourceHandles("*.java")
public class EnumTypeResource extends FileResource<EnumTypeResource>
{

   @Inject
   public EnumTypeResource(final ResourceFactory factory)
   {
      super(factory, null);
   }

   public EnumTypeResource(final ResourceFactory factory, final File file)
   {
      super(factory, file);
      setFlag(ResourceFlag.ProjectSourceFile);
   }

   @Override
   public Resource<?> getChild(final String name)
   {
      List<Resource<?>> children = listResources();
      List<Resource<?>> subset = new ArrayList<Resource<?>>();

      for (Resource<?> child : children)
      {
         if ((name != null) && (child instanceof Member))
         {
            String childName = child.getName();
            if (((Member<?, ?>) child.getUnderlyingResourceObject()).getName().equals(name.trim())
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
   @SuppressWarnings("unchecked")
   public synchronized List<Resource<?>> listResources()
   {
      try
      {
         List<Resource<?>> list = new LinkedList<Resource<?>>();

         for (Member<?, ?> member : getEnumSource().getMembers())
         {
            if (member instanceof Field)
            {
               list.add(new EnumFieldResource(this, (Field<? extends JavaEnum>) member));
            }
            else if (member instanceof Method)
            {
               list.add(new EnumMethodResource(this, (Method<? extends JavaEnum>) member));
            }
            else
            {
               throw new UnsupportedOperationException("Unknown member type: " + member);
            }
         }
         return list;
      }
      catch (FileNotFoundException e)
      {
         return Collections.emptyList();
      }
   }

   public EnumTypeResource setContents(final JavaEnum source)
   {
      setContents(source.toString());
      return this;
   }

   public JavaEnum getEnumSource() throws FileNotFoundException
   {
      return (JavaEnum) JavaParser.parse(file);
   }

   @Override
   public EnumTypeResource createFrom(File file)
   {
      return new EnumTypeResource(resourceFactory, file);
   }

   @Override
   public String toString()
   {
      try
      {
         return getEnumSource().getQualifiedName();
      }
      catch (FileNotFoundException e)
      {
         throw new ResourceException(e);
      }
      catch (Exception e)
      {
         return getName();
      }
   }
}
