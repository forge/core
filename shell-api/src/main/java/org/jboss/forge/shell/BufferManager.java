package org.jboss.forge.shell;

/**
 * A BufferManager is responsible for managing a shell buffer in Forge.
 *
 * @author Mike Brock
 */
public interface BufferManager
{
   /**
    * Set the buffer into buffering-only mode. No data will be flushed to a lower-level buffer.
    */
   public void bufferOnlyMode();

   /**
    * Direct-write mode. Data will be flushed to the lower-level buffer immediately upon write.
    */
   public void directWriteMode();

   /**
    * Flush data to the lower-level buffer immediately.
    */
   public void flushBuffer();

   /**
    * Write a single byte to the buffer.
    * @param b
    */
   public void write(byte b);

   /**
    * Write a byte array to the buffer.
    * @param b
    */
   public void write(byte[] b);

   /**
    * Write a byte array to the buffer with offset
    * @param b
    * @param offset
    * @param length
    */
   public void write(byte[] b, int offset, int length);
   
   public void write(String s);
   
   public void directWrite(String s);

   /**
    * Set buffer position.
    */
   public void setBufferPosition(int row, int col);

   /**
    * Get height in lines
    * @return
    */
   public int getHeight();

   /**
    * Get width in lines
    * @return
    */
   public int getWidth();
}
