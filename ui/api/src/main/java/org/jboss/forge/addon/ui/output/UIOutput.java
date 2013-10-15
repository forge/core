/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.output;

import java.io.PrintStream;

/**
 * Used when information must be shown to users
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIOutput
{
   /**
    * Returns the {@link PrintStream} used to display information in the output stream.
    */
   PrintStream out();

   /**
    * Returns the {@link PrintStream} used to display information in the output stream.
    */
   PrintStream err();
}
