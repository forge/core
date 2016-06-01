/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.impl.mock.Career;
import org.jboss.forge.addon.ui.impl.mock.Gender;
import org.jboss.forge.addon.ui.input.mock.CareerInput;
import org.jboss.forge.addon.ui.input.mock.CareerInputImpl;
import org.jboss.forge.addon.ui.input.mock.Framework;
import org.jboss.forge.addon.ui.input.mock.FrameworkImpl;
import org.jboss.forge.addon.ui.input.mock.GenderInput;
import org.jboss.forge.addon.ui.input.mock.GenderInputImpl;
import org.jboss.forge.addon.ui.input.mock.TargetPackage;
import org.jboss.forge.addon.ui.input.mock.TargetPackageImpl;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class InputDecoratorTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addClasses(TargetPackage.class, TargetPackageImpl.class)
               .addClasses(Career.class, CareerInput.class, CareerInputImpl.class)
               .addClasses(Gender.class, GenderInput.class, GenderInputImpl.class)
               .addClasses(Framework.class, FrameworkImpl.class);

      return archive;
   }

   @Inject
   private TargetPackage targetPackage;

   @Inject
   private Framework framework;

   @Inject
   private CareerInput careerInput;

   @Inject
   private GenderInput genderInput;

   @Test
   public void testUIInputDecorator() throws Exception
   {
      Assert.assertEquals("targetPackage", targetPackage.getName());
      Assert.assertEquals(InputType.JAVA_PACKAGE_PICKER, targetPackage.getFacet(HintsFacet.class).getInputType());
   }

   @Test
   public void testUIInputManyDecorator() throws Exception
   {
      Assert.assertEquals("framework", framework.getName());
      Assert.assertThat(framework.getValue(), hasItems("Java EE", "Furnace"));
   }

   @Test
   public void testUISelectOneDecorator() throws Exception
   {
      Assert.assertEquals("gender", genderInput.getName());
      Assert.assertThat(genderInput.getValueChoices(), hasItems(Gender.values()));
   }

   @Test
   public void testUISelectManyDecorator() throws Exception
   {
      Assert.assertEquals("careers", careerInput.getName());
      Assert.assertThat(careerInput.isRequired(), is(true));
      Assert.assertThat(careerInput.getValueChoices(), hasItems(Career.values()));
   }
}
