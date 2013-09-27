package org.jboss.forge.ui.test;

import java.util.List;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.CommandExecutionListener;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;

public interface CommandTester<C extends UICommand>
{

   public void setInitialSelection(Resource<?>... selection);

   /**
    * Is the current command dialog valid ?
    * 
    * @return true if valid, false otherwise
    */
   public boolean isValid();

   /**
    * The validation errors for the current command dialog
    */
   public List<String> getValidationErrors();

   /**
    * Is the dialog allowed to finish?
    */
   public boolean canExecute();

   /**
    * Finish clicked
    * 
    * @param listener if you wish to listen for the result for the command dialog.
    * @throws Exception if anything wrong happens
    */
   public void execute(CommandExecutionListener listener) throws Exception;

   /**
    * Sets the value of a property
    * 
    * TODO: Property should be typesafe.
    */
   public void setValueFor(String property, Object value);

   /**
    * Returns the {@link InputComponent} by the name
    */
   public InputComponent<?, ?> getInputComponent(String property);
   
   /**
    * Is this current command enabled ?
    */
   public boolean isEnabled();

}
