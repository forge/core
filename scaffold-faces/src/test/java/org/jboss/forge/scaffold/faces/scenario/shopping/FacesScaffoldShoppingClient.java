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
import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Ignore
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

         // Create an Address

         page = page.getAnchorByText("Address").click();
         assertTrue(page.asText().contains("Search Address entities"));
         page = page.getAnchorByText("Create New").click();

         HtmlForm form = page.getFormByName("create");
         assertTrue(page.asText().contains("Create a new Address"));
         form.getInputByName("create:addressBeanAddressStreet").setValueAttribute("Address Street #1");
         form.getInputByName("create:addressBeanAddressCity").setValueAttribute("Address City #1");
         page = page.getAnchorByText("Save").click();

         // Create another Address

         assertTrue(page.asText().contains("Search Address entities"));
         form = page.getFormByName("search");
         form.getInputByName("search:addressBeanSearchStreet").setValueAttribute("Address Street #2");
         form.getInputByName("search:addressBeanSearchCity").setValueAttribute("Address City #2");
         page = page.getAnchorByText("Create New").click();

         form = page.getFormByName("create");
         assertTrue(page.asText().contains("Create a new Address"));
         assertEquals("Address Street #2",form.getInputByName("create:addressBeanAddressStreet").getValueAttribute());
         assertEquals("Address City #2",form.getInputByName("create:addressBeanAddressCity").getValueAttribute());
         page = page.getAnchorByText("Save").click();

         // Create a Customer

         page = page.getAnchorByText("Customer").click();
         assertTrue(page.asText().contains("Search Customer entities"));
         page = page.getAnchorByText("Create New").click();

         form = page.getFormByName("create");
         assertTrue(page.asText().contains("Create a new Customer"));
         form.getInputByName("create:customerBeanCustomerFirstName").setValueAttribute("Customer Firstname #1");
         form.getInputByName("create:customerBeanCustomerLastName").setValueAttribute("Customer Lastname #1");

         // Test OneToMany (not mappedBy)

         form.getSelectByName("create:customerBeanCustomerAddressesSelect")
                  .setSelectedAttribute("1", true);
         page = page.getHtmlElementById("create:customerBeanCustomerAddressesAdd").click();

         // Test OneToMany (mappedBy)

         form = page.getFormByName("create");
         form.getSelectByName("create:customerBeanCustomerOrders:submittedOrderBeanSubmittedOrderAddress")
                  .setSelectedAttribute("2", true);
         page = page.getHtmlElementById("create:customerBeanCustomerOrders:customerBeanCustomerOrdersAdd").click();
         page = page.getAnchorByText("Save").click();

         // Test it all saved

         assertTrue(page.asText().contains("Search Customer entities"));
         HtmlTable table = (HtmlTable) page.getHtmlElementById("search:customerBeanPageItems");
         assertEquals("Customer Firstname #1", table.getCellAt(1, 0).getTextContent());

         page = page.getAnchorByText("Customer Firstname #1").click();
         assertTrue(page.asText().contains("View existing Customer"));
         assertEquals("Customer Firstname #1", page.getHtmlElementById("customerBeanCustomerFirstName")
                  .getTextContent());
         table = (HtmlTable) page.getHtmlElementById("customerBeanCustomerAddresses");
         assertEquals("Address Street #1", table.getCellAt(1, 0).getTextContent());
         table = (HtmlTable) page.getHtmlElementById("customerBeanCustomerOrders");
         assertEquals("Address Street #2, Address City #2, , 0", table.getCellAt(1, 0).getTextContent());

         // Test deleting the relationships

         page = page.getAnchorByText("Edit").click();
         assertTrue(page.asText().contains("Edit existing Customer"));
         table = (HtmlTable) page.getHtmlElementById("create:customerBeanCustomerAddresses");
         page = table.getCellAt( 1, 4 ).getHtmlElementsByTagName("a").get(0).click();
         table = (HtmlTable) page.getHtmlElementById("create:customerBeanCustomerAddresses");
         assertEquals("", table.getCellAt(1, 0).getTextContent());
         table = (HtmlTable) page.getHtmlElementById("create:customerBeanCustomerOrders");
         page = table.getCellAt( 2, 1 ).getHtmlElementsByTagName("a").get(0).click();
         table = (HtmlTable) page.getHtmlElementById("create:customerBeanCustomerOrders");
         assertEquals("", table.getCellAt(2, 0).getTextContent());
         page = page.getAnchorByText("Save").click();

         assertTrue(page.asText().contains("View existing Customer"));
         assertEquals("Customer Firstname #1", page.getHtmlElementById("customerBeanCustomerFirstName")
                  .getTextContent());
         table = (HtmlTable) page.getHtmlElementById("customerBeanCustomerAddresses");
         assertEquals("", table.getCellAt(1, 0).getTextContent());
         table = (HtmlTable) page.getHtmlElementById("customerBeanCustomerOrders");
         assertEquals("", table.getCellAt(1, 0).getTextContent());
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