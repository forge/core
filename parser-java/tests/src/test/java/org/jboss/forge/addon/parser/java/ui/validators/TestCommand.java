/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui.validators;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class TestCommand extends AbstractUICommand
{

   @Inject
   @WithAttributes(label = "Class Name", type = InputType.JAVA_CLASS_PICKER)
   private UIInput<String> className;

   @Inject
   @WithAttributes(label = "Package Name", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> packageName;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(className).add(packageName);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return null;
   }

}
