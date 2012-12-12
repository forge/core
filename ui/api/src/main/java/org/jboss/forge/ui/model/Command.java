/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.model;

import java.util.List;

/**
 * A Command is a model representation of a method invocation.
 *
 * A Command may contain 0..N parameters and
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface Command
{
   /**
    * The Name of this command
    *
    */
   public String getName();

   /**
    * Returns an unmodifiable list of the possible parameters of this command
    *
    * @return List with the required parameters. Returns an empty list if no parameters are needed.
    */
   public List<CommandParameter> getParameters();
}
