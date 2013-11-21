/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.enhancer;

import java.util.logging.Logger;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.InputComponentInjectionEnricher;
import org.jboss.forge.addon.ui.input.SelectComponent;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class SelectComponentEnricher implements InputComponentInjectionEnricher
{

   private static final Logger logger = Logger.getLogger(SelectComponentEnricher.class.getName());
   @Inject
   SelectComponentEnhancerImpl enhancer;

   @SuppressWarnings("unchecked")
   @Override
   public void enrich(InjectionPoint injectionPoint, InputComponent<?, ?> input)
   {
      // Works only for SelectComponents
      if (input instanceof SelectComponent)
      {
         SelectComponent<?, Object> select = (SelectComponent<?, Object>) input;
         try
         {
            enhancer.applyFiltersFor(select);
         }
         catch (ContextNotActiveException noContext)
         {
            logger.warning("Context not active while applying UISelectOne filters. Ignoring.");
         }
      }
   }

}
