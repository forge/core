/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources.java;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.parser.java.Member;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.resources.VirtualResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("rawtypes")
public abstract class JavaMemberResource<T extends Member> extends VirtualResource<T>
{
   private final T member;

   public JavaMemberResource(final Resource<?> parent, final T member)
   {
      super(parent);
      this.member = member;
      setFlag(ResourceFlag.Leaf);
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
