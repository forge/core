/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class InputComponentsTest
{
   @Inject
   private UIInputMany<File> files;

   @Inject
   private UISelectOne<String> values;

   @Inject
   private UISelectMany<String> manyValues;

   @Inject
   private UIInputMany<Path> paths;

   @Inject
   private UIInput<Integer> integerInput;

   @Inject
   private ConverterFactory converterFactory;

   @Test
   public void testUISelectOneShouldNotSetValueNotContainedInValueChoices()
   {
      values.setValueChoices(Arrays.asList("A", "B", "C"));
      InputComponents.setValueFor(converterFactory, values, "D");
      Assert.assertNull(values.getValue());
   }

   @Test
   public void testUIInputManyFileSingleList()
   {
      Assert.assertThat(InputComponents.hasValue(files), is(false));
      InputComponents.setValueFor(converterFactory, files, OperatingSystemUtils.getTempDirectory());
      Assert.assertThat(InputComponents.hasValue(files), is(true));
      List<File> list = Lists.toList(files.getValue());
      Assert.assertThat(list.size(), equalTo(1));
      Assert.assertThat(list, hasItem(OperatingSystemUtils.getTempDirectory()));
   }

   @Test
   public void testUIInputManyPathSingleList()
   {
      Assert.assertThat(InputComponents.hasValue(paths), is(false));
      InputComponents.setValueFor(converterFactory, paths, OperatingSystemUtils.getTempDirectory().toPath());
      Assert.assertThat(InputComponents.hasValue(paths), is(true));
      List<Path> list = Lists.toList(paths.getValue());
      Assert.assertThat(list.size(), equalTo(1));
      Assert.assertThat(list, hasItem(OperatingSystemUtils.getTempDirectory().toPath()));
   }

   @Test
   public void testUISelectOneIndex()
   {
      values.setValueChoices(Arrays.asList("A", "B", "C"));
      values.setValue("B");
      Assert.assertEquals(1, values.getSelectedIndex());
      values.setValue(null);
      Assert.assertEquals(-1, values.getSelectedIndex());
   }

   @Test
   public void testUISelectManyIndexes()
   {
      manyValues.setValueChoices(Arrays.asList("A", "B", "C"));
      manyValues.setValue(Arrays.asList("B", "C"));
      Assert.assertArrayEquals(new int[] { 1, 2 }, manyValues.getSelectedIndexes());
      manyValues.setValue(Collections.emptyList());
      Assert.assertArrayEquals(new int[0], manyValues.getSelectedIndexes());
   }

   @Test
   public void testUIInputIntegerWithEmptyStringValue()
   {
      Assert.assertNull(integerInput.getValue());
      InputComponents.setValueFor(converterFactory, integerInput, "");
      Assert.assertNull(integerInput.getValue());
      InputComponents.setValueFor(converterFactory, integerInput, "123");
      Assert.assertThat(integerInput.getValue(), equalTo(123));
   }
}
