package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Id;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.util.Refactory;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.project.ProjectScoped;

public class JpaDtoGenerator
{

   @Inject
   @ProjectScoped
   private Project project;

   public List<JavaResource> from(JavaClass entity, String dtoPackage) throws FileNotFoundException
   {
      if (entity == null)
      {
         throw new IllegalArgumentException("The argument entity was null.");
      }

      List<JavaResource> dtos = new ArrayList<JavaResource>();

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      JavaClass dtoClass = JavaParser.create(JavaClass.class)
               .setPackage(dtoPackage)
               .setName(entity.getName() + "DTO")
               .setPublic()
               .addInterface(Serializable.class);

      for (Field<?> field : entity.getFields())
      {
         if (field.hasAnnotation(Id.class))
         {
            Field<JavaClass> idfield = dtoClass.addField("private " + field.getType() + " " + field.getName() + ";");
            Refactory.createGetterAndSetter(dtoClass, idfield);
         }
      }

      dtos.add(java.saveJavaSource(dtoClass));

      return dtos;
   }

}
