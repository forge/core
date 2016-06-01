/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.facets;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraintType;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Configures the project as an Addon Test project
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraints({
         @FacetConstraint({ JavaSourceFacet.class, DependencyFacet.class, DefaultFurnaceContainerFacet.class }),
         @FacetConstraint(value = { FurnaceVersionFacet.class }, type = FacetConstraintType.OPTIONAL)
})
public class AddonTestFacet extends AbstractFacet<Project>implements ProjectFacet
{
   public static Dependency FURNACE_TEST_HARNESS_DEPENDENCY = DependencyBuilder.create()
            .setGroupId("org.jboss.forge.furnace.test")
            .setArtifactId("furnace-test-harness")
            .setScopeType("test");
   public static Dependency FURNACE_TEST_ADAPTER_DEPENDENCY = DependencyBuilder.create()
            .setGroupId("org.jboss.forge.furnace.test")
            .setArtifactId("arquillian-furnace-classpath")
            .setScopeType("test");

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         String version = null;
         if (getFaceted().hasFacet(FurnaceVersionFacet.class))
         {
            version = FurnaceVersionFacet.VERSION_PROPERTY;
         }
         DependencyInstaller installer = SimpleContainer
                  .getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
         installer.install(getFaceted(), DependencyBuilder.create(FURNACE_TEST_HARNESS_DEPENDENCY).setVersion(version));
         installer.install(getFaceted(), DependencyBuilder.create(FURNACE_TEST_ADAPTER_DEPENDENCY).setVersion(version));
      }

      if (isInstalled())
      {
         Project project = getFaceted();
         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         String topLevelPackage = facet.getBasePackage();
         JavaClassSource testClass = Roaster.create(JavaClassSource.class).setPackage(topLevelPackage);
         testClass.setName("AbstractTestCase").setAbstract(true);
         facet.saveTestJavaSource(testClass.getEnclosingType());
         return true;
      }
      return false;
   }

   @Override
   public boolean isInstalled()
   {
      DependencyInstaller installer = SimpleContainer
               .getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
      return installer.isInstalled(origin, FURNACE_TEST_HARNESS_DEPENDENCY)
               && installer.isInstalled(origin, FURNACE_TEST_ADAPTER_DEPENDENCY);
   }

}