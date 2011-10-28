/**
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
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
 **/
package org.jboss.forge.spec.javaee.jsf;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="http://community.jboss.org/people/bleathem">Brian Leathem</a>
 */
public enum FacesVersion {

   JSF_EE_2_1("JSF 2.0 for EE Environments (Provided)",
           Arrays.asList(
                   DependencyBuilder.create("javax.faces:jsf-api:2.1:provided")
           )
   ),
   JSF_EE_1_2("JSF 1.2 for EE Environments (Provided)",
           Arrays.asList(
                   DependencyBuilder.create("javax.faces:jsf-api:1.2_15:provided")
           )
   ),
   MOJARRA_SERVLET_2_1("Mojarra 2.0 (Bundled)",
           Arrays.asList(
                   DependencyBuilder.create("com.sun.faces:jsf-api:2.1.3_01"),
                   DependencyBuilder.create("com.sun.faces:jsf-impl:2.1.3_01")
           )
   ),
   MYFACES_2_1("MyFaces 2.1.x (Bundled)",
           Arrays.asList(
                   DependencyBuilder.create("org.apache.myfaces.core:myfaces-api:2.1.3"),
                   DependencyBuilder.create("org.apache.myfaces.core:myfaces-impl:2.1.3")
           )
   ),
   MYFACES_1_2("MyFaces 1.2.x (Bundled)",
           Arrays.asList(
                   DependencyBuilder.create("org.apache.myfaces.core:myfaces-api:1.2.10"),
                   DependencyBuilder.create("org.apache.myfaces.core:myfaces-impl:1.2.10")
           )
   );

   private String name;
   private List<? extends Dependency> dependencies;

   private FacesVersion(String name, List<? extends Dependency> dependencies)
   {
      this.name = name;
      this.dependencies = dependencies;
   }
    public String getName()
   {
      return name;
   }
    public List<? extends Dependency> getDependencies()
   {
      return dependencies;
   }

   @Override
   public String toString()
   {
     return name;
   }
}
