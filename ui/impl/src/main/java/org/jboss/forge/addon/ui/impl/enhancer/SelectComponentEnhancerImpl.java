/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.enhancer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.jboss.forge.addon.ui.cdi.CommandScoped;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.SelectComponentEnhancer;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Implementation of the {@link SelectComponentEnhancer} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@CommandScoped
@SuppressWarnings("unchecked")
public class SelectComponentEnhancerImpl implements SelectComponentEnhancer
{
   private Map<String, Predicate<Object>> filters = new HashMap<String, Predicate<Object>>();

   @Override
   public <T> void registerValueChoiceFilter(Class<T> facetType, Predicate<T> filter)
   {
      Assert.notNull(facetType, "Facet type may not be null");
      Assert.notNull(filter, "Filter may not be null");
      String key = facetType.getName();
      filters.put(key, (Predicate<Object>) filter);
   }

   void applyFiltersFor(SelectComponent<?, Object> select)
   {
      String key = select.getValueType().getName();
      Predicate<Object> filter = filters.get(key);
      if (filter != null)
      {
         Collection<Object> filteredFacets = new LinkedList<Object>();
         Iterable<Object> valueChoices = select.getValueChoices();
         if (valueChoices != null)
         {
            for (Object choice : valueChoices)
            {
               if (filter.accept(choice))
               {
                  filteredFacets.add(choice);
               }
            }
         }
         select.setValueChoices(filteredFacets);
      }
   }
}
