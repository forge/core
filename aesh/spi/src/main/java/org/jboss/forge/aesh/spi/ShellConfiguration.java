/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.spi;

import org.jboss.forge.container.services.Exported;

/**
 * Used to configure the shell before it is initialized.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface ShellConfiguration
{
   /**
    * Perform any necessary configuration.
    */
   void configure();
}
