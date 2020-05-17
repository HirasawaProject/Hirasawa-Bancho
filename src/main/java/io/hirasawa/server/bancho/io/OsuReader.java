package io.hirasawa.server.bancho.io;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Reader for osu! specific files and networking
 *
 * @author Markus Jarderot (https://stackoverflow.com/questions/28788616/parse-the-osu-binary-database-in-java)
 */
public class OsuReader {
    private DataInputStream reader;

    public OsuReader(String filename) throws IOException {
        this(new FileInputStream(filename));
    }

    public OsuReader(InputStream source) {
        this.reader = new DataInputStream(source);
    }

    public byte readByte() throws IOException {
        // 1 byte
        return this.reader.readByte();
    }

    public short readShort() throws IOException {
        // 2 bytes, little endian
        byte[] bytes = new byte[2];
        this.reader.readFully(bytes);
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort();
    }

    public int readInt() throws IOException {
        // 4 bytes, little endian
        byte[] bytes = new byte[4];
        this.reader.readFully(bytes);
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    public long readLong() throws IOException {
        // 8 bytes, little endian
        byte[] bytes = new byte[8];
        this.reader.readFully(bytes);
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return bb.getLong();
    }

    public int readULEB128() throws IOException {
        // variable bytes, little endian
        // MSB says if there will be more bytes. If cleared,
        // that byte is the last.
        int value = 0;
        for (int shift = 0; shift < 32; shift += 7) {
            byte b = this.reader.readByte();
            value |= ((int) b & 0x7F) << shift;

            if (b >= 0) return value; // MSB is zero. End of value.
        }
        throw new IOException("ULEB128 too large");
    }

    public float readSingle() throws IOException {
        // 4 bytes, little endian
        byte[] bytes = new byte[4];
        this.reader.readFully(bytes);
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return bb.getFloat();
    }

    public double readDouble() throws IOException {
        // 8 bytes little endian
        byte[] bytes = new byte[8];
        this.reader.readFully(bytes);
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return bb.getDouble();
    }

    public boolean readBoolean() throws IOException {
        // 1 byte, zero = false, non-zero = true
        return this.reader.readBoolean();
    }

    public String readString() throws IOException {
        // Kind describes how to handle the length
        // 0: the string is empty
        // 11: the the string is not empty
        byte kind = this.reader.readByte();
        if (kind == 0) {
            return "";
        }
        if (kind != 11) throw new IOException("Invalid string kind");

        int length = readULEB128();
        if (length == 0) {
            return "";
        }

        byte[] byteArray = new byte[length];
        this.reader.readFully(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    public void skipBytes(int n) throws IOException {
        this.reader.skipBytes(n);
    }
}