/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.impl.mock.MockUIContext;
import org.jboss.forge.addon.ui.impl.mock.MockValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
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
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addPackage(MockUIContext.class.getPackage());

      return archive;
   }

   @Inject
   @WithAttributes(label = "Required With Message", required = true, requiredMessage = "MSG")
   UIInput<String> requiredWithMessage;

   @Inject
   @WithAttributes(label = "Required With Callback Message", required = true)
   UIInput<String> requiredWithCallbackMessage;

   @Inject
   @WithAttributes(label = "Required Without Message", required = true)
   UIInput<String> requiredNoMessage;

   @Inject
   @WithAttributes(label = "Not Required", required = false)
   UIInput<String> notRequired;

   @Inject
   @WithAttributes(name = "Custom Name", label = "Not Required", required = false)
   UIInput<String> anotherName;

   @Inject
   UIInput<String> withValidator;

   @Test
   public void testInputName()
   {
      Assert.assertEquals("Custom Name", anotherName.getName());
   }

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
      withValidator.addValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext validator)
         {
            validator.addValidationError(withValidator, "MSG");
         }
      });
      withValidator.setValue("FOO");
      withValidator.validate(context);
      List<String> errors = context.getErrorsFor(withValidator);
      Assert.assertThat(errors, notNullValue());
      Assert.assertThat(errors.size(), equalTo(1));
      Assert.assertThat(errors, hasItem("MSG"));
   }

   @Test
   public void testDoNotValidateOnNullValues()
   {
      UIContext ctx = new MockUIContext();
      MockValidationContext context = new MockValidationContext(ctx);
      withValidator.addValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext validator)
         {
            validator.addValidationError(withValidator, "MSG");
         }
      });
      withValidator.validate(context);
      List<String> errors = context.getErrorsFor(withValidator);
      Assert.assertThat(errors, nullValue());
   }

   @Test
   public void testRequiredAndAdditionalValidator()
   {
      UIContext ctx = new MockUIContext();
      MockValidationContext context = new MockValidationContext(ctx);
      requiredNoMessage.addValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext validator)
         {
            validator.addValidationError(withValidator, "MSG");
         }
      });
      requiredNoMessage.validate(context);
      List<String> errors = context.getErrorsFor(requiredNoMessage);
      Assert.assertThat(errors, notNullValue());
      Assert.assertThat(errors.size(), equalTo(1));
      Assert.assertThat(errors, hasItem(InputComponents.validateRequired(requiredNoMessage)));
   }

   @Test
   public void testRequiredCallbackMessage()
   {
      UIContext ctx = new MockUIContext();
      MockValidationContext context = new MockValidationContext(ctx);
      requiredWithCallbackMessage.setRequiredMessage(new Callable<String>()
      {
         @Override
         public String call() throws Exception
         {
            return "MSG";
         }
      });
      requiredWithCallbackMessage.validate(context);
      List<String> errors = context.getErrorsFor(requiredWithCallbackMessage);
      Assert.assertThat(errors, notNullValue());
      Assert.assertThat(errors.size(), equalTo(1));
      Assert.assertThat(errors, hasItem("MSG"));
   }
   
}
