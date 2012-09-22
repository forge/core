import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.Import;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.ejb.util.JavaUtils;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CopyOfReOrgTest extends AbstractShellTest
{

   @Test
   public void simple1() throws Exception
   {
      JavaClass from = JavaParser.parse(JavaClass.class,
               "package it.coopservice.consulenti.model; " +
                        "import javax.persistence.Entity; " +
                        "import javax.persistence.Column; " +
                        "import java.io.Serializable; " +
                        "@Entity public class Responsabile implements Serializable{" +
                        " @Column  private String nome;" +
                        "public String getNome(){ return this.nome;  }" +
                        "public void setNome(final String nome) {this.cognome = cognome;} " +
                        "@Column   private String cognome;" +
                        "public String getCognome() { return this.cognome; }" +
                        "public void setCognome(final String cognome) {this.cognome = cognome; }" +
                        "}");
      reorg(from);
      // clone(from, "it.coopservice.consulenti.model.Responsabile33", true);
      // assertTrue(content.contains("<T>"));
   }

   private void reorg(JavaClass from)
   {
      System.out.println(from.toString());
      System.out.println("********************************");
      System.out.println("********************************");
      List<Method<JavaClass>> methods = from.getMethods();
      for (Method<JavaClass> met : from.getMethods())
      {
         from.removeMethod(met);
         // newJavaClass.addMethod(met.toString());
      }
      System.out.println("********************************");
      System.out.println("********************************");
      System.out.println(from.toString());
      System.out.println("********************************");
      System.out.println("********************************");
      for (Method<JavaClass> met : methods)
      {
         from.addMethod(met.toString());
      }
      System.out.println(from.toString());
   }

   private void clone(JavaClass from, String resourceName, boolean overwrite) throws Exception
   {
      JavaSourceFacet java = super.getProject().getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource(resourceName);
      JavaClass newJavaClass = null;
      if (!resource.exists() || overwrite)
      {
         if (resource.createNewFile())
         {
            JavaClass javaClass = JavaParser.create(JavaClass.class);
            String name = java.calculateName(resource);
            String packag = java.calculatePackage(resource);
            System.out.println("NAME: " + name);
            System.out.println("PACKAGE: " + packag);
            javaClass.setName(name);
            javaClass.setPackage(packag);
            newJavaClass = javaClass;
         }
         else
         {
            newJavaClass = JavaUtils.getJavaClassFrom(resource);
         }
      }
      else if (overwrite)
      {
         newJavaClass = JavaUtils.getJavaClassFrom(resource);
      }
      else
      {
         throw new RuntimeException("PackageAndName already exists ["
                  + resource.getFullyQualifiedName()
                  + "] Re-run with '--overwrite' to continue.");
      }
      // newJavaClass.setName(from.getName());
      // newJavaClass.setPackage(from.getPackage());
      if (from.getSuperType() != null && !from.getSuperType().equals("java.lang.Object"))
      {
         newJavaClass.setSuperType(from.getSuperType());
      }
      if (from.getInterfaces() != null && from.getInterfaces().size() > 0)
      {
         for (String in : from.getInterfaces())
         {
            if (in != null && !in.isEmpty())
               newJavaClass.addInterface(in);
         }
      }
      for (Import im : from.getImports())
      {
         if (!newJavaClass.getImports().contains(im))
            newJavaClass.addImport(im);
      }
      for (Annotation<JavaClass> ann : from.getAnnotations())
      {
         // System.out.println(ann.toString());
         newJavaClass.addAnnotation(ann.toString().replace("@", ""));
      }

      for (Field<JavaClass> fi : from.getFields())
      {
         newJavaClass.addField(fi.toString());
      }

      for (Method<JavaClass> met : from.getMethods())
      {
         newJavaClass.addMethod(met.toString());
      }

      String content = newJavaClass.toString();
      System.out.println(content);
      save(newJavaClass);
   }

   private void save(JavaSource<?> javaSource) throws Exception
   {
      JavaSourceFacet javaSourceFacet = super.getProject()
               .getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(javaSource);
   }

}