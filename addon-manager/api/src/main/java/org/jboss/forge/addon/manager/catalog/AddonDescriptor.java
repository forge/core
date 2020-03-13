/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.catalog;

/**
 * An addon descriptor as present in https://github.com/forge/website-data/tree/master/addons YAMLs
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface AddonDescriptor
{
   /**
    * @return The unique ID of this addon
    */
   String getId();

   /**
    * @return The human-readable name of this addon
    */
   String getName();

   /**
    * @return The human-readable description of this addon
    */
   String getDescription();

   /**
    * @return The category this addon is bound to
    */
   AddonDescriptorCategory getCategory();

   /**
    * @return The tags used when searching for this addon
    * @see AddonSearchCommand
    */
   String[] getTags();

   /**
    * @return The commands executed to install this addon
    * @see AddonInstallFromCatalogCommand
    */
   String[] getInstallCmd();

   /**
    * @return The author name, for bragging purposes
    */
   String getAuthorName();

   public static enum AddonDescriptorCategory
   {
      COMMUNITY, CORE;
   }
}
