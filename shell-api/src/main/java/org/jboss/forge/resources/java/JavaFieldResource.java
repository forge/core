/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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

package org.jboss.forge.resources.java;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaFieldResource extends JavaMemberResource<Field<? extends JavaSource<?>>>
{
   private final Field<? extends JavaSource<?>> field;

   public JavaFieldResource(final JavaResource parent, final Field<? extends JavaSource<?>> field)
   {
      super(parent, field);
      this.field = field;

      setFlag(ResourceFlag.Leaf);
   }

   @Override
   public Resource<Field<? extends JavaSource<?>>> createFrom(final Field<? extends JavaSource<?>> file)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public Field<? extends JavaSource<?>> getUnderlyingResourceObject()
   {
      return field;
   }

   @Override
   public String getName()
   {
      return field.getName() + "::" + field.getType();
   }

   @Override
   public String toString()
   {
      return field.toString();
   }

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public boolean delete() throws UnsupportedOperationException
   {
      JavaSource<?> origin = field.getOrigin();
      if (origin instanceof FieldHolder)
      {
         ((FieldHolder) origin).removeField(field);
         if (!((FieldHolder) origin).hasField(field))
         {
            ((JavaResource) this.getParent()).setContents(origin.toString());
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean delete(final boolean recursive) throws UnsupportedOperationException
   {
      return delete();
   }
}
