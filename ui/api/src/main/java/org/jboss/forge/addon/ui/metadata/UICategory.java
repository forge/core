/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.metadata;

import org.jboss.forge.addon.ui.util.Categories;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface UICategory
{
   public static final UICategory NO_CATEGORY = Categories.create("Uncategorized");

   String getName();

   UICategory getSubCategory();
}
