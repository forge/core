/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.jpa;

import static org.jboss.forge.spec.javaee.jpa.container.WebLogic12cContainer.HIBERNATE_TRANSACTION_JTA_PLATFORM;
import static org.jboss.forge.spec.javaee.jpa.container.WebLogic12cContainer.WEBLOGIC_JTA_PLATFORM;

import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.PersistenceMetaModelFacet;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class PersistencePluginTest extends AbstractJPATest
{

   @Test
   public void testNewEntity() throws Exception
   {
      Project project = getProject();

      queueInputLines("", "");
      getShell().execute(
               "persistence setup --provider HIBERNATE --container CUSTOM_JTA --jndiDataSource java:jboss:jta-ds ");

      PersistenceDescriptor config = project.getFacet(PersistenceFacet.class).getConfig();
      List<PersistenceUnitDef> units = config.listUnits();
      PersistenceUnitDef unit = units.get(0);

      Assert.assertEquals("java:jboss:jta-ds", unit.getJtaDataSource());
   }

   @Test
   public void testAS6DataSource() throws Exception
   {
      Project project = getProject();

      queueInputLines("", "");
      getShell().execute(
               "persistence setup --provider HIBERNATE --container JBOSS_AS7");

      PersistenceDescriptor config = project.getFacet(PersistenceFacet.class).getConfig();
      List<PersistenceUnitDef> units = config.listUnits();
      PersistenceUnitDef unit = units.get(0);

      Assert.assertEquals("java:jboss/datasources/ExampleDS", unit.getJtaDataSource());
   }

   @Test
   public void testWebLogic12cDataSource() throws Exception
   {
      Project project = getProject();

      queueInputLines("", "");
      getShell()
               .execute(
                        "persistence setup --provider HIBERNATE --container WEBLOGIC_12C --jndiDataSource jdbc/test/data/source ");

      PersistenceDescriptor config = project.getFacet(PersistenceFacet.class).getConfig();
      List<PersistenceUnitDef> units = config.listUnits();
      PersistenceUnitDef unit = units.get(0);

      Assert.assertEquals(5, unit.getProperties().size());

      Assert.assertEquals(HIBERNATE_TRANSACTION_JTA_PLATFORM, unit.getProperties().get(0).getName());
      Assert.assertEquals(WEBLOGIC_JTA_PLATFORM, unit.getProperties().get(0).getValue());

      Assert.assertEquals("hibernate.hbm2ddl.auto", unit.getProperties().get(1).getName());
      Assert.assertEquals("create-drop", unit.getProperties().get(1).getValue());

      Assert.assertEquals("hibernate.show_sql", unit.getProperties().get(2).getName());
      Assert.assertEquals("true", unit.getProperties().get(2).getValue());

      Assert.assertEquals("hibernate.format_sql", unit.getProperties().get(3).getName());
      Assert.assertEquals("true", unit.getProperties().get(3).getValue());

      Assert.assertEquals("hibernate.transaction.flush_before_completion", unit.getProperties().get(4).getName());
      Assert.assertEquals("true", unit.getProperties().get(4).getValue());

   }

   @Test
   public void testAS7DataSource() throws Exception
   {
      Project project = getProject();

      queueInputLines("", "");
      getShell().execute(
               "persistence setup --provider HIBERNATE --container JBOSS_AS7");

      PersistenceDescriptor config = project.getFacet(PersistenceFacet.class).getConfig();
      List<PersistenceUnitDef> units = config.listUnits();
      PersistenceUnitDef unit = units.get(0);

      Assert.assertEquals("java:jboss/datasources/ExampleDS", unit.getJtaDataSource());
   }

   @Test
   public void testHibernateProperties() throws Exception
   {
      Project project = getProject();

      queueInputLines("", "");
      getShell().execute(
               "persistence setup --provider HIBERNATE --container JBOSS_AS7");

      PersistenceDescriptor config = project.getFacet(PersistenceFacet.class).getConfig();
      List<PersistenceUnitDef> units = config.listUnits();
      PersistenceUnitDef unit = units.get(0);

      Assert.assertEquals("hibernate.hbm2ddl.auto", unit.getProperties().get(0).getName());
      Assert.assertEquals("create-drop", unit.getProperties().get(0).getValue());

      Assert.assertEquals("hibernate.show_sql", unit.getProperties().get(1).getName());
      Assert.assertEquals("true", unit.getProperties().get(1).getValue());

      Assert.assertEquals("hibernate.format_sql", unit.getProperties().get(2).getName());
      Assert.assertEquals("true", unit.getProperties().get(2).getValue());

      Assert.assertEquals("hibernate.transaction.flush_before_completion", unit.getProperties().get(3).getName());
      Assert.assertEquals("true", unit.getProperties().get(3).getValue());

      Assert.assertEquals(4, unit.getProperties().size());
   }

   @Test
   public void testMySQLDatabase() throws Exception
   {
      Project project = getProject();

      queueInputLines("", "");
      getShell().execute(
               "persistence setup --provider HIBERNATE --container JBOSS_AS7 --database MYSQL");

      PersistenceDescriptor config = project.getFacet(PersistenceFacet.class).getConfig();
      List<PersistenceUnitDef> units = config.listUnits();
      PersistenceUnitDef unit = units.get(0);

      Assert.assertEquals("hibernate.dialect", unit.getProperties().get(4).getName());
      Assert.assertEquals("org.hibernate.dialect.MySQLDialect", unit.getProperties().get(4).getValue());

      Assert.assertEquals(5, unit.getProperties().size());
   }

   @Test
   public void testMySQLDatabaseWithJndiDataSource() throws Exception
   {
      Project project = getProject();

      queueInputLines("", "");
      getShell()
               .execute(
                        "persistence setup --provider HIBERNATE --container JBOSS_AS6 --database MYSQL_INNODB --jndiDataSource java:demo");

      PersistenceDescriptor config = project.getFacet(PersistenceFacet.class).getConfig();

      List<PersistenceUnitDef> units = config.listUnits();
      PersistenceUnitDef unit = units.get(0);

      Assert.assertEquals("java:demo", unit.getJtaDataSource());
      Assert.assertEquals("hibernate.dialect", unit.getProperties().get(4).getName());
      Assert.assertEquals("org.hibernate.dialect.MySQLInnoDBDialect", unit.getProperties().get(4).getValue());

      Assert.assertEquals(5, unit.getProperties().size());
   }

   @Test
   public void testEclipseLinkWithMetaModel() throws Exception
   {
      Project project = getProject();

      queueInputLines("y", "", "");
      getShell().execute("persistence setup --provider ECLIPSELINK --container GLASSFISH_3");

      Assert.assertTrue(project.hasFacet(PersistenceMetaModelFacet.class));
      PersistenceMetaModelFacet facet = project.getFacet(PersistenceMetaModelFacet.class);
      Assert.assertTrue(facet.getCompilerArgs().contains("eclipselink.persistencexml"));
      Assert.assertEquals("org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", facet.getProcessor());
      Assert.assertEquals("eclipselink", facet.getProcessorDependency().getArtifactId());
      Assert.assertEquals(1, project.getFacet(MavenCoreFacet.class).getPOM().getPluginRepositories().size());
   }
}
