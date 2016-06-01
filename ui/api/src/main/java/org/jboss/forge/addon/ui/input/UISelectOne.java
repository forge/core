/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

/**
 * A {@link UISelectOne} should be used when the number of items to be chosen are known before rendering the component.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <VALUETYPE>
 */
public interface UISelectOne<VALUETYPE> extends SelectComponent<UISelectOne<VALUETYPE>, VALUETYPE>,
         SingleValued<UISelectOne<VALUETYPE>, VALUETYPE>
{
   /**
    * @return the selected index for the value returned in {@link #getValue()} in the {@link #getValueChoices()} list.
    *         Returns <code>-1</code> if {@link #getValue()} is <code>null</code>
    */
   int getSelectedIndex();
}
