
package org.jboss.forge.addon.parser.java.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class JLSValidatorTests
{
   @Test
   public void testValidateSimpleClassName() throws Exception
   {
      ValidationResult result;

      result = JLSValidator.validateClassName("A");
      assertEquals(ResultType.INFO, result.getType());

      result = JLSValidator.validateClassName("AClass");
      assertEquals(ResultType.INFO, result.getType());
   }

   @Test
   public void testValidateQualifiedClassName() throws Exception
   {
      ValidationResult result;

      result = JLSValidator.validateClassName("com.acme.AClass");
      assertEquals(ResultType.INFO, result.getType());

      result = JLSValidator.validateClassName("com.acme.AClass_");
      assertEquals(ResultType.INFO, result.getType());

      result = JLSValidator.validateClassName("com.acme.AClass$");
      assertEquals(ResultType.WARNING, result.getType());
   }

   @Test
   public void testValidateInvalidClassName() throws Exception
   {
      ValidationResult result;

      result = JLSValidator.validateClassName("!");
      assertEquals(ResultType.ERROR, result.getType());

      result = JLSValidator.validateClassName("A!");
      assertEquals(ResultType.ERROR, result.getType());
   }

   @Test
   public void testValidateNullClassName() throws Exception
   {
      ValidationResult result = JLSValidator.validateClassName(null);
      assertEquals(ResultType.ERROR, result.getType());
   }

   @Test
   public void testValidateEmptyClassName() throws Exception
   {
      ValidationResult result = JLSValidator.validateClassName("");
      assertEquals(ResultType.ERROR, result.getType());
   }

   @Test
   public void testValidateSimplePackageName() throws Exception
   {
      ValidationResult result;

      result = JLSValidator.validatePackageName("a");
      assertEquals(ResultType.INFO, result.getType());

      result = JLSValidator.validatePackageName("com");
      assertEquals(ResultType.INFO, result.getType());
   }

   @Test
   public void testValidateQualifiedPackageName() throws Exception
   {
      ValidationResult result;

      result = JLSValidator.validatePackageName("com.acme");
      assertEquals(ResultType.INFO, result.getType());

      result = JLSValidator.validatePackageName("com.acme.$");
      assertEquals(ResultType.INFO, result.getType());
   }

   @Test
   public void testValidateInvalidPackageName() throws Exception
   {
      ValidationResult result;

      result = JLSValidator.validatePackageName("com.acme!");
      assertEquals(ResultType.ERROR, result.getType());

      result = JLSValidator.validatePackageName("com.acme.");
      assertEquals(ResultType.ERROR, result.getType());
   }

   @Test
   public void testValidateNullPackageName() throws Exception
   {
      ValidationResult result = JLSValidator.validatePackageName(null);
      assertEquals(ResultType.ERROR, result.getType());
   }

   @Test
   public void testValidateEmptyPackageName() throws Exception
   {
      ValidationResult result = JLSValidator.validatePackageName("");
      assertEquals(ResultType.ERROR, result.getType());
   }

   @Test
   public void testValidateSimpleVariableName() throws Exception
   {
      ValidationResult result;

      result = JLSValidator.validatePackageName("a");
      assertEquals(ResultType.INFO, result.getType());

      result = JLSValidator.validatePackageName("flag");
      assertEquals(ResultType.INFO, result.getType());
   }

   @Test
   public void testValidateInvalidVariableName() throws Exception
   {
      ValidationResult result;

      result = JLSValidator.validatePackageName("acme!");
      assertEquals(ResultType.ERROR, result.getType());

      result = JLSValidator.validatePackageName("acme.");
      assertEquals(ResultType.ERROR, result.getType());
   }

   @Test
   public void testValidateNullVariableName() throws Exception
   {
      ValidationResult result = JLSValidator.validatePackageName(null);
      assertEquals(ResultType.ERROR, result.getType());
   }

   @Test
   public void testValidateEmptyvariableName() throws Exception
   {
      ValidationResult result = JLSValidator.validatePackageName("");
      assertEquals(ResultType.ERROR, result.getType());
   }
}
