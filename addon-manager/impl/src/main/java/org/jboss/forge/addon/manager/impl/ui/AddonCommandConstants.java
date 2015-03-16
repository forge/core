package org.jboss.forge.addon.manager.impl.ui;

public interface AddonCommandConstants
{
   String[] ADDON_MANAGER_CATEGORIES = { "Forge", "Manage" };
   
   String ADDON_INSTALL_COMMAND_NAME = "Install an Addon";
   String ADDON_INSTALL_COMMAND_NAME_NO_GUI = "addon-install";
   String ADDON_INSTALL_COMMAND_DESCRIPTION = "Command to install a Furnace 2 addon.";
   
   String ADDON_UPDATE_COMMAND_NAME = "Update an Addon";
   String ADDON_UPDATE_COMMAND_NAME_NO_GUI = "addon-update";
   String ADDON_UPDATE_COMMAND_DESCRIPTION = "Command to update a Furnace 2 addon. It may end up updating multiple addons.";
   
   String ADDON_BUILD_INSTALL_COMMAND_NAME_FROM_GIT = "Install an Addon from GIT";
   String ADDON_BUILD_INSTALL_COMMAND_NAME_FROM_GIT_NO_GUI = "addon-install-from-git";
   String ADDON_BUILD_INSTALL_COMMAND_NAME = "Build and Install an Addon";
   String ADDON_BUILD_INSTALL_COMMAND_NAME_NO_GUI = "addon-build-and-install";
   String ADDON_BUILD_INSTALL_COMMAND_DESCRIPTION = "Command to build and install a Furnace 2 addon.";
   
   String ADDON_LIST_COMMAND_DESCRIPTION = "Command to list all currently installed Addons.";
   String ADDON_LIST_COMMAND_NAME = "addon-list";
   
   String ADDON_REMOVE_COMMAND_NAME = "Remove an Addon";
   String ADDON_REMOVE_COMMAND_NAME_NO_GUI = "addon-remove";
   String ADDON_REMOVE_COMMAND_DESCRIPTION = "Command to remove a Furnace 2 addon.";
}
