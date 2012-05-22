/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.forge.scaffold.faces;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.*;
import junit.framework.Assert;
import org.jboss.arquillian.protocol.servlet.arq514hack.descriptors.impl.web.Strings;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.util.Refactory;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspector;
import org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectorConfig;
import org.jboss.forge.scaffold.faces.util.AnnotationLookup;
import org.jboss.forge.shell.util.Streams;
import org.junit.Test;
import org.metawidget.util.XmlUtils;
import org.metawidget.util.simple.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import static org.junit.Assert.*;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.*;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.ForgePropertyStyle;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.ForgePropertyStyleConfig;
import org.jboss.forge.scaffold.faces.metawidget.processor.ForgeInspectionResultProcessor;
import static org.metawidget.inspector.InspectionResultConstants.*;

/**
 *
 * @author Thomas Frühbeck
 */
public class PrimaryKeyFacesScaffoldTest extends AbstractFacesScaffoldTest {

   @Inject
   private ResourceFactory factory;

   @Test
   public void testGenerateFromLegacyPrimaryKey() throws Exception {
      final String parentPrimaryKey = "parentPrimaryKey";
      final String parentPrimaryKeyCC = StringUtils.capitalize(parentPrimaryKey);

      Project project = setupScaffoldProject();

      queueInputLines("");
      generateAlternateEntity(project, "com.test.model", "Parent", parentPrimaryKey);

      getShell().execute("entity --named Child");
      getShell().execute("field string --named name");
      getShell().execute("field manyToOne --named parent --fieldType com.test.model.Parent.java --inverseFieldName children");

      queueInputLines("", "", "");
      getShell().execute("scaffold from-entity com.test.model.* --scaffoldType faces");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      // Code
      JavaResource parentBean = java.getJavaResource("com.test.view.ParentBean");
      JavaResource childBean = java.getJavaResource("com.test.view.ChildBean");

      String parentContent = Streams.toString(parentBean.getResourceInputStream());
      assertTrue(parentContent.contains("id=\" + this.parent.get" + parentPrimaryKeyCC + "()"));
      assertTrue(parentContent.contains("valueOf(((Parent) value).get" + parentPrimaryKeyCC + "()"));

      String childContent = Streams.toString(childBean.getResourceInputStream());
      assertTrue(childContent.contains("this.child.getId()"));

      // View
      FileResource<?> view = web.getWebResource("/parent/view.xhtml");
      assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());
      assertTrue(contents.contains(
              "template=\"/resources/scaffold/pageTemplate.xhtml"));

      view = web.getWebResource("/parent/search.xhtml");
      contents = Streams.toString(view.getResourceInputStream());
      assertTrue(view.exists());
      assertTrue(contents.contains("<f:param name=\"id\" value=\"#{_item." + parentPrimaryKey + "}\"/>"));

