/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result;

import org.jboss.forge.addon.ui.command.UICommand;

/**
 * The result of a {@link UICommand}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Result
{
   /**
    * Get the output message from the executed {@link UICommand}.
    */
   public String getMessage();
}
