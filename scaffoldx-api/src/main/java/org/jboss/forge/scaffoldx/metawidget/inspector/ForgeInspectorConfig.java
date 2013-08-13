package org.jboss.forge.scaffoldx.metawidget.inspector;

import org.jboss.forge.project.Project;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.util.simple.ObjectUtils;

public class ForgeInspectorConfig extends BaseObjectInspectorConfig
{

   private Project project;

   protected Project getProject()
   {
      return this.project;
   }

   public ForgeInspectorConfig setProject(Project project)
   {
      this.project = project;
      return this;
   }

   @Override
   public boolean equals(Object that)
   {
      if (this == that)
      {
         return true;
      }

      if (!ObjectUtils.nullSafeClassEquals(this, that))
      {
         return false;
      }

      if (this.project != ((ForgeInspectorConfig) that).project)
      {
         return false;
      }

      return super.equals(that);
   }

   @Override
   public int hashCode()
   {

      int hashCode = super.hashCode();
      hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.project);

      return hashCode;
   }
}
