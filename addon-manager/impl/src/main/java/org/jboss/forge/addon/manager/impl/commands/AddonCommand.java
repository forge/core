package org.jboss.forge.addon.manager.impl.commands;

import javax.inject.Inject;

import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandID;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.base.SimpleUICommandID;

public abstract class AddonCommand implements UICommand, AddonCommandConstants
{

   @Inject
   private UIInput<String> groupId;
   @Inject
   private UIInput<String> name;
   @Inject
   private UIInput<String> version;

   protected abstract String getName();

   protected abstract String getDescription();

   protected String getCoordinates() 
   {
      return groupId.getValue() + ':' +
               name.getValue() + ',' +
               version.getValue();
   }

   @Override
   public UICommandID getId()
   {
      return new SimpleUICommandID(getName(), getDescription());
   }

   public void initializeUI(UIContext context) throws Exception
   {
      initializeGroupIdInput(context);
      initializeNameInput(context);
      initializeVersionInput(context);
   }

   private void initializeGroupIdInput(UIContext context)
   {
      groupId.setLabel("Group Id:");
      groupId.setRequired(true);
      context.getUIBuilder().add(groupId);
   }

   private void initializeNameInput(UIContext context)
   {
      name.setLabel("Name:");
      name.setRequired(true);
      context.getUIBuilder().add(name);
   }

   private void initializeVersionInput(UIContext context)
   {
      version.setLabel("Version:");
      version.setRequired(true);
      context.getUIBuilder().add(version);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

}
