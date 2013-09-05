/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.faces;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.faces.FacesFacet_2_1;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class FacesFacetImpl_2_1 extends AbstractFacesFacetImpl implements FacesFacet_2_1
{
   public FacesFacetImpl_2_1(DependencyInstaller installer)
   {
      super(installer);
   }

   public static final Dependency JAVAEE6_FACES_21 = DependencyBuilder
            .create("org.jboss.spec.javax.faces:jboss-jsf-api_2.1_spec").setScopeType("provided");

   @Override
   public Version getSpecVersion()
   {
      return new SingleVersion("2.1");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new LinkedHashMap<Dependency, List<Dependency>>();
      result.put(JAVAEE6_FACES_21, Arrays.asList(JAVAEE6_FACES_21));
      return result;
   }

}
