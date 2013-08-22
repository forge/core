/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.UIValidator;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.impl.mock.MockUIContext;
import org.jboss.forge.addon.ui.impl.mock.MockValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
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
 * Tests the validation features
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class InputComponentValidatorTest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addPackage(MockUIContext.class.getPackage())
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   @WithAttributes(label = "Required With Message", required = true, requiredMessage = "MSG")
   UIInput<String> requiredWithMessage;

   @Inject
   @WithAttributes(label = "Required Without Message", required = true)
   UIInput<String> requiredNoMessage;

   @Inject
   @WithAttributes(label = "Not Required", required = false)
   UIInput<String> notRequired;

   @Inject
   UIInput<String> withValidator;

   @Test
   public void testRequiredWithMessage()
   {
      UIContext ctx = new MockUIContext();
      MockValidationContext context = new MockValidationContext(ctx);
      requiredWithMessage.validate(context);
      List<String> errors = context.getErrorsFor(requiredWithMessage);
      Assert.assertThat(errors, notNullValue());
      Assert.assertThat(errors.size(), equalTo(1));
      Assert.assertThat(errors, hasItem("MSG"));
   }

   @Test
   public void testRequiredNoMessage()
   {
      UIContext ctx = new MockUIContext();
      MockValidationContext context = new MockValidationContext(ctx);
      requiredNoMessage.validate(context);
      List<String> errors = context.getErrorsFor(requiredNoMessage);
      Assert.assertThat(errors, notNullValue());
      Assert.assertThat(errors.size(), equalTo(1));
      Assert.assertThat(errors, hasItem(InputComponents.validateRequired(requiredNoMessage)));
   }

   @Test
   public void testNotRequired()
   {
      UIContext ctx = new MockUIContext();
      MockValidationContext context = new MockValidationContext(ctx);
      notRequired.validate(context);
      List<String> errors = context.getErrorsFor(notRequired);
      Assert.assertThat(errors, nullValue());
   }

   @Test
   public void testAdditionalValidator()
   {
      UIContext ctx = new MockUIContext();
      MockValidationContext context = new MockValidationContext(ctx);
      withValidator.setValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext validator)
         {
            validator.addValidationError(withValidator, "MSG");
         }
      });
      withValidator.validate(context);
      List<String> errors = context.getErrorsFor(withValidator);
      Assert.assertThat(errors, notNullValue());
      Assert.assertThat(errors.size(), equalTo(1));
      Assert.assertThat(errors, hasItem("MSG"));

   }
}
