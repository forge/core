/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.resources;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFacet;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.source.FieldHolderSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaFieldResourceImpl extends AbstractJavaMemberResource<Field<?>> implements
         JavaFieldResource
{
   private final Field<?> field;

   public JavaFieldResourceImpl(final ResourceFactory factory, final Resource<?> parent,
            final Field<?> field)
   {
      super(factory, parent, field);
      this.field = field;
   }

   @Override
   public Resource<Field<?>> createFrom(final Field<?> file)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public Field<?> getUnderlyingResourceObject()
   {
      return field;
   }

   @Override
   public String getName()
   {
      return field.getName() + "::" + field.getType().getQualifiedName();
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
      Object origin = field.getOrigin();
      if (origin instanceof FieldHolderSource)
      {
         ((FieldHolderSource) origin).removeField(field);
         if (!((FieldHolderSource) origin).hasField(field))
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
   public boolean supports(ResourceFacet type)
   {
      return false;
   }
}
