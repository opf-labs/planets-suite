/*
 * Value.java Created on 02 July 2007, 08:28 To change this template, choose
 * Tools | Template Manager and open the template in the editor.
 */

package eu.planets_project.ifr.core.storage.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "valueType", namespace = "http://planets-project.eu/ifr/core/storage/data", propOrder = {
        "bool", "date", "dbl", "lng", "bin", "str", "type" })
public class Value implements javax.jcr.Value, Serializable {
    private static final long serialVersionUID = 6645189934720874060L;

    private boolean bool;
    private Calendar date;
    private double dbl;
    private long lng;
    private byte[] bin;
    private String str;
    private int type;

    public Value() {
        type = PropertyType.UNDEFINED;
    }

    public Value(boolean b) {
        type = PropertyType.BOOLEAN;
        bool = b;
    }

    public Value(Calendar c) {
        type = PropertyType.DATE;
        date = (Calendar) c.clone();
    }

    public Value(double d) {
        type = PropertyType.DOUBLE;
        dbl = d;
    }

    public Value(long l) {
        type = PropertyType.LONG;
        lng = l;
    }

    public Value(byte[] b) {
        type = PropertyType.BINARY;
        bin = b.clone();
    }

    public Value(InputStream is) throws IOException {
        type = PropertyType.BINARY;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) > 0) {
            baos.write(buffer, 0, len);
        }
        bin = baos.toByteArray();
    }

    public Value(String s) {
        type = PropertyType.STRING;
        str = s;
    }

    public Value(javax.jcr.Value val) throws ValueFormatException,
            IllegalStateException, RepositoryException, IOException {
        type = val.getType();
        switch (getType()) {
        case PropertyType.BINARY: {
            InputStream is = val.getStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            bin = baos.toByteArray();
        }
            break;
        case PropertyType.BOOLEAN:
            bool = val.getBoolean();
            break;
        case PropertyType.DATE:
            date = (Calendar) val.getDate().clone();
            break;
        case PropertyType.DOUBLE:
            dbl = val.getDouble();
            break;
        case PropertyType.LONG:
            lng = val.getLong();
            break;
        case PropertyType.NAME:
        case PropertyType.STRING:
            str = val.getString();
            break;
        case PropertyType.UNDEFINED:
        case PropertyType.PATH:
        case PropertyType.REFERENCE:
            System.err.println("Warning: unhandled property type "
                    + PropertyType.nameFromValue(getType()));
            break;
        }
    }

    public boolean getBool() throws IllegalStateException, ValueFormatException {
        if (getType() != PropertyType.BOOLEAN) {
            throw new ValueFormatException();
        }
        return bool;
    }

    public boolean getBoolean() throws IllegalStateException,
            ValueFormatException {
        return getBool();
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public Calendar getDate() throws IllegalStateException,
            ValueFormatException {
        if (getType() != PropertyType.DATE) {
            throw new ValueFormatException();
        }
        return (Calendar) date.clone();
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public double getDbl() throws IllegalStateException, ValueFormatException {
        if (getType() != PropertyType.DOUBLE) {
            throw new ValueFormatException();
        }
        return dbl;
    }

    public double getDouble() throws IllegalStateException,
            ValueFormatException {
        return getDbl();
    }

    public void setDbl(double dbl) {
        this.dbl = dbl;
    }

    public long getLng() throws IllegalStateException, ValueFormatException {
        if (getType() != PropertyType.LONG) {
            throw new ValueFormatException();
        }
        return lng;
    }

    public long getLong() throws IllegalStateException, ValueFormatException {
        return getLng();
    }

    public void setLng(long lng) {
        this.lng = lng;
    }

    public InputStream getStream() throws IllegalStateException,
            ValueFormatException {
        if (getType() != PropertyType.BINARY) {
            throw new ValueFormatException();
        }
        return new ByteArrayInputStream(bin);
    }

    public byte[] getBin() {
        // It's primitive and one-dimensional, so we can clone:
        return (byte[]) bin.clone();
    }

    public void setBin(byte[] bin) {
        // It's primitive and one-dimensional, so we can clone:
        this.bin = (byte[]) bin.clone();
    }

    public String getStr() throws IllegalStateException, ValueFormatException {
        if (getType() != PropertyType.STRING) {
            throw new ValueFormatException();
        }
        return str;
    }

    public String getString() throws IllegalStateException,
            ValueFormatException {
        return getStr();
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
