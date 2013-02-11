/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.parser.resources;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.facets.Facet;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaFieldResource extends JavaMemberResource<Field<? extends JavaSource<?>>>
{
   private final Field<? extends JavaSource<?>> field;

   public JavaFieldResource(final ResourceFactory factory, final JavaResource parent,
            final Field<? extends JavaSource<?>> field)
   {
      super(factory, parent, field);
      this.field = field;
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

   @Override
   public boolean supports(Class<? extends Facet<?>> type)
   {
      return false;
   }
}
