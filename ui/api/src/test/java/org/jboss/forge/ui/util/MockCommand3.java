/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.util;

import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.Result;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockCommand3 implements UICommand
{

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(MockCommand3.class)
               .name("A Name");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return false;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   public void validate(UIValidationContext validator)
   {
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      return null;
   }

}
