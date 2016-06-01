/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import static org.hamcrest.CoreMatchers.hasItems;

import java.lang.annotation.ElementType;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InputComponentFactoryTest
{
   @Deployment
   @AddonDeployments({ @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   InputComponentFactory factory;

   @Test(expected = IllegalArgumentException.class)
   public void testAssertNameNotNull() throws Exception
   {
      factory.createInput(null, String.class);
   }

   @Test
   public void testCreateUIInput() throws Exception
   {
      UIInput<String> input = factory.createInput("foo", String.class);
      Assert.assertNotNull(input);
      Assert.assertTrue(input.hasFacet(HintsFacet.class));
      Assert.assertEquals(InputComponents.DEFAULT_SHORT_NAME, input.getShortName());
   }

   @Test
   public void testCreateUIInputBooleanDefaultValueIsFalse() throws Exception
   {
      UIInput<Boolean> input = factory.createInput("foo", Boolean.class);
      Assert.assertNotNull(input);
      Assert.assertEquals(false, input.getValue());
   }

   @Test
   public void testCreateUIInputWithShortName() throws Exception
   {
      UIInput<String> input = factory.createInput("foo", 'f', String.class);
      Assert.assertNotNull(input);
      Assert.assertTrue(input.hasFacet(HintsFacet.class));
      Assert.assertEquals('f', input.getShortName());
   }

   @Test
   public void testCreateUIInputMany() throws Exception
   {
      UIInputMany<String> input = factory.createInputMany("foo", String.class);
      Assert.assertNotNull(input);
      Assert.assertTrue(input.hasFacet(HintsFacet.class));
      Assert.assertEquals(InputComponents.DEFAULT_SHORT_NAME, input.getShortName());
   }

   @Test
   public void testCreateUIInputManyWithShortName() throws Exception
   {
      UIInputMany<String> input = factory.createInputMany("foo", 'f', String.class);
      Assert.assertNotNull(input);
      Assert.assertTrue(input.hasFacet(HintsFacet.class));
      Assert.assertEquals('f', input.getShortName());
   }

   @Test
   public void testCreateUISelectMany() throws Exception
   {
      UISelectMany<String> input = factory.createSelectMany("foo", String.class);
      Assert.assertNotNull(input);
      Assert.assertTrue(input.hasFacet(HintsFacet.class));
      Assert.assertEquals(InputComponents.DEFAULT_SHORT_NAME, input.getShortName());
   }

   @Test
   public void testCreateUISelectManyWithShortName() throws Exception
   {
      UISelectMany<String> input = factory.createSelectMany("foo", 'f', String.class);
      Assert.assertNotNull(input);
      Assert.assertTrue(input.hasFacet(HintsFacet.class));
      Assert.assertEquals('f', input.getShortName());
   }

   @Test
   public void testCreateUISelectOne() throws Exception
   {
      UISelectOne<String> input = factory.createSelectOne("foo", String.class);
      Assert.assertNotNull(input);
      Assert.assertTrue(input.hasFacet(HintsFacet.class));
      Assert.assertEquals(InputComponents.DEFAULT_SHORT_NAME, input.getShortName());
   }

   @Test
   public void testCreateUISelectOneWithShortName() throws Exception
   {
      UISelectOne<String> input = factory.createSelectOne("foo", 'f', String.class);
      Assert.assertNotNull(input);
      Assert.assertTrue(input.hasFacet(HintsFacet.class));
      Assert.assertEquals('f', input.getShortName());
   }

   @Test
   public void testCreateUIInputDefaultValue() throws Exception
   {
      UIInput<String> input = factory.createInput("foo", 'f', String.class);
      Assert.assertNotNull(input);
      input.setDefaultValue((String) null);
      Assert.assertFalse(input.hasDefaultValue());
   }

   @Test
   public void testCreateUIInputManyDefaultValue() throws Exception
   {
      UIInputMany<String> input = factory.createInputMany("foo", 'f', String.class);
      Assert.assertNotNull(input);
      input.setDefaultValue((Iterable<String>) null);
      Assert.assertFalse(input.hasDefaultValue());
   }

   @Test
   public void testCreateUISelectManyDefaultValue() throws Exception
   {
      UISelectMany<String> input = factory.createSelectMany("foo", 'f', String.class);
      Assert.assertNotNull(input);
      input.setDefaultValue((Iterable<String>) null);
      Assert.assertFalse(input.hasDefaultValue());
   }

   @Test
   public void testCreateUISelectOneDefaultValue() throws Exception
   {
      UISelectOne<String> input = factory.createSelectOne("foo", 'f', String.class);
      Assert.assertNotNull(input);
      input.setDefaultValue((String) null);
      Assert.assertFalse(input.hasDefaultValue());
   }

   @Test
   public void testCreateUISelectOneWithValueChoicesByDefault() throws Exception
   {
      UISelectOne<ElementType> input = factory.createSelectOne("foo", 'f', ElementType.class);
      Assert.assertNotNull(input);
      Assert.assertThat(input.getValueChoices(), hasItems(ElementType.values()));
   }

}
