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
package org.jboss.forge.pluginloader;

import java.io.IOException;
import java.util.Collection;

import org.jboss.modules.ClassSpec;
import org.jboss.modules.PackageSpec;
import org.jboss.modules.Resource;
import org.jboss.modules.ResourceLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class LocalResourceLoader implements ResourceLoader
{

   @Override
   public String getRootName()
   {
      return "org.jboss.forge";
   }

   @Override
   public ClassSpec getClassSpec(final String fileName) throws IOException
   {
      return null;
   }

   @Override
   public PackageSpec getPackageSpec(final String name) throws IOException
   {
      return null;
   }

   @Override
   public Resource getResource(final String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getLibrary(final String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Collection<String> getPaths()
   {
      // TODO Auto-generated method stub
      return null;
   }

}
