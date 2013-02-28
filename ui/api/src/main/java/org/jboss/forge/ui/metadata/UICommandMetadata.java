/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.metadata;

import java.net.URL;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface UICommandMetadata
{
   String getName();

   String getDescription();

   UICategory getCategory();

   /**
    * Returns the location of the documentation of this command
    *
    * @return null if no documentation was found
    */
   URL getDocLocation();

}
