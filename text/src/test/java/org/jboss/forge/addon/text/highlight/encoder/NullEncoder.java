package org.jboss.forge.addon.text.highlight.encoder;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.TokenType;

public class NullEncoder implements Encoder
{

   @Override
   public void textToken(String text, TokenType type)
   {
   }

   @Override
   public void beginGroup(TokenType type)
   {
   }

   @Override
   public void endGroup(TokenType type)
   {
   }

   @Override
   public void beginLine(TokenType type)
   {
   }

   @Override
   public void endLine(TokenType type)
   {
   }
}
