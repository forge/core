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
package org.jboss.forge.scaffold.faces;

import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.test.web.WebTest;
import org.jboss.scenario.FacesScaffoldLiveTestScenario01;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */

@RunWith(Arquillian.class)
public class FacesScaffoldLiveTest extends AbstractFacesScaffoldTest
{
   @Inject
   private WebTest webTest;

   @Test
   public void testGenerateFromNestedOneToOne() throws Exception
   {
      Project me = getShell().getCurrentProject();
      Project test = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Baz");
      getShell().execute("field string --named name");
      getShell().execute("entity --named Bar");
      getShell().execute("field string --named name");
      getShell().execute("field oneToOne --named baz --fieldType com.test.domain.Baz");
      getShell().execute("entity --named Foo");
      getShell().execute("field string --named name");
      getShell().execute("field oneToOne --named bar --fieldType com.test.domain.Bar");

      queueInputLines("", "", "", "", "");
      getShell().execute("scaffold from-entity com.test.domain.*");

      webTest.setup(test);
      JavaClass clazz = webTest.from(me, FacesScaffoldLiveTestScenario01.class);

      webTest.buildDefaultDeploymentMethod(test, clazz, Arrays.asList(
               ".addAsResource(\"META-INF/persistence.xml\", \"META-INF/persistence.xml\")"
               ));
      webTest.addAsTestClass(test, clazz);

      getShell().execute("build");
   }
}
