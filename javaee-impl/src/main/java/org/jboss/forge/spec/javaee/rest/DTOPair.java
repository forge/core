package org.jboss.forge.spec.javaee.rest;

import org.jboss.forge.resources.java.JavaResource;

public class DTOPair
{
   private JavaResource rootDTO;
   private JavaResource nestedDTO;

   public JavaResource getRootDTO()
   {
      return rootDTO;
   }

   public void setRootDTO(JavaResource rootDTO)
   {
      this.rootDTO = rootDTO;
   }

   public JavaResource getNestedDTO()
   {
      return nestedDTO;
   }

   public void setNestedDTO(JavaResource nestedDTO)
   {
      this.nestedDTO = nestedDTO;
   }
}
