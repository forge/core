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
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.VirtualResource;
import org.jboss.forge.roaster.model.Member;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractJavaMemberResource<T extends Member> extends VirtualResource<T>
{
   private final T member;

   public AbstractJavaMemberResource(ResourceFactory factory, final Resource<?> parent, final T member)
   {
      super(factory, parent);
      this.member = member;
   }

   @Override
   public Resource<T> createFrom(final T file)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public T getUnderlyingResourceObject()
   {
      return member;
   }

   @Override
   public String getName()
   {
      return member.getName();
   }

   @Override
   public String toString()
   {
      return member.toString();
   }
}
