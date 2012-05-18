/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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

package org.jboss.forge.spec.plugin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.spec.javaee.SoapFacet;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class SoapPluginTest extends SingletonAbstractShellTest
{
   @Test
   public void testInstall() throws Exception
   {
      Project project = initializeJavaProject();

      assertFalse(project.hasFacet(SoapFacet.class));
      queueInputLines("", "");
      getShell().execute("setup soap");
      assertTrue(project.hasFacet(SoapFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               DependencyBuilder.create("org.jboss.spec.javax.xml.soap:jboss-saaj-api_1.3_spec")));
   }

}
