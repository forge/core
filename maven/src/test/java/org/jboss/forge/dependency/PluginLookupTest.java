/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.dependency;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.Root;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class PluginLookupTest
{
   @Deployment
   public static JavaArchive createTestArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar").addPackages(true, Root.class.getPackage())
               .addAsManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"))
               .addAsManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension")
               .addAsManifestResource(
                        "META-INF/services/org.jboss.forge.project.dependencies.DependencyResolverProvider");
   }

   @Inject
   private DependencyResolver resolver;

   @Test
   public void testResolveNonJarArtifact() throws Exception
   {
//      Dependency dep = DependencyBuilder.create("org.jboss.forge:forge-example-plugin:2.0.0-SNAPSHOT").setPackagingType("far");
//      DependencyRepository repo = new DependencyRepositoryImpl(KnownRepository.JBOSS_NEXUS);
//      List<DependencyResource> artifacts = resolver.resolveDependencies(dep, Arrays.asList(repo));
//      for (DependencyResource dependencyResource : artifacts)
//      {
//         if ("far".equals(dependencyResource.getDependency().getPackagingType()))
//         {
//            System.out.println(dependencyResource.getDependency().getScopeTypeEnum());
//            System.out.println("PLUGIN: "+dependencyResource);
//         }
//      }
   }
}
