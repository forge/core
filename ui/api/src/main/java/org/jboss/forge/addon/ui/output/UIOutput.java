/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

   /**
    * Prints a successful message in the provided {@link PrintStream}
    */
   void success(final PrintStream out, final String message);

   /**
    * Prints an error message in the provided {@link PrintStream}
    */
   void error(final PrintStream out, final String message);

   /**
    * Prints an information message in the provided {@link PrintStream}
    */
   void info(final PrintStream out, final String message);

   /**
    * Prints a warning message in the provided {@link PrintStream}
    */
   void warn(final PrintStream out, final String message);
}
