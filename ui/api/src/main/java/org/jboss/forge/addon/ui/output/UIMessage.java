/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.output;

import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * A message to be displayed
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIMessage
{
   /**
    * @return the description of this message
    */
   String getDescription();

   /**
    * @return the severity of this message
    */
   Severity getSeverity();

   /**
    * @return the {@link InputComponent} that was referenced by this message or <code>null</code> if there is no
    *         {@link InputComponent} associated with this message
    */
   InputComponent<?, ?> getSource();

   enum Severity
   {
      ERROR, WARN, INFO;
   }
}
