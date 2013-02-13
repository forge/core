package org.jboss.forge.addon.manager.impl.commands;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

import org.jboss.forge.ui.UIBuilder;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.base.UICommandMetadataBase;

@Vetoed
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
      return groupId.getValue() + ':' + name.getValue() + ',' + version.getValue();
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return new UICommandMetadataBase(getName(), getDescription());
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      initializeGroupIdInput(builder);
      initializeNameInput(builder);
      initializeVersionInput(builder);
   }

   private void initializeGroupIdInput(UIBuilder builder)
   {
      groupId.setLabel("Group Id:");
      groupId.setRequired(true);
      builder.add(groupId);
   }

   private void initializeNameInput(UIBuilder builder)
   {
      name.setLabel("Name:");
      name.setRequired(true);
      builder.add(name);
   }

   private void initializeVersionInput(UIBuilder builder)
   {
      version.setLabel("Version:");
      version.setRequired(true);
      builder.add(version);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

}
