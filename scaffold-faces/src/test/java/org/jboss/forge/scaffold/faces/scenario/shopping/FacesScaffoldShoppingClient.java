/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces.scenario.shopping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
         form.getInputByName("search:addressBeanExampleStreet").setValueAttribute("Address Street #2");
         form.getInputByName("search:addressBeanExampleCity").setValueAttribute("Address City #2");
         page = page.getAnchorByText("Create New").click();

         form = page.getFormByName("create");
         assertTrue(page.asText().contains("Create a new Address"));
         assertEquals("Address Street #2", form.getInputByName("create:addressBeanAddressStreet").getValueAttribute());
         assertEquals("Address City #2", form.getInputByName("create:addressBeanAddressCity").getValueAttribute());
         page = page.getAnchorByText("Save").click();

         // Create yet another Address

         page = page.getAnchorByText("Create New").click();
         form = page.getFormByName("create");
         form.getInputByName("create:addressBeanAddressStreet").setValueAttribute("Address Street #3");
         form.getInputByName("create:addressBeanAddressCity").setValueAttribute("Address City #3");
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
         HtmlTable table = (HtmlTable) page.getHtmlElementById("create:customerBeanCustomerAddresses");
         assertEquals("Address Street #1", table.getCellAt(1, 0).getTextContent());

         // Test OneToMany (mappedBy)

         form = page.getFormByName("create");
         form.getSelectByName("create:customerBeanCustomerOrders:submittedOrderBeanAddAddress")
                  .setSelectedAttribute("2", true);
         page = page.getHtmlElementById("create:customerBeanCustomerOrders:customerBeanCustomerOrdersAdd").click();
         table = (HtmlTable) page.getHtmlElementById("create:customerBeanCustomerOrders");
         assertEquals("Address street: Address Street #2, City: Address City #2, zipCode: 0", table.getCellAt(2, 0).getTextContent());

         // Test adding multiple OneToMany (mappedBy) before clicking Save

         form = page.getFormByName("create");
         form.getSelectByName("create:customerBeanCustomerOrders:submittedOrderBeanAddAddress")
                  .setSelectedAttribute("3", true);
         page = page.getHtmlElementById("create:customerBeanCustomerOrders:customerBeanCustomerOrdersAdd").click();
         table = (HtmlTable) page.getHtmlElementById("create:customerBeanCustomerOrders");
         assertTrue( "Address street: Address Street #2, City: Address City #2, zipCode: 0".equals(table.getCellAt(2, 0).getTextContent()) || "Address street: Address Street #3, City: Address City #3, zipCode: 0".equals(table.getCellAt(2, 0).getTextContent() ));
         assertTrue( "Address street: Address Street #2, City: Address City #2, zipCode: 0".equals(table.getCellAt(3, 0).getTextContent()) || "Address street: Address Street #3, City: Address City #3, zipCode: 0".equals(table.getCellAt(3, 0).getTextContent() ));
         assertTrue(!table.getCellAt(2, 0).getTextContent().equals(table.getCellAt(3, 0).getTextContent()));

         page = page.getAnchorByText("Save").click();

         // Test it all saved

         assertTrue(page.asText().contains("Search Customer entities"));
         table = (HtmlTable) page.getHtmlElementById("search:customerBeanPageItems");
         assertEquals("Customer Firstname #1", table.getCellAt(1, 0).getTextContent());

         page = page.getAnchorByText("Customer Firstname #1").click();
         assertTrue(page.asText().contains("View existing Customer"));
         assertEquals("Customer Firstname #1", page.getHtmlElementById("customerBeanCustomerFirstName")
                  .getTextContent());
         table = (HtmlTable) page.getHtmlElementById("customerBeanCustomerAddresses");
         assertEquals("Address Street #1", table.getCellAt(1, 0).getTextContent());
         table = (HtmlTable) page.getHtmlElementById("customerBeanCustomerOrders");
         assertTrue( "Address street: Address Street #2, City: Address City #2, zipCode: 0".equals(table.getCellAt(1, 0).getTextContent()) || "Address street: Address Street #3, City: Address City #3, zipCode: 0".equals(table.getCellAt(1, 0).getTextContent() ));
         assertTrue( "Address street: Address Street #2, City: Address City #2, zipCode: 0".equals(table.getCellAt(2, 0).getTextContent()) || "Address street: Address Street #3, City: Address City #3, zipCode: 0".equals(table.getCellAt(2, 0).getTextContent() ));
         assertTrue(!table.getCellAt(1, 0).getTextContent().equals(table.getCellAt(2, 0).getTextContent()));

         // Test foreign key constraints

         page = page.getAnchorByText("Address").click();
         page = page.getAnchorByText("Address Street #1").click();
         page = page.getAnchorByText("Edit").click();
         assertTrue(!page.asXml().contains("<ul class=\"error\">"));
         page = page.getAnchorByText("Delete").click();
         assertTrue(page.asXml().contains("<ul class=\"error\">"));

         // Test deleting the relationships

         page = page.getAnchorByText("Customer").click();
         page = page.getAnchorByText("Customer Firstname #1").click();
         page = page.getAnchorByText("Edit").click();
         assertTrue(page.asText().contains("Edit existing Customer"));
         table = (HtmlTable) page.getHtmlElementById("create:customerBeanCustomerAddresses");
         page = table.getCellAt(1, 4).getHtmlElementsByTagName("a").get(0).click();
         table = (HtmlTable) page.getHtmlElementById("create:customerBeanCustomerAddresses");
         assertEquals("", table.getCellAt(1, 0).getTextContent());
         table = (HtmlTable) page.getHtmlElementById("create:customerBeanCustomerOrders");
         page = table.getCellAt(2, 1).getHtmlElementsByTagName("a").get(0).click();
         table = (HtmlTable) page.getHtmlElementById("create:customerBeanCustomerOrders");
         page = table.getCellAt(2, 1).getHtmlElementsByTagName("a").get(0).click();
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