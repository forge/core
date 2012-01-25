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
package org.jboss.forge.scaffold.faces.scenario;

import static org.junit.Assert.*;

import org.jboss.jsfunit.api.InitialPage;
import org.jboss.jsfunit.api.JSFUnitResource;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@InitialPage("/faces/index.xhtml")
public class FacesScaffoldPetClinicClientTest
{
   @JSFUnitResource
   private JSFSession jsfSession;
   @JSFUnitResource
   private JSFClientSession client;
   @JSFUnitResource
   private JSFServerSession server;

   @Test
   public void testAll() throws Exception
   {
      HtmlPage page = (HtmlPage) this.client.getContentPage();

      try {
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
         assertEquals( "Owner Firstname #1", table.getCellAt(1, 0).getTextContent());
         assertEquals( "Owner Lastname #1", table.getCellAt(1, 1).getTextContent());

         page = page.getAnchorByText("Owner Firstname #1").click();
         assertTrue(page.asText().contains("View existing Owner"));
         assertEquals("Owner Firstname #1",page.getHtmlElementById("ownerBeanOwnerFirstName").getTextContent());
         assertEquals("Owner Lastname #1",page.getHtmlElementById("ownerBeanOwnerLastName").getTextContent());
         assertEquals("",page.getHtmlElementById("ownerBeanOwnerAddress").getTextContent());
         page = page.getAnchorByText("Edit").click();

         form = page.getFormByName("create");
         form.getInputByName("create:ownerBeanOwnerAddress").setValueAttribute("Owner Address #1");
         page = page.getAnchorByText("Save").click();
         assertTrue(page.asText().contains("View existing Owner"));
         assertEquals("Owner Address #1",page.getHtmlElementById("ownerBeanOwnerAddress").getTextContent());

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
         assertEquals( "Pet #1", table.getCellAt(1, 0).getTextContent());
         assertEquals( "2", table.getCellAt(1, 1).getTextContent());
         assertEquals( "true", table.getCellAt(1, 2).getTextContent());
         assertEquals( "Owner Firstname #1, Owner Lastname #1, Owner Address #1, , , , ", table.getCellAt(1, 3).getTextContent());

         page = page.getAnchorByText("Pet #1").click();
         assertTrue(page.asText().contains("View existing Pet"));
         page = page.getAnchorByText("Owner Firstname #1, Owner Lastname #1, Owner Address #1, , , ,").click();
         assertTrue(page.asText().contains("View existing Owner"));

      } catch( Throwable t ) {

         t.printStackTrace();
         throw new RuntimeException( t );

      } finally {
         System.out.println(page.asXml());
      }
   }
}