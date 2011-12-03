/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.env;

import javax.inject.Inject;

import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationImplTest extends AbstractShellTest
{
   @Inject
   private Configuration config;

   @Test
   public void test()
   {
      String string = config.getString("foobar");
      Assert.assertNull(string);

      /*
       * By default, the write operations will persist to the first delegate (PROJECT), 
       * if no project is available they will persist to the next delegate (user settings)
       */
      Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
      Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

      Assert.assertEquals("foobar!", config.getString("foobar"));
   }
}
