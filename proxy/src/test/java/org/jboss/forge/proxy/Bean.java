/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.proxy;

public class Bean
{

   private String att;

   public Bean()
   {
   }

   public void setAtt(String att)
   {
      this.att = att;
   }

   public String getAtt()
   {
      return att;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((att == null) ? 0 : att.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Bean other = (Bean) obj;
      if (att == null)
      {
         if (other.att != null)
            return false;
      }
      else if (!att.equals(other.att))
         return false;
      return true;
   }

}
