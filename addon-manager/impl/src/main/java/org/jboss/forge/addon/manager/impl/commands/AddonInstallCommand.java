package org.jboss.forge.addon.manager.impl.commands;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.Results;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandID;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.base.SimpleUICommandID;

@Singleton
public class AddonInstallCommand implements UICommand {

	private static final String COMMAND_NAME = 
			"org.jboss.forge.tools.addons.install";
	private static final String COMMAND_DESCRIPTION = 
			"Command to install a Forge 2 addon.";
	
	@Inject private UIInput<String> groupId;
	@Inject private UIInput<String> name;
	@Inject private UIInput<String> version;
	
	@Inject private AddonManager addonManager;

	public UICommandID getId() {
		return new SimpleUICommandID(COMMAND_NAME, COMMAND_DESCRIPTION);
	}

	public void initializeUI(UIContext context) throws Exception {
		initializeGroupIdInput(context);
		initializeNameInput(context);
		initializeVersionInput(context);
	}

	private void initializeGroupIdInput(UIContext context) {
		groupId.setLabel("Group Id:");
		groupId.setRequired(true);
		context.getUIBuilder().add(groupId);
	}

	private void initializeNameInput(UIContext context) {
		name.setLabel("Name:");
		name.setRequired(true);
		context.getUIBuilder().add(name);
	}

	private void initializeVersionInput(UIContext context) {
		version.setLabel("Version:");
		version.setRequired(true);
		context.getUIBuilder().add(version);
	}

	public void validate(UIValidationContext context) {
		// TODO Auto-generated method stub

	}

	public Result execute(UIContext context) {
		String coordinates = 
				groupId.getValue() + ':' + 
				name.getValue() + ',' +
				version.getValue();
		System.out.println("About to install addon: " + coordinates);
		if (addonManager == null) System.out.println("addonManager is null");
		try {
			addonManager.install(AddonId.fromCoordinates(coordinates)).perform();
			System.out.println("Addon installed: " + coordinates);
		} catch (Throwable t) {
			System.out.println("Something went wrong");
			t.printStackTrace();
		}
		return Results.success("Addon " + coordinates + " was installed succesfully.");
	}
	
}
