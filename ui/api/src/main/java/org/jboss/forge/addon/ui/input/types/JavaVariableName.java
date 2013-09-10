package org.jboss.forge.addon.ui.input.types;

public class JavaVariableName extends PatternedInput
{

   private String variableName;

   protected String getVariableName()
   {
      return variableName;
   }

   protected void setVariableName(String variableName)
   {
      this.variableName = variableName;
   }

}
