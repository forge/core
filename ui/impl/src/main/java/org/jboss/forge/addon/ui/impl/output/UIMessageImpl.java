/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.output;

import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.furnace.util.Assert;

/**
 * Default implementation of {@link UIMessage}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIMessageImpl implements UIMessage
{
   private final String description;
   private final Severity severity;
   private InputComponent<?, ?> inputComponent;

   public UIMessageImpl(Severity severity, String description, InputComponent<?, ?> inputComponent)
   {
      Assert.notNull(severity, "Severity should never be null");
      this.description = description;
      this.severity = severity;
      this.inputComponent = inputComponent;
   }

   @Override
   public String getDescription()
   {
      return this.description;
   }

   @Override
   public Severity getSeverity()
   {
      return this.severity;
   }

   @Override
   public InputComponent<?, ?> getSource()
   {
      return this.inputComponent;
   }

   @Override
   public String toString()
   {
      return "[" + severity + "] " + description;
   }

}
