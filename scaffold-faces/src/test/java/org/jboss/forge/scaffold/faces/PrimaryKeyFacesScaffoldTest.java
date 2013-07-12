/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.util.Refactory;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.util.Streams;
import org.junit.Test;

/**
 * @author Thomas Fruhbeck
 */

public class PrimaryKeyFacesScaffoldTest extends AbstractFacesScaffoldTest
{
   //
   // Private statics
   //

   private static final String CRLF = "\r\n";

   //
   // Public methods
   //

   /**
    * Tests generating a scaffold from an entity that was not built by Forge.
    */

   @Test
   public void testGenerateFromLegacyEntity() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      generateLegacyEntities(project);

      getShell().execute("entity --named Child");
      getShell().execute("field string --named name");
      getShell().execute(
               "field manyToOne --named parent --fieldType com.test.model.Parent.java --inverseFieldName children");

      queueInputLines("", "", "");
      getShell().execute("scaffold from-entity com.test.model.* --scaffoldType faces");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      // Code

      JavaResource parentBean = java.getJavaResource("com.test.view.ParentBean");
      String contents = Streams.toString(parentBean.getResourceInputStream());
      assertTrue(contents.contains("id=\" + this.parent.getParentId()"));
      assertTrue(contents.contains("valueOf(((Parent) value).getParentId()"));

      JavaResource childBean = java.getJavaResource("com.test.view.ChildBean");
      contents = Streams.toString(childBean.getResourceInputStream());
      assertTrue(contents.contains("this.child.getId()"));

      // View Parent

      FileResource<?> view = web.getWebResource("/parent/view.xhtml");
      assertTrue(view.exists());
      contents = Streams.toString(view.getResourceInputStream());
      assertTrue(contents.contains(
               "template=\"/resources/scaffold/pageTemplate.xhtml"));

      // (test REVERSE_PRIMARY_KEY)

      assertTrue(contents.contains("<f:param name=\"id\" value=\"#{parentBean.parent.state.stateId}\"/>"));

      // Search Parent

      FileResource<?> search = web.getWebResource("/parent/search.xhtml");
      contents = Streams.toString(search.getResourceInputStream());
      assertTrue(search.exists());

      // (test PRIMARY_KEY)

      assertTrue(contents.contains("<f:param name=\"id\" value=\"#{_item.parentId}\"/>"));

      // (fields should not be required)

      StringBuilder metawidget = new StringBuilder();
      metawidget.append("\t\t\t\t\t\t").append("<h:outputLabel for=\"parentBeanExampleName\" value=\"Name:\"/>")
               .append(CRLF);
      metawidget.append("\t\t\t\t\t\t").append("<h:panelGroup>").append(CRLF);
      metawidget.append("\t\t\t\t\t\t\t")
               .append("<h:inputText id=\"parentBeanExampleName\" value=\"#{parentBean.example.name}\"/>").append(CRLF);
      metawidget.append("\t\t\t\t\t\t\t").append("<h:message for=\"parentBeanExampleName\" styleClass=\"error\"/>")
               .append(CRLF);
      metawidget.append("\t\t\t\t\t\t").append("</h:panelGroup>").append(CRLF);
      metawidget.append("\t\t\t\t\t\t").append("<h:outputText/>").append(CRLF);
      assertTrue(contents.contains(metawidget));

      metawidget = new StringBuilder();
      metawidget.append("\t\t\t\t\t\t").append("<h:outputLabel for=\"parentBeanExampleState\" value=\"State:\"/>")
               .append(CRLF);
      metawidget.append("\t\t\t\t\t\t").append("<h:panelGroup>").append(CRLF);
      metawidget
               .append("\t\t\t\t\t\t\t")
               .append("<h:selectOneMenu converter=\"#{stateBean.converter}\" id=\"parentBeanExampleState\" value=\"#{parentBean.example.state}\">")
               .append(CRLF);
      metawidget.append("\t\t\t\t\t\t\t\t").append("<f:selectItem/>").append(CRLF);
      metawidget.append("\t\t\t\t\t\t\t\t").append("<f:selectItems value=\"#{stateBean.all}\"/>").append(CRLF);
      metawidget.append("\t\t\t\t\t\t\t").append("</h:selectOneMenu>").append(CRLF);
      metawidget.append("\t\t\t\t\t\t\t").append("<h:message for=\"parentBeanExampleState\" styleClass=\"error\"/>")
               .append(CRLF);
      metawidget.append("\t\t\t\t\t\t").append("</h:panelGroup>").append(CRLF);
      assertTrue(contents.contains(metawidget));

      // Create Parent

