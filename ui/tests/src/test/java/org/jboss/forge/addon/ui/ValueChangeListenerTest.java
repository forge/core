/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.input.ValueChangeListener;
import org.jboss.forge.addon.ui.input.events.ValueChangeEvent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ValueChangeListenerTest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private UIInput<String> one;

   @Inject
   private UIInputMany<String> many;

   @Inject
   private UISelectOne<String> selectOne;

   @Inject
   private UISelectMany<String> selectMany;

   @Test
   public void testValueChangeUIInput() throws Exception
   {
      one.setDefaultValue("Default Value");

      List<ValueChangeEvent> valueEventList = new ArrayList<>();
      one.addValueChangeListener(new CollectValueChangeListener(valueEventList));
      one.setValue("Value 1");
      one.setValue("Value 2");
      one.setValue("Value 3");
      one.setValue("Value 3");

      Assert.assertEquals(3, valueEventList.size());
      assertExpectedValues("Default Value", "Value 1", valueEventList.get(0));
      assertExpectedValues("Value 1", "Value 2", valueEventList.get(1));
      assertExpectedValues("Value 2", "Value 3", valueEventList.get(2));
   }

   @Test
   public void testValueChangeUIInputMany() throws Exception
   {
      many.setDefaultValue(Arrays.asList("Default Value"));

      List<ValueChangeEvent> valueEventList = new ArrayList<>();
      many.addValueChangeListener(new CollectValueChangeListener(valueEventList));
      many.setValue(Arrays.asList("Value 1"));
      many.setValue(Arrays.asList("Value 2"));
      many.setValue(Arrays.asList("Value 3"));
      many.setValue(Arrays.asList("Value 3"));

      Assert.assertEquals(3, valueEventList.size());
      assertExpectedValues(Arrays.asList("Default Value"), Arrays.asList("Value 1"), valueEventList.get(0));
      assertExpectedValues(Arrays.asList("Value 1"), Arrays.asList("Value 2"), valueEventList.get(1));
      assertExpectedValues(Arrays.asList("Value 2"), Arrays.asList("Value 3"), valueEventList.get(2));
   }

   @Test
   public void testValueChangeUISelectOne() throws Exception
   {
      selectOne.setValueChoices(Arrays.asList("Default Value", "Value 1", "Value 2", "Value 3"));
      selectOne.setDefaultValue("Default Value");

      List<ValueChangeEvent> valueEventList = new ArrayList<>();
      selectOne.addValueChangeListener(new CollectValueChangeListener(valueEventList));
      selectOne.setValue("Value 1");
      selectOne.setValue("Value 2");
      selectOne.setValue("Value 3");
      selectOne.setValue("Value 3");

      Assert.assertEquals(3, valueEventList.size());
      assertExpectedValues("Default Value", "Value 1", valueEventList.get(0));
      assertExpectedValues("Value 1", "Value 2", valueEventList.get(1));
      assertExpectedValues("Value 2", "Value 3", valueEventList.get(2));
   }

   @Test
   public void testValueChangeUISelectMany() throws Exception
   {
      selectMany.setValueChoices(Arrays.asList("Default Value", "Value 1", "Value 2", "Value 3"));

      selectMany.setDefaultValue(Arrays.asList("Default Value"));

      List<ValueChangeEvent> valueEventList = new ArrayList<>();
      selectMany.addValueChangeListener(new CollectValueChangeListener(valueEventList));
      selectMany.setValue(Arrays.asList("Value 1"));
      selectMany.setValue(Arrays.asList("Value 2"));
      selectMany.setValue(Arrays.asList("Value 3"));
      selectMany.setValue(Arrays.asList("Value 3"));

      Assert.assertEquals(3, valueEventList.size());
      assertExpectedValues(Arrays.asList("Default Value"), Arrays.asList("Value 1"), valueEventList.get(0));
      assertExpectedValues(Arrays.asList("Value 1"), Arrays.asList("Value 2"), valueEventList.get(1));
      assertExpectedValues(Arrays.asList("Value 2"), Arrays.asList("Value 3"), valueEventList.get(2));
   }

   private void assertExpectedValues(Iterable<?> oldValue, Iterable<?> newValue, ValueChangeEvent evt)
   {
      Assert.assertThat(evt.getOldValue(), is(instanceOf(Iterable.class)));
      Assert.assertThat(evt.getNewValue(), is(instanceOf(Iterable.class)));
      Assert.assertTrue("Old value does not match",
               InputComponents.areElementsEqual(oldValue, (Iterable<?>) evt.getOldValue()));
      Assert.assertTrue("Old value does not match",
               InputComponents.areElementsEqual(newValue, (Iterable<?>) evt.getNewValue()));

   }

   private void assertExpectedValues(Object oldValue, Object newValue, ValueChangeEvent evt)
   {
      Assert.assertEquals(oldValue, evt.getOldValue());
      Assert.assertEquals(newValue, evt.getNewValue());
   }

   private static class CollectValueChangeListener implements ValueChangeListener
   {

      private final List<ValueChangeEvent> events;

      public CollectValueChangeListener(List<ValueChangeEvent> events)
      {
         this.events = events;
      }

      @Override
      public void valueChanged(ValueChangeEvent event)
      {
         events.add(event);
      }
   }
}
