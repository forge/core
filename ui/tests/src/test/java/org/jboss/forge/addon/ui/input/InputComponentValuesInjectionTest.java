/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.impl.mock.Career;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InputComponentValuesInjectionTest
{
   @Deployment
   @AddonDeployments({ @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.addon:convert"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addClasses(Career.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:convert"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   InputComponentFactory inputFactory;

   @Inject
   ConverterFactory converterFactory;

   @Inject
   UISelectOne<Boolean> selectBoolean;

   @Test
   public void testSetValueInInputManyForIterable()
   {
      UIInputMany<Career> inputMany = inputFactory.createInputMany("inputMany", Career.class);
      Iterable<String> stringIterable = Arrays.asList("TECHNOLOGY", "MEDICINE");
      InputComponents.setValueFor(converterFactory, inputMany, stringIterable);
      Iterable<Career> value = inputMany.getValue();
      Assert.assertNotNull(value);
      Iterator<Career> iterator = value.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertSame(Career.TECHNOLOGY, iterator.next());
      Assert.assertTrue(iterator.hasNext());
      Assert.assertSame(Career.MEDICINE, iterator.next());
      Assert.assertFalse(iterator.hasNext());
   }

   @Test
   public void testSetValueInSelectManyForIterable()
   {
      UISelectMany<Career> selectMany = inputFactory.createSelectMany("inputMany", Career.class);
      selectMany.setValueChoices(Arrays.asList(Career.values()));
      Iterable<String> stringIterable = Arrays.asList("TECHNOLOGY", "MEDICINE");
      InputComponents.setValueFor(converterFactory, selectMany, stringIterable);
      Iterable<Career> value = selectMany.getValue();
      Assert.assertNotNull(value);
      Iterator<Career> iterator = value.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertSame(Career.TECHNOLOGY, iterator.next());
      Assert.assertTrue(iterator.hasNext());
      Assert.assertSame(Career.MEDICINE, iterator.next());
      Assert.assertFalse(iterator.hasNext());
   }

   @Test
   public void testSetValueInSelectManyForInterface()
   {
      UISelectMany<Version> versions = inputFactory.createSelectMany("versions", Version.class);
      List<Version> versionList = Arrays.<Version> asList(SingleVersion.valueOf("1.0.0"), SingleVersion.valueOf("2.0.0"));
      versions.setValueChoices(versionList);
      InputComponents.setValueFor(converterFactory, versions, Arrays.asList("1.0.0", "2.0.0"));
      Iterator<Version> value = versions.getValue().iterator();
      Assert.assertThat(value.hasNext(), is(true));
      Assert.assertThat(value.next(), is(sameInstance(versionList.get(0))));
      Assert.assertThat(value.hasNext(), is(true));
      Assert.assertThat(value.next(), is(sameInstance(versionList.get(1))));
      Assert.assertThat(value.hasNext(), is(false));
   }

   @Test
   public void testChoiceValuesInjection()
   {
      // Injected choice values are not null
      Assert.assertThat(selectBoolean.getValueChoices(), not(nullValue()));
      Assert.assertTrue(selectBoolean.getValueChoices().iterator().hasNext());

      // Manually created choice values are not null
      UISelectOne<Boolean> selectOne = inputFactory.createSelectOne("selectOne", Boolean.class);
      Assert.assertNotNull(selectOne.getValueChoices());
      Assert.assertTrue(selectOne.getValueChoices().iterator().hasNext());
   }

   @Test(expected = IllegalArgumentException.class)
   @Ignore("Assertions commented out")
   public void testSetInvalidValueInSelectOne()
   {
      UISelectOne<String> selectOne = inputFactory.createSelectOne("selectOne", String.class);
      selectOne.setValueChoices(Arrays.asList("A", "B", "C"));
      selectOne.setValue("D");
   }

   @Test(expected = IllegalArgumentException.class)
   @Ignore("Assertions commented out")
   public void testSetInvalidValueInSelectMany()
   {
      UISelectMany<String> selectMany = inputFactory.createSelectMany("selectMany", String.class);
      selectMany.setValueChoices(Arrays.asList("A", "B", "C"));
      selectMany.setValue(Arrays.asList("B", "D"));
   }
}