/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl.ui;

public interface AddonCommandConstants
{
   String[] ADDON_MANAGER_CATEGORIES = { "Forge", "Manage" };

   String ADDON_INSTALL_COMMAND_NAME = "Install an Addon";
   String ADDON_INSTALL_COMMAND_NAME_NO_GUI = "addon-install";
   String ADDON_INSTALL_COMMAND_DESCRIPTION = "Installs a Forge addon.";

   String ADDON_INSTALL_FROM_CATALOG_COMMAND_NAME = "Install an Addon from the catalog";
   String ADDON_INSTALL_FROM_CATALOG_COMMAND_NAME_NO_GUI = "addon-install-from-catalog";
   String ADDON_INSTALL_FROM_CATALOG_COMMAND_DESCRIPTION = "Installs a Forge addon from the available catalog";

   String ADDON_SEARCH_COMMAND_NAME = "Search an Addon";
   String ADDON_SEARCH_COMMAND_NAME_NO_GUI = "addon-search";
   String ADDON_SEARCH_COMMAND_DESCRIPTION = "Search a Forge addon from the available catalog";

   String ADDON_UPDATE_COMMAND_NAME = "Update an Addon";
   String ADDON_UPDATE_COMMAND_NAME_NO_GUI = "addon-update";
   String ADDON_UPDATE_COMMAND_DESCRIPTION = "Updates a Forge addon. It may update or install addons that the specified addon depends on.";

   String ADDON_BUILD_INSTALL_COMMAND_NAME_FROM_GIT = "Install an Addon from GIT";
   String ADDON_BUILD_INSTALL_COMMAND_NAME_FROM_GIT_NO_GUI = "addon-install-from-git";
   String ADDON_BUILD_INSTALL_COMMAND_NAME = "Build and Install an Addon";
   String ADDON_BUILD_INSTALL_COMMAND_NAME_NO_GUI = "addon-build-and-install";
   String ADDON_BUILD_INSTALL_COMMAND_DESCRIPTION = "Builds and installs a Forge addon.";

   String ADDON_LIST_COMMAND_DESCRIPTION = "Command to list all currently installed Addons.";
   String ADDON_LIST_COMMAND_NAME = "addon-list";

   String ADDON_REMOVE_COMMAND_NAME = "Remove an Addon";
   String ADDON_REMOVE_COMMAND_NAME_NO_GUI = "addon-remove";
   String ADDON_REMOVE_COMMAND_DESCRIPTION = "Removes a Forge addon.";
}
