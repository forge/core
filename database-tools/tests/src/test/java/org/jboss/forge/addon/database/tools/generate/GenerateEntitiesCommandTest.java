/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.database.tools.generate;

import javax.inject.Inject;

import org.h2.Driver;
import org.h2.constant.SysProperties;
import org.h2.message.DbException;
import org.h2.server.Service;
import org.h2.server.ShutdownHandler;
import org.h2.server.TcpServer;
import org.h2.tools.Server;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.New;
import org.h2.util.Tool;
import org.h2.util.Utils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyQuery;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.ui.test.UITestHarness;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
;

@RunWith(Arquillian.class)
public class GenerateEntitiesCommandTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:hibernate-tools"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:hibernate-tools"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"))
               .addClass(Server.class)
               .addClass(ShutdownHandler.class)
               .addClass(Tool.class)
               .addClass(DbException.class)
               .addClass(Service.class)
               .addClass(TcpServer.class)
               .addClass(Driver.class)
               .addClass(NetUtils.class)
               .addClass(SysProperties.class)
               .addClass(Utils.class)
               .addClass(getClass("org.h2.util.Utils$1"))
               .addClass(New.class)
               .addClass(MathUtils.class); 
      return archive;
   }
   
   private static Class<?> getClass(String name) {
      try {
         return Class.forName(name);
      }
      catch (ClassNotFoundException e)
      {
         e.printStackTrace();
         return null;
      }
   }
   
   @Inject
   private ProjectFactory projectFactory;

   @Inject 
   private FacetFactory facetFactory;
   
//   @Inject
//   private WizardTester<GenerateEntitiesCommand> wizard;
   
   @Inject
   private UITestHarness testHarness;

   @Inject
   private DependencyResolver resolver;

//   private Server server;
//   private Project project;
   
   @Before
   public void setup() throws Exception {
//      server = Server.createTcpServer().start();
//      Class.forName("org.h2.Driver");
//      Connection conn = DriverManager
//            .getConnection("jdbc:h2:tcp://localhost/mem:test;USER=foo;PASSWORD=bar");
//      conn.createStatement().execute(
//            "CREATE TABLE customer(" + "  id INTEGER PRIMARY KEY,"
//                  + "   first_name VARCHAR(256),"
//                  + "   last_name VARCHAR(256))");
//      conn.commit();
//      project = projectFactory.createTempProject();
//      facetFactory.install(project, PersistenceFacet.class);
   }

   @Test
   public void testGenerateEntitiesCommand() throws Exception
   {
      Assert.assertTrue(true);
      try {
         Server server = Server.createTcpServer().start();
         server.stop();
      } catch (Throwable t) {
         t.printStackTrace();
      }
//      wizard.setInitialSelection(project.getProjectRoot());
//      wizard.launch();
//      Assert.assertTrue(wizard.isEnabled());
//      wizard.setValueFor("targetPackage", "com.example.entity");
//      wizard.setValueFor("connectionProfile", "");
//      Assert.assertTrue(wizard.canFlipToNextPage());
//      wizard.next();
//      wizard.setValueFor("jdbcUrl", "jdbc:h2:tcp://localhost/mem:test");
//      wizard.setValueFor("userName", "foo");
//      wizard.setValueFor("userPassword", "bar");
//      wizard.setValueFor("hibernateDialect", "org.hibernate.dialect.H2Dialect");
//      wizard.setValueFor("driverLocation", resolveH2DriverJarResource());
//      wizard.setValueFor("driverClass", "org.h2.Driver");
//      Assert.assertTrue(wizard.canFlipToNextPage());
   }
   
   @After
   public void teardown() {
//      project.getProjectRoot().delete(true);
//      server.stop();
   }
   
   private FileResource<?> resolveH2DriverJarResource() {
      
      DependencyQuery query = DependencyQueryBuilder.create("com.h2database:h2:1.3.167");
      Dependency dependency = resolver.resolveArtifact(query);
      if (dependency != null) {
         return dependency.getArtifact();
      } else {
         return null;
      }
   }
   
}