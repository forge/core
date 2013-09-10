package org.jboss.forge.addon.ui.input.types;

public class JavaVariableName extends PatternedStringInput
{

   private String variableName;

   public String getVariableName()
   {
      return variableName;
   }

   public void setVariableName(String variableName)
   {
      this.variableName = variableName;
   }

   @Override
   public String getValidatableValue()
   {
      return variableName;
   }

}
