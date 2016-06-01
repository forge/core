/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.addon.facets.factory;

import org.jboss.forge.addon.facets.AbstractFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MockExceptionFacet extends AbstractFacet<MockFaceted>
{

   @Override
   public boolean install()
   {
      throw new RuntimeException("expected");
   }

   @Override
   public boolean isInstalled()
   {
      throw new RuntimeException("expected");
   }

}
