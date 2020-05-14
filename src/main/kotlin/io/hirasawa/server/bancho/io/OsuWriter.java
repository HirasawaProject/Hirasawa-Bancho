package io.hirasawa.server.bancho.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

/**
 * Writer for osu! specific files and networking
 *
 * Based off the work from Markus Jarderot (https://stackoverflow.com/questions/28788616/parse-the-osu-binary-database-in-java)
 */
public class OsuWriter {
    private DataOutputStream writer;

    public OsuWriter(String filename) throws IOException {
        this(new FileOutputStream(filename));
    }

    public OsuWriter(OutputStream source) {
        this.writer = new DataOutputStream(source);
    }

    public void writeByte(byte data) throws IOException {
        // 1 byte
        this.writer.writeByte(data);
    }

    public void writeShort(short data) throws IOException {
        // 2 bytes, little endian
        byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array();
        writer.write(bytes);
    }

    public void writeInt(int data) throws IOException {
        // 4 bytes, little endian
        byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array();
        writer.write(bytes);
    }

    public void writeLong(long data) throws IOException {
        // 8 bytes, little endian
        byte[] bytes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(data).array();
        writer.write(bytes);
    }

    public void writeULEB128(int data) throws IOException {
        int bytesWritten = 0;

        do {
            byte groupValue = (byte) (data & 0x7F);
            data >>>= 7;
            if (data != 0) {
                groupValue |= 0x80;
            }

            writer.writeByte(data);
            bytesWritten++;
        } while (data != 0);
    }

    public void writeFloat(float data) throws IOException {
        // 4 bytes, little endian
        byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(data).array();
        writer.write(bytes);
    }

    public void writeDouble(double data) throws IOException {
        // 8 bytes little endian
        byte[] bytes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(data).array();
        writer.write(bytes);
    }

    public void writeBoolean(boolean data) throws IOException {
        // 1 byte, zero = false, non-zero = true
        writer.writeBoolean(data);
    }

    public void writeString(String data) throws IOException
    {
        if (data.isEmpty()) {
            // If empty set kind to 0 for minimal data
            writer.writeByte(0);
            return;
        }

        // Set kind to 11 if contains data
        writer.writeByte(11);

        this.writeULEB128(data.length());
        this.writer.writeUTF(data);
    }
}