/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;
import org.jboss.forge.spec.javaee.CDIFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.api")
@RequiresFacet({ DependencyFacet.class, PackagingFacet.class, CDIFacet.class, ResourceFacet.class })
@RequiresPackagingType(PackagingType.JAR)
public class ForgeAPIFacet extends BaseFacet
{

   @Inject
   private Shell shell;

   @Inject
   private DependencyInstaller installer;

   @Override
   public boolean install()
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      List<Dependency> versions = deps.resolveAvailableVersions("org.jboss.forge:forge-shell-api:[,]");
      Dependency version = shell.promptChoiceTyped("Install which version of the Forge API?", versions, versions.get(versions.size() - 1));
      deps.setProperty("forge.api.version", version.getVersion());
      DependencyBuilder apiDep = DependencyBuilder.create("org.jboss.forge:forge-shell-api:${forge.api.version}")
               .setScopeType(ScopeType.PROVIDED);
      DependencyBuilder testDep = DependencyBuilder.create("org.jboss.forge:forge-test-harness:${forge.api.version}")
               .setScopeType(ScopeType.TEST);
      DependencyBuilder testShellDep = DependencyBuilder.create("org.jboss.forge:forge-shell:${forge.api.version}")
               .setScopeType(ScopeType.TEST);

      installer.install(project, apiDep);
      installer.install(project, testDep);
      installer.install(project, testShellDep);

      project.getFacet(ResourceFacet.class).createResource("<forge/>".toCharArray(), "META-INF/forge.xml");

      return true;
   }

   @Override
   public boolean isInstalled()
   {
      Dependency dep = DependencyBuilder.create("org.jboss.forge:forge-shell-api");
      PackagingType packagingType = project.getFacet(PackagingFacet.class).getPackagingType();
      return project.getFacet(DependencyFacet.class).hasEffectiveDependency(dep)
               && PackagingType.JAR.equals(packagingType);
   }
}
