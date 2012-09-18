package org.jboss.forge.spec.ejb.testClasses;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
public class BaseEntity implements Serializable
{
   private String name;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
}
