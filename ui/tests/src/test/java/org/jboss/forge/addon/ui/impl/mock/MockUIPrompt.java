/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.mock;

import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MockUIPrompt implements UIPrompt
{

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.input.UIPrompt#prompt(java.lang.String)
    */
   @Override
   public String prompt(String message)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.input.UIPrompt#promptSecret(java.lang.String)
    */
   @Override
   public String promptSecret(String message)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.input.UIPrompt#promptBoolean(java.lang.String)
    */
   @Override
   public boolean promptBoolean(String message)
   {
      // TODO Auto-generated method stub
      return false;
   }

}
