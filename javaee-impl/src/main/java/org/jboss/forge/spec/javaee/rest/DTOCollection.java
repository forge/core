package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.project.ProjectScoped;

public class DTOCollection
{

   private Map<JavaClass, DTOPair> dtos = new HashMap<JavaClass, DTOPair>();

   @Inject
   @ProjectScoped
   private Project project;

   @Inject
   private ShellPrompt prompt;

   public Collection<JavaResource> allResources()
   {
      List<JavaResource> result = new ArrayList<JavaResource>();
      for (DTOPair pair : dtos.values())
      {
         result.add(pair.rootDTO);
         result.add(pair.nestedDTO);
      }
      return result;
   }

   public void addRootDTO(JavaClass klass, JavaClass rootDTO)
   {
      JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
      DTOPair dtoPair = dtos.containsKey(klass) ? dtos.get(klass) : new DTOPair();
      if (rootDTO != null)
      {
         try
         {
            if (!java.getJavaResource(rootDTO).exists()
                     || prompt.promptBoolean("DTO [" + rootDTO.getQualifiedName() + "] already, exists. Overwrite?"))
            {
               dtoPair.rootDTO = java.saveJavaSource(rootDTO);
            }
         }
         catch (FileNotFoundException fileEx)
         {
            throw new RuntimeException(fileEx);
         }
      }
      dtos.put(klass, dtoPair);
   }

   public void addNestedDTO(JavaClass klass, JavaClass nestedDTO)
   {
      JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
      DTOPair dtoPair = dtos.containsKey(klass) ? dtos.get(klass) : new DTOPair();
      if (nestedDTO != null)
      {
         try
         {
            if (!java.getJavaResource(nestedDTO).exists()
                     || prompt.promptBoolean("DTO [" + nestedDTO.getQualifiedName() + "] already, exists. Overwrite?"))
            {
               dtoPair.nestedDTO = java.saveJavaSource(nestedDTO);
            }
         }
         catch (FileNotFoundException fileEx)
         {
            throw new RuntimeException(fileEx);
         }
      }
      dtos.put(klass, dtoPair);
   }

   public class DTOPair
   {
      private JavaResource rootDTO;
      private JavaResource nestedDTO;

      public JavaResource getRootDTO()
      {
         return rootDTO;
      }

      public JavaResource getNestedDTO()
      {
         return nestedDTO;
      }
   }

   public boolean containsDTOFor(JavaClass entity, boolean root)
   {
      if (dtos.get(entity) == null)
      {
         return false;
      }
      return (root ? (dtos.get(entity).rootDTO != null) : (dtos.get(entity).nestedDTO != null));
   }

   public JavaClass getDTOFor(JavaClass entity, boolean root)
   {
      if (dtos.get(entity) == null)
      {
         return null;
      }
      JavaResource javaResource = root ? (dtos.get(entity).rootDTO) : (dtos.get(entity).nestedDTO);
      JavaClass javaClass;
      try
      {
         javaClass = (JavaClass) javaResource.getJavaSource();
      }
      catch (FileNotFoundException fileEx)
      {
         return null;
      }
      return javaClass;
   }
}