      view = web.getWebResource("/child/view.xhtml");
      contents = Streams.toString(view.getResourceInputStream());
      assertTrue(view.exists());
      assertTrue(contents.contains("childBean.child.parent." + parentPrimaryKey));
}

   private JavaResource generateAlternateEntity(Project project, String pkg, String entityName, String primaryKey) throws FileNotFoundException {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass javaClass = JavaParser.create(JavaClass.class).setPackage(pkg).setName(entityName).setPublic().addAnnotation(Entity.class).getOrigin().addInterface(Serializable.class);

      String idName = primaryKey;
      if (idName == null) {
         StringUtils.decapitalize(entityName + "Id");
      }

      Field<JavaClass> id = javaClass.addField("private String " + idName + " = null;");
      id.addAnnotation(Id.class);
      id.addAnnotation(GeneratedValue.class).setEnumValue("strategy", GenerationType.AUTO);
      id.addAnnotation(Column.class).setStringValue("name", idName).setLiteralValue("updatable", "false").setLiteralValue("nullable", "false");

      Refactory.createGetterAndSetter(javaClass, id);

      Field<JavaClass> name = javaClass.addField("private String name = null;");
      Refactory.createGetterAndSetter(javaClass, name);
      
      Refactory.createToStringFromFields(javaClass, id);
      Refactory.createHashCodeAndEquals(javaClass);

      return java.saveJavaSource(javaClass);
   }

   @Test
   public void testPrimaryKeys() throws Exception {
      Project project = initializeJavaProject();
      queueInputLines("HIBERNATE", "JBOSS_AS7", "", "");
      getShell().execute("persistence setup");

      for (PrimaryKeyTestBase testClass : new PrimaryKeyTestBase[]{
                 new PrimaryKeyFieldTest(), new PrimaryKeyPropertyTest(), new PrimaryKeyPropertyAssignedTest()}) {
         testPrimaryKey(project, testClass);
      }
   }

   public void testPrimaryKey(Project project, PrimaryKeyTestBase testClass) throws Exception {

      final String parentPrimaryKey = "primaryKey";
      final String parentPrimaryKeyCC = StringUtils.capitalize(parentPrimaryKey);

      ForgeInspectorConfig config = new ForgeInspectorConfig();
      config.setAnnotationLookup(new AnnotationLookup(project));
      config.setPropertyStyle(new ForgePropertyStyle(new ForgePropertyStyleConfig().setProject(project)));
      ForgeInspectionResultProcessor processor = new ForgeInspectionResultProcessor();

      generatePkEntity(project, "org.test", "Parent", parentPrimaryKey, testClass);
      
      String xml = new ForgeInspector(config).inspect(null, "org.test.Parent");
      xml = processor.processInspectionResult(xml, null, project, xml, new String[]{});
      
      Document document = XmlUtils.documentFromString(xml);
      assertEquals("inspection-result", document.getFirstChild().getNodeName());
      Element entity = (Element) document.getFirstChild().getFirstChild();
      assertEquals(ENTITY, entity.getNodeName());

      Map<String, String> attributes = XmlUtils.getAttributesAsMap(entity);
      assertEquals(parentPrimaryKey, attributes.get(PRIMARY_KEY));

      NodeList properties = entity.getElementsByTagName(PROPERTY);
      for (int i = 0; i < properties.getLength(); i++) {
         Element prop = (Element) properties.item(i);
         attributes = XmlUtils.getAttributesAsMap(prop);

         if (!(testClass instanceof PrimaryKeyPropertyAssignedTest)) {
            assertTrue(attributes.containsKey(PRIMARY_KEY_NOT_GENERATED));
         }
      }
      Element property = (Element) entity.getFirstChild();
      attributes = XmlUtils.getAttributesAsMap(property);
      assertEquals(parentPrimaryKey, attributes.get(PRIMARY_KEY));
      assertEquals(parentPrimaryKey, attributes.get(ENTITY_PRIMARY_KEY));
      
   }

   private JavaResource generatePkEntity(Project project, String pkg, String entityName, String primaryKey, PrimaryKeyTestBase pkTest) throws FileNotFoundException {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass javaClass = JavaParser.create(JavaClass.class).setPackage(pkg).setName(entityName).setPublic().addAnnotation(Entity.class).getOrigin().addInterface(Serializable.class);

      String idName = primaryKey;
      if (idName == null) {
         StringUtils.decapitalize(entityName + "Id");
      }

      Field<JavaClass> id = javaClass.addField("private String " + idName + " = null;");
      if (pkTest.field) {
         id.addAnnotation(Id.class);
         if (pkTest.generated) {
            id.addAnnotation(GeneratedValue.class).setEnumValue("strategy", GenerationType.AUTO);
            id.addAnnotation(Column.class).setStringValue("name", idName).setLiteralValue("updatable", "false").setLiteralValue("nullable", "false");
         }
      }



      Refactory.createGetterAndSetter(javaClass, id);
      if (!pkTest.field) {
         Method getPk = javaClass.getMethod("get" + StringUtils.capitalize(idName));
         getPk.addAnnotation(Id.class);
         if (pkTest.generated) {
            getPk.addAnnotation(GeneratedValue.class).setEnumValue("strategy", GenerationType.AUTO);
            getPk.addAnnotation(Column.class).setStringValue("name", idName).setLiteralValue("updatable", "false").setLiteralValue("nullable", "false");
         }
      }

      Field<JavaClass> name = javaClass.addField("private String name = null;");
      Refactory.createGetterAndSetter(javaClass, name);
      Field<JavaClass> type = javaClass.addField("private String type = null;");
      Refactory.createGetterAndSetter(javaClass, type);

      return java.saveJavaSource(javaClass);
   }

   
   class PrimaryKeyTestBase {

      public boolean generated, field;
   }

   class PrimaryKeyFieldTest extends PrimaryKeyTestBase {

      {
         generated = true;
         field = true;
      }
   }

   class PrimaryKeyPropertyTest extends PrimaryKeyTestBase {

      {
         generated = true;
         field = false;
      }
   }

   class PrimaryKeyPropertyAssignedTest extends PrimaryKeyTestBase {

      {
         generated = false;
         field = true;
      }
   }
}
