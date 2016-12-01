/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaEnum;
import org.jboss.forge.roaster.model.source.EnumConstantSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class JavaNewEnumConstCommandTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:parser-java"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven"),
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"));

      return archive;
   }

   @Inject
   private UITestHarness testHarness;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @SuppressWarnings("deprecation")
   @Test
   public void testCreateEnum() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaEnumSource javaEnum = Roaster.parse(JavaEnumSource.class,
               "package org.jboss.forge.test; public enum CreditCardType{}");
      facet.saveJavaSource(javaEnum);

      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaEnum.class)));

      CommandController controller = testHarness.createCommandController(JavaNewEnumConstantCommand.class,
               javaResource);
      controller.initialize();
      controller.setValueFor("arguments", Arrays.asList("VISA", "AMERICAN_EXPRESS", "MASTERCARD"));
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));

      javaResource = facet.getJavaResource("org.jboss.forge.test.CreditCardType");
      JavaEnumSource enumClass = javaResource.getJavaType();
      List<EnumConstantSource> enumConstants = enumClass.getEnumConstants();
      Assert.assertEquals(3, enumConstants.size());

      Assert.assertEquals("VISA", enumConstants.get(0).getName());
      Assert.assertEquals("AMERICAN_EXPRESS", enumConstants.get(1).getName());
      Assert.assertEquals("MASTERCARD", enumConstants.get(2).getName());
   }

}
