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
import java.util.Iterator;
import java.util.List;

import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.MethodHolder;
import org.jboss.forge.parser.java.Parameter;
import org.jboss.forge.resources.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("rawtypes")
public class JavaMethodResource extends JavaMemberResource<Method>
{
   private final Method<? extends JavaSource<?>> method;

   public JavaMethodResource(final Resource<?> parent, final Method<? extends JavaSource<?>> method)
   {
      super(parent, method);
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
      String params = "(";
      List<Parameter> parameters = method.getParameters();

      Iterator<Parameter> iterator = parameters.iterator();
      while (iterator.hasNext())
      {
         Parameter p = iterator.next();
         params += p.getType();

         if (iterator.hasNext())
         {
            params += ",";
         }
      }

      params += ")";

      String returnType = method.getReturnType() == null ? "void" : method.getReturnType();
      return method.getName() + params + "::" + returnType;
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
}
