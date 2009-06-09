/**
 * This file is part of Jahia: An integrated WCM, DMS and Portal Solution
 * Copyright (C) 2002-2009 Jahia Solutions Group SA. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL (or any later version), you may redistribute this Program in connection
 * with Free/Libre and Open Source Software ("FLOSS") applications as described
 * in Jahia's FLOSS exception. You should have received a copy of the text
 * describing the FLOSS exception, and it is also available here:
 * http://www.jahia.com/license
 *
 * Commercial and Supported Versions of the program
 * Alternatively, commercial and supported versions of the program may be used
 * in accordance with the terms contained in a separate written agreement
 * between you and Jahia Solutions Group SA. If you are unsure which license is appropriate
 * for your use, please contact the sales department at sales@jahia.com.
 */
/* Copyright 2006 Taglab Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package org.jahia.services.security.spnego;

import org.ietf.jgss.GSSException;

/**
 * Exactly the same as {@link GSSException} except that it is a
 * {@link RuntimeException} via {@link SpnegoException}.
 * @author Martin Algesten
 */
public class GSSSpnegoException extends SpnegoException {

  /**
   * Generated by Eclipse.
   */
  private static final long serialVersionUID = -6914929877480458931L;

  /**
   * Constructs GSSSpnegoException.
   * @param cause the GSSException.
   */
  public GSSSpnegoException(GSSException cause) {
    super(cause);
  }

  /**
   * @see org.ietf.jgss.GSSException#getMajor()
   * @return the mechanism error code.
   */
  public int getMajor() {
    return ((GSSException) getCause()).getMajor();
  }

  /**
   * @see org.ietf.jgss.GSSException#getMajorString()
   * @return the String explanation string for the major error code.
   */
  public String getMajorString() {
    return ((GSSException) getCause()).getMajorString();
  }

  /**
   * @see org.ietf.jgss.GSSException#getMinor()
   * @return mechanism level error.
   */
  public int getMinor() {
    return ((GSSException) getCause()).getMinor();
  }

  /**
   * @see org.ietf.jgss.GSSException#getMinorString()
   * @return the String explanation or error.
   */
  public String getMinorString() {
    return ((GSSException) getCause()).getMinorString();
  }

}
