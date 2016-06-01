/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.annotation;

import java.io.File;

import org.jboss.forge.addon.ui.UIDesktop;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link ReservedParameters}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ReservedParametersTest
{
   @Test
   public void testIsReservedParameter()
   {
      Assert.assertTrue(ReservedParameters.isReservedParameter(UIContext.class));
      Assert.assertTrue(ReservedParameters.isReservedParameter(UIPrompt.class));
      Assert.assertTrue(ReservedParameters.isReservedParameter(UIOutput.class));
      Assert.assertTrue(ReservedParameters.isReservedParameter(UIProgressMonitor.class));
      Assert.assertTrue(ReservedParameters.isReservedParameter(UIProvider.class));
      Assert.assertTrue(ReservedParameters.isReservedParameter(UIExecutionContext.class));
      Assert.assertTrue(ReservedParameters.isReservedParameter(UIDesktop.class));

      Assert.assertFalse(ReservedParameters.isReservedParameter(String.class));
      Assert.assertFalse(ReservedParameters.isReservedParameter(File.class));
   }
}
