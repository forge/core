/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result.navigation;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.util.MockCommand;
import org.jboss.forge.addon.ui.util.MockCommand2;
import org.jboss.forge.addon.ui.util.MockCommand3;
import org.jboss.forge.addon.ui.util.MockWizard;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link NavigationResultBuilder}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class NavigationResultBuilderTest
{
   @Test
   public void testBuilderBuildShouldBeNullForNoEntries()
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      NavigationResult result = builder.build();
      Assert.assertNull(result);
   }

   @Test
   public void testBuilderCreateFromNullNavigationResult()
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      NavigationResult result = builder.build();
      NavigationResultBuilder builder2 = NavigationResultBuilder.create(result);
      Assert.assertNotNull(builder2);
      Assert.assertNotSame(builder2, builder);
      NavigationResult result2 = builder2.build();
      Assert.assertNull(result2);
   }

   @Test
   public void testBuilderCreateFromExistingNavigationResult()
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      builder.add(MockCommand.class);
      NavigationResult result = builder.build();
      NavigationResultBuilder builder2 = NavigationResultBuilder.create(result);
      Assert.assertNotNull(builder2);
      Assert.assertNotSame(builder2, builder);
      NavigationResult result2 = builder2.build();
      Assert.assertNotNull(result2);
      Assert.assertNotNull(result2.getNext());
      Assert.assertEquals(1, result2.getNext().length);
   }

   @Test
   public void testBuilderEntriesSizeMatch()
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      builder.add(MockCommand.class).add(MockCommand2.class).add(MockCommand3.class).add(new MockCommand());
      NavigationResult result = builder.build();
      Assert.assertNotNull(result);
      Assert.assertNotNull(result.getNext());
      Assert.assertEquals(4, result.getNext().length);
   }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderAddWithWizardArgument()
    {
        NavigationResultBuilder builder = NavigationResultBuilder.create();
        List<Class<? extends UICommand>> commands = new ArrayList<>();
        commands.add(MockWizard.class);
        builder.add(Metadata.forCommand(MockWizard.class), commands);
    }
}
