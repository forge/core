/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetIsAmbiguousException;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategy;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategyFactory;
import org.jboss.forge.addon.javaee.rest.config.RestWebXmlConfigurationStrategy;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.xml.resources.XMLResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class RestFacetTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Test(expected = FacetIsAmbiguousException.class)
   public void testCannotInstallAmbiguousFacetType() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      facetFactory.install(project, RestFacet.class);
   }

   @Test
   public void testFacetAnnotationConfiguration()
   {
      Project project = projectFactory.createTempProject();
      Assert.assertFalse(project.hasFacet(JavaSourceFacet.class));
      String path = "/rest";
      JavaClassSource javaClass = Roaster.create(JavaClassSource.class).setName("ApplicationRestConfig")
               .setPackage("com.test.foo");

      RestFacet_1_1 facet = facetFactory.install(project, RestFacet_1_1.class);
      Assert.assertNotNull(facet);
      RestConfigurationStrategy config = RestConfigurationStrategyFactory.createUsingJavaClass(path, javaClass);
      facet.setConfigurationStrategy(config);
      Assert.assertTrue(project.hasFacet(JavaSourceFacet.class));
      Assert.assertSame(config, facet.getConfigurationStrategy());
      Assert.assertEquals(path, facet.getApplicationPath());
   }

   @Test
   public void testFacetXMLConfiguration() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Assert.assertFalse(project.hasFacet(ServletFacet_3_0.class));
      String path = "/rest";
      RestFacet_1_1 facet = facetFactory.install(project, RestFacet_1_1.class);
      Assert.assertNotNull(facet);
      RestConfigurationStrategy config = RestConfigurationStrategyFactory.createUsingWebXml(path);
      facet.setConfigurationStrategy(config);
      Assert.assertTrue(project.hasFacet(ServletFacet_3_0.class));
      Assert.assertSame(config, facet.getConfigurationStrategy());
      Assert.assertEquals("/rest", facet.getApplicationPath());

      XMLResource webConfig = project.getFacet(ServletFacet_3_0.class).getConfigFile().reify(XMLResource.class);
      Assert.assertNotNull(webConfig);
      Node xmlSource = webConfig.getXmlSource();
      Assert.assertNotNull(xmlSource);
      Node servletClass = xmlSource.getSingle("servlet/servlet-name=" + RestWebXmlConfigurationStrategy.JAXRS_SERVLET);
      Node servletMapping = xmlSource.getSingle("servlet-mapping/servlet-name="
               + RestWebXmlConfigurationStrategy.JAXRS_SERVLET);
      Assert.assertNotNull(servletClass);
      Assert.assertNotNull(servletMapping);
      Node urlPatternTag = servletMapping.getParent().getSingle("url-pattern");
      Assert.assertNotNull(urlPatternTag);
      Assert.assertEquals(path, urlPatternTag.getText());
   }
}
