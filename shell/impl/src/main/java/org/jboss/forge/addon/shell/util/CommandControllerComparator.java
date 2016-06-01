/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.util;

import java.util.Comparator;

import org.jboss.forge.addon.ui.controller.CommandController;

/**
 * A comparator for {@link CommandController} objects
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CommandControllerComparator implements Comparator<CommandController>
{

   @Override
   public int compare(CommandController left, CommandController right)
   {
      if (left == right)
         return 0;
      if (right == null)
         return 1;
      if (left == null)
         return -1;

      int categoryResult = left.getMetadata().getCategory().toString()
               .compareTo(right.getMetadata().getCategory().toString());
      if (categoryResult == 0)
      {
         String leftName = left.getMetadata().getName();
         String rightName = right.getMetadata().getName();

         if (leftName == rightName)
            return 0;
         if (leftName == null)
            return -1;

         return leftName.compareTo(rightName);
      }

      return categoryResult;
   }

}
