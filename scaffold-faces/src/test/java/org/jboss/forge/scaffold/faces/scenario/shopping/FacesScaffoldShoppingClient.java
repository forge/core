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
package org.jboss.forge.scaffold.faces.scenario.shopping;

import static org.junit.Assert.*;

import org.jboss.jsfunit.api.InitialPage;
import org.jboss.jsfunit.api.JSFUnitResource;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@InitialPage("/faces/index.xhtml")
public class FacesScaffoldShoppingClient
{
   @JSFUnitResource
   private JSFClientSession client;

   @Test
   public void testAll() throws Exception
   {
      HtmlPage page = (HtmlPage) this.client.getContentPage();

      try
      {
         // Welcome page

         assertTrue(page.asText().contains("Welcome to Forge"));

         // Create an Owner

         page = page.getAnchorByText("Customer").click();
         assertTrue(page.asText().contains("Search Customer entities"));
         page = page.getAnchorByText("Create New").click();

         HtmlForm form = page.getFormByName("create");
         assertTrue(page.asText().contains("Create a new Customer"));
         form.getInputByName("create:customerBeanCustomerFirstName").setValueAttribute("Customer Firstname #1");
         form.getInputByName("create:customerBeanCustomerLastName").setValueAttribute("Customer Lastname #1");
         page = page.getAnchorByText("Save").click();
      }
      catch (Throwable t)
      {
         t.printStackTrace();
         throw new RuntimeException(t);
      }
      finally
      {
         System.out.println(page.asXml());
      }
   }
}