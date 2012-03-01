package org.jboss.forge.resources.enumtype;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.resources.VirtualResource;

public class EnumConstantResource extends VirtualResource<EnumConstant<JavaEnum>>
{
   private final EnumConstant<JavaEnum> member;

   public EnumConstantResource(final Resource<?> parent, final EnumConstant<JavaEnum> member)
   {
      super(parent);
      this.member = member;
      setFlag(ResourceFlag.Leaf);
   }

   @Override
   public Resource<EnumConstant<JavaEnum>> createFrom(final EnumConstant<JavaEnum> file)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public EnumConstant<JavaEnum> getUnderlyingResourceObject()
   {
      return member;
   }

   @Override
   public String getName()
   {
      return member.getName();
   }

   @Override
   public String toString()
   {
      return member.toString();
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      // TODO Auto-generated method stub
      return false;
   }

}
