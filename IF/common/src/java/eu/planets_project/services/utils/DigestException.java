/* $Id: DigestException.java,v 1.4 2007/12/04 13:22:01 mke Exp $
 * $Revision: 1.4 $
 * $Date: 2007/12/04 13:22:01 $
 * $Author: mke $
 *
 * The SB Util Library.
 * Copyright (C) 2005-2007  The State and University Library of Denmark
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.planets_project.services.utils;



/**
 * A {@link RuntimeException} thrown when there is unexpected errors
 * computing a digest.
 *
 * <p>For example when you checksum a fixed {@link String} with
 * a well known digest such as MD5, errors a re not expected.
 *
 * <p>It is also used to hide explicit code to handle {@link java.security.NoSuchAlgorithmException}s
 * in cases where you request algorithms required by the
 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/security/CryptoSpec.html">Java CryptoSpec</a>. Fx
 * {@code MD5} and {@code SHA-1}.
 */
public class DigestException extends RuntimeException {

    /** Generated ID. */
    private static final long serialVersionUID = 1921532045016030988L;

    public DigestException (String message) {
        super (message);
    }

    public DigestException (String message, Throwable t) {
        super (message, t);
    }

}
