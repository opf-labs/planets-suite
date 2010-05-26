/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.services.utils;

/** Utillity class for working with bytes, bytearrays, and strings. */
public class ByteString {
    /** The number of bits in a nibble (used for shifting). */
    private static final byte BITS_IN_NIBBLE = 4;
    /** A bitmask for a nibble (used for "and'ing" out the bits. */
    private static final byte BITMASK_FOR_NIBBLE = 0x0f;

    /** Utility class, don't initialise. */
    private ByteString() {
    }

    /**
     * Converts a byte array to a hexstring.
     *
     * @param ba the bytearray to be converted
     * @return ba converted to a hexstring
     */
    public static String toHex(final byte[] ba) {
        char[] hexdigit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};

        StringBuffer sb = new StringBuffer("");
        int ba_len = ba.length;

        for (int i = 0; i < ba_len; i++) {
            sb.append(hexdigit[(ba[i] >> BITS_IN_NIBBLE) & BITMASK_FOR_NIBBLE]);
            sb.append(hexdigit[ba[i] & BITMASK_FOR_NIBBLE]);
        }

        return sb.toString();
    }


}
