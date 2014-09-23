/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.plugins;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ConfigurationElementTest
{

   @Test
   public void testConfigurationElementBuilder()
   {
      ConfigurationElementBuilder element = ConfigurationElementBuilder.create()
               .setName("myCustomRule")
               .addAttribute("implementation", "org.apache.maven.enforcer.rule.MyCustomRule").
               addChild(ConfigurationElementBuilder.create().setName("shouldIfail").setText("false"));
      String expected = "<myCustomRule implementation=\"org.apache.maven.enforcer.rule.MyCustomRule\"><shouldIfail>false</shouldIfail></myCustomRule>";
      Assert.assertEquals(expected, element.toString());
   }
}
