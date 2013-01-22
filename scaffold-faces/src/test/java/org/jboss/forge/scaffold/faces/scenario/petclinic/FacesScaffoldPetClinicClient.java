/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces.scenario.petclinic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.jsfunit.api.InitialPage;
import org.jboss.jsfunit.api.JSFUnitResource;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Ignore
@InitialPage("/faces/petClinic/index.xhtml")
public class FacesScaffoldPetClinicClient
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

         page = page.getAnchorByText("Owner").click();
         assertTrue(page.asText().contains("Search Owner entities"));
         page = page.getAnchorByText("Create New").click();

         HtmlForm form = page.getFormByName("create");
         assertTrue(page.asText().contains("Create a new Owner"));
         form.getInputByName("create:ownerBeanOwnerFirstName").setValueAttribute("Owner Firstname #1");
         form.getInputByName("create:ownerBeanOwnerLastName").setValueAttribute("Owner Lastname #1");
         page = page.getAnchorByText("Save").click();

         // Edit the Owner

         assertTrue(page.asText().contains("Search Owner entities"));
         HtmlTable table = (HtmlTable) page.getHtmlElementById("search:ownerBeanPageItems");
         assertEquals("Owner Firstname #1", table.getCellAt(1, 0).getTextContent());
         assertEquals("Owner Lastname #1", table.getCellAt(1, 1).getTextContent());

         page = page.getAnchorByText("Owner Firstname #1").click();
         assertTrue(page.asText().contains("View existing Owner"));
         assertEquals("Owner Firstname #1", page.getHtmlElementById("ownerBeanOwnerFirstName").getTextContent());
         assertEquals("Owner Lastname #1", page.getHtmlElementById("ownerBeanOwnerLastName").getTextContent());
         assertEquals("", page.getHtmlElementById("ownerBeanOwnerAddress").getTextContent());
         page = page.getAnchorByText("Edit").click();

         form = page.getFormByName("create");
         form.getInputByName("create:ownerBeanOwnerAddress").setValueAttribute("Owner Address #1");
         page = page.getAnchorByText("Save").click();
         assertTrue(page.asText().contains("View existing Owner"));
         assertEquals("Owner Address #1", page.getHtmlElementById("ownerBeanOwnerAddress").getTextContent());

         page = page.getAnchorByText("View All").click();
         assertTrue(page.asText().contains("Search Owner entities"));

         // Create a Pet and associate it with the Owner

         page = page.getAnchorByText("Pet").click();
         assertTrue(page.asText().contains("Search Pet entities"));
         page = page.getAnchorByText("Create New").click();

         form = page.getFormByName("create");
         form.getInputByName("create:petBeanPetName").setValueAttribute("Pet #1");
         form.getInputByName("create:petBeanPetType").setValueAttribute("2");
         form.getInputByName("create:petBeanPetSendReminders").setChecked(true);
         form.getSelectByName("create:petBeanPetOwner").setSelectedAttribute("1", true);
         page = page.getAnchorByText("Save").click();

         // Click through from the Pet to the Owner

         table = (HtmlTable) page.getHtmlElementById("search:petBeanPageItems");
         assertEquals("Pet #1", table.getCellAt(1, 0).getTextContent());
         assertEquals("2", table.getCellAt(1, 1).getTextContent());
         DomNode booleanNode = table.getCellAt(1, 2).getChildNodes().get(0).getChildNodes().get(0);

         assertEquals("span", booleanNode.getNodeName());
         assertEquals("", booleanNode.getTextContent());
         assertEquals("boolean-true", booleanNode.getAttributes().getNamedItem("class").getNodeValue());

         assertEquals("Owner firstName: Owner Firstname #1, lastName: Owner Lastname #1, address: Owner Address #1", table.getCellAt(1, 3)
                  .getTextContent());

         page = page.getAnchorByText("Pet #1").click();
         assertTrue(page.asText().contains("View existing Pet"));
         page = page.getAnchorByText("Owner firstName: Owner Firstname #1, lastName: Owner Lastname #1, address: Owner Address #1").click();
         assertTrue(page.asText().contains("View existing Owner"));

         // Create a new Owner

         page = page.getAnchorByText("Create New").click();
         form = page.getFormByName("create");
         form.getInputByName("create:ownerBeanOwnerFirstName").setValueAttribute("Owner Firstname #2");
         form.getInputByName("create:ownerBeanOwnerLastName").setValueAttribute("Owner Lastname #2");
         page = page.getAnchorByText("Save").click();

         table = (HtmlTable) page.getHtmlElementById("search:ownerBeanPageItems");
         assertEquals("Owner Firstname #1", table.getCellAt(1, 0).getTextContent());
         assertEquals("Owner Lastname #1", table.getCellAt(1, 1).getTextContent());
         assertEquals("Owner Firstname #2", table.getCellAt(2, 0).getTextContent());
         assertEquals("Owner Lastname #2", table.getCellAt(2, 1).getTextContent());

         // Search for a Pet by Owner

         page = page.getAnchorByText("Pet").click();
         assertTrue(page.asText().contains("Search Pet entities"));

         table = (HtmlTable) page.getHtmlElementById("search:petBeanPageItems");
         assertEquals("Pet #1", table.getCellAt(1, 0).getTextContent());

         form = page.getFormByName("search");
         form.getSelectByName("search:petBeanExampleOwner").setSelectedAttribute("3", true);
         page = page.getAnchorByText("Search").click();
         table = (HtmlTable) page.getHtmlElementById("search:petBeanPageItems");
         assertEquals("", table.getCellAt(1, 0).getTextContent());

         form = page.getFormByName("search");
         form.getSelectByName("search:petBeanExampleOwner").setSelectedAttribute("1", true);
         page = page.getAnchorByText("Search").click();
         page = page.getAnchorByText("Pet #1").click();

         // Delete a Pet

         assertTrue(page.asText().contains("View existing Pet"));
         page = page.getAnchorByText("Edit").click();
         assertTrue(page.asText().contains("Edit existing Pet"));
         page = page.getAnchorByText("Cancel").click();
         assertTrue(page.asText().contains("View existing Pet"));
         page = page.getAnchorByText("Edit").click();
         page = page.getAnchorByText("Delete").click();
         assertTrue(page.asText().contains("Search Pet entities"));
         table = (HtmlTable) page.getHtmlElementById("search:petBeanPageItems");
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