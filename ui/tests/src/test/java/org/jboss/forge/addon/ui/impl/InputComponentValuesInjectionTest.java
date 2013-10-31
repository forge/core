/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl;

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
import org.jboss.forge.addon.ui.InputComponentFactory;
import org.jboss.forge.addon.ui.domain.Career;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@SuppressWarnings({ "unchecked", "rawtypes" })
public class InputComponentValuesInjectionTest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:convert"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
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
      InputComponents.setValueFor(converterFactory, (InputComponent) inputMany, stringIterable);
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
      InputComponents.setValueFor(converterFactory, (InputComponent) selectMany, stringIterable);
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
      List<Version> versionList = Arrays.<Version> asList(new SingleVersion("1.0.0"), new SingleVersion("2.0.0"));
      versions.setValueChoices(versionList);
      InputComponents.setValueFor(converterFactory, (InputComponent) versions, Arrays.asList("1.0.0", "2.0.0"));
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

      // Manually created choice values are null
      UISelectOne<Boolean> selectOne = inputFactory.createSelectOne("selectOne", Boolean.class);
      Assert.assertThat(selectOne.getValueChoices(), nullValue());
   }
}