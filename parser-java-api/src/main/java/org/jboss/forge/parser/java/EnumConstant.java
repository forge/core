package org.jboss.forge.parser.java;

public interface EnumConstant<O extends JavaSource<O>> extends Member<O, EnumConstant<O>>
{
   
   /**
    * Get this enum constant name.
    */
   String getName();
   
   /**
    * Set this enum constant name.
    */
   EnumConstant<O> setName(String name);
   
   /**
    * @return
    */
   String getType();
   
   /**
    * @return
    */
   String getQualifiedType();
   
   /**
    * @param type
    * @return
    */
   boolean isType(Class<?> type);
   
   /**
    * @param type
    * @return
    */
   boolean isType(String type);
   
   /**
    * @param clazz
    * @return
    */
   EnumConstant<O> setType(Class<?> clazz);
   
   /**
    * @param type
    * @return
    */
   EnumConstant<O> setType(String type);
   
   /**
    * @param entity
    * @return
    */
   EnumConstant<O> setType(JavaSource<?> entity);
   
   /**
    * @return
    */
   String getStringInitializer();
   
   /**
    * @return
    */
   String getLiteralInitializer();
   
   /**
    * @param value
    * @return
    */
   EnumConstant<O> setLiteralInitializer(String value);

   /**
    * @param value
    * @return
    */
   EnumConstant<O> setStringInitializer(String value);

   /* (non-Javadoc)
    * @see org.jboss.forge.parser.Internal#getInternal()
    */
   Object getInternal();

}