      FileResource<?> create = web.getWebResource("/parent/create.xhtml");
      contents = Streams.toString(create.getResourceInputStream());
      assertTrue(create.exists());

      // (fields should be required)

      metawidget = new StringBuilder();
      metawidget.append("\t\t\t\t").append("<h:outputLabel for=\"parentBeanParentName\" value=\"Name:\"/>")
               .append(CRLF);
      metawidget.append("\t\t\t\t").append("<h:panelGroup>").append(CRLF);
      metawidget
               .append("\t\t\t\t\t")
               .append("<h:inputText id=\"parentBeanParentName\" required=\"true\" value=\"#{parentBean.parent.name}\"/>")
               .append(CRLF);
      metawidget.append("\t\t\t\t\t").append("<h:message for=\"parentBeanParentName\" styleClass=\"error\"/>")
               .append(CRLF);
      metawidget.append("\t\t\t\t").append("</h:panelGroup>").append(CRLF);
      metawidget.append("\t\t\t\t").append("<h:outputText value=\"*\"/>").append(CRLF);
      assertTrue(contents.contains(metawidget));

      metawidget = new StringBuilder();
      metawidget.append("\t\t\t\t").append("<h:outputLabel for=\"parentBeanParentState\" value=\"State:\"/>")
               .append(CRLF);
      metawidget.append("\t\t\t\t").append("<h:panelGroup>").append(CRLF);
      metawidget
               .append("\t\t\t\t\t")
               .append("<h:selectOneMenu converter=\"#{stateBean.converter}\" id=\"parentBeanParentState\" required=\"true\" value=\"#{parentBean.parent.state}\">")
               .append(CRLF);
      metawidget.append("\t\t\t\t\t\t").append("<f:selectItems value=\"#{stateBean.all}\"/>").append(CRLF);
      metawidget.append("\t\t\t\t\t").append("</h:selectOneMenu>").append(CRLF);
      metawidget.append("\t\t\t\t\t").append("<h:message for=\"parentBeanParentState\" styleClass=\"error\"/>")
               .append(CRLF);
      metawidget.append("\t\t\t\t").append("</h:panelGroup>").append(CRLF);
      assertTrue(contents.contains(metawidget));

      // View Child

      view = web.getWebResource("/child/view.xhtml");
      contents = Streams.toString(view.getResourceInputStream());
      assertTrue(view.exists());
      assertTrue(contents.contains("childBean.child.parent.parentId"));
   }

   //
   // Private methods
   //

   @SuppressWarnings("unchecked")
   private void generateLegacyEntities(Project project)
            throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      // Lookup

      JavaClass javaClass = JavaParser.create(JavaClass.class).setPackage("com.test.model")
               .setName("State").setPublic()
               .addAnnotation(Entity.class).getOrigin().addInterface(Serializable.class);

      // Id

      String idName = "stateId";
      Field<JavaClass> id = javaClass.addField("private String " + idName + " = null;");
      id.addAnnotation(Id.class);
      id.addAnnotation(GeneratedValue.class).setEnumValue("strategy", GenerationType.AUTO);
      Refactory.createGetterAndSetter(javaClass, id);

      // hashCode, equals and toString

      Refactory.createToStringFromFields(javaClass, id);
      Refactory.createHashCodeAndEquals(javaClass);

      java.saveJavaSource(javaClass);

      // Parent

      javaClass = JavaParser.create(JavaClass.class).setPackage("com.test.model").setName("Parent")
               .setPublic()
               .addAnnotation(Entity.class).getOrigin().addInterface(Serializable.class);

      // Id

      idName = "parentId";
      id = javaClass.addField("private String " + idName + " = null;");
      id.addAnnotation(Id.class);
      id.addAnnotation(GeneratedValue.class).setEnumValue("strategy", GenerationType.AUTO);
      id.addAnnotation(Column.class).setStringValue("name", idName).setLiteralValue("updatable", "false")
               .setLiteralValue("nullable", "false");

      Refactory.createGetterAndSetter(javaClass, id);

      // Name

      Field<JavaClass> name = javaClass.addField("private String name = null;");
      name.addAnnotation(Column.class).setLiteralValue("nullable", "false");
      Refactory.createGetterAndSetter(javaClass, name);

      // Address

      Field<JavaClass> state = javaClass.addField("private State state = null;");
      state.addAnnotation(ManyToOne.class).setLiteralValue("optional", "false");
      Refactory.createGetterAndSetter(javaClass, state);

      // hashCode, equals and toString

      Refactory.createToStringFromFields(javaClass, id);
      Refactory.createHashCodeAndEquals(javaClass);

      java.saveJavaSource(javaClass);
   }
}
