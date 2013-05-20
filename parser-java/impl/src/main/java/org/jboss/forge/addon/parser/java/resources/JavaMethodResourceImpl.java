/*
 * Copyright 2012-2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.parser.java.resources.JavaMethodResource;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFacet;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.MethodHolder;
import org.jboss.forge.parser.java.Parameter;
import org.jboss.forge.parser.java.util.Strings;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("rawtypes")
public class JavaMethodResourceImpl extends AbstractJavaMemberResource<Method> implements JavaMethodResource
{
   private final Method<? extends JavaSource<?>> method;

   public JavaMethodResourceImpl(final ResourceFactory factory, final Resource<?> parent,
            final Method<? extends JavaSource<?>> method)
   {
      super(factory, parent, method);
      this.method = method;
   }

   @Override
   public Resource<Method> createFrom(final Method file)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public Method<? extends JavaSource<?>> getUnderlyingResourceObject()
   {
      return method;
   }

   @Override
   public String getName()
   {
      List<String> parameterTypes = new ArrayList<String>();
      for (Parameter<?> p : method.getParameters())
      {
         parameterTypes.add(p.getType());
      }

      String returnType = method.getReturnType() == null ? "void" : method.getReturnType();
      return String.format("%s(%s)::%s", method.getName(), Strings.join(parameterTypes, ","), returnType);
   }

   @Override
   public String toString()
   {
      return method.toString();
   }

   @Override
   @SuppressWarnings({ "unchecked" })
   public boolean delete() throws UnsupportedOperationException
   {
      JavaSource<?> origin = method.getOrigin();
      if (origin instanceof MethodHolder)
      {
         ((MethodHolder) origin).removeMethod(method);
         if (!((MethodHolder) origin).hasMethodSignature(method))
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
