/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//httpclient/src/java/org/apache/commons/httpclient/HttpStatus.java,v 1.18 2004/05/02 11:21:13 olegk Exp $
 * $Revision: 480424 $
 * $Date: 2006-11-29 06:56:49 +0100 (Wed, 29 Nov 2006) $
 *
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.openapitools.openapidiff.core.output;

import java.util.HashMap;
import java.util.Map;

/**
 * Constants enumerating the HTTP status codes. All status codes defined in RFC1945 (HTTP/1.0,
 * RFC2616 (HTTP/1.1), and RFC2518 (WebDAV) are supported.
 */
public final class HttpStatus {

  private static final Map<Integer, String> REASON_PHRASES = new HashMap<>();

  static {
    REASON_PHRASES.put(100, "Continue");
    REASON_PHRASES.put(101, "Switching Protocols");
    REASON_PHRASES.put(102, "Processing");
    REASON_PHRASES.put(200, "OK");
    REASON_PHRASES.put(201, "Created");
    REASON_PHRASES.put(202, "Accepted");
    REASON_PHRASES.put(203, "Non Authoritative Information");
    REASON_PHRASES.put(204, "No Content");
    REASON_PHRASES.put(205, "Reset Content");
    REASON_PHRASES.put(206, "Partial Content");
    REASON_PHRASES.put(207, "Multi-Status");
    REASON_PHRASES.put(300, "Multiple Choices");
    REASON_PHRASES.put(301, "Moved Permanently");
    REASON_PHRASES.put(302, "Moved Temporarily");
    REASON_PHRASES.put(303, "See Other");
    REASON_PHRASES.put(304, "Not Modified");
    REASON_PHRASES.put(305, "Use Proxy");
    REASON_PHRASES.put(307, "Temporary Redirect");
    REASON_PHRASES.put(400, "Bad Request");
    REASON_PHRASES.put(401, "Unauthorized");
    REASON_PHRASES.put(402, "Payment Required");
    REASON_PHRASES.put(403, "Forbidden");
    REASON_PHRASES.put(404, "Not Found");
    REASON_PHRASES.put(405, "Method Not Allowed");
    REASON_PHRASES.put(406, "Not Acceptable");
    REASON_PHRASES.put(407, "Proxy Authentication Required");
    REASON_PHRASES.put(408, "Request Timeout");
    REASON_PHRASES.put(409, "Conflict");
    REASON_PHRASES.put(410, "Gone");
    REASON_PHRASES.put(411, "Length Required");
    REASON_PHRASES.put(412, "Precondition Failed");
    REASON_PHRASES.put(413, "Request Too Long");
    REASON_PHRASES.put(414, "Request-URI Too Long");
    REASON_PHRASES.put(415, "Unsupported Media Type");
    REASON_PHRASES.put(416, "Requested Range Not Satisfiable");
    REASON_PHRASES.put(417, "Expectation Failed");
    REASON_PHRASES.put(419, "Insufficient Space On Resource");
    REASON_PHRASES.put(420, "Method Failure");
    REASON_PHRASES.put(422, "Unprocessable Entity");
    REASON_PHRASES.put(423, "Locked");
    REASON_PHRASES.put(424, "Failed Dependency");
    REASON_PHRASES.put(500, "Internal Server Error");
    REASON_PHRASES.put(501, "Not Implemented");
    REASON_PHRASES.put(502, "Bad Gateway");
    REASON_PHRASES.put(503, "Service Unavailable");
    REASON_PHRASES.put(504, "Gateway Timeout");
    REASON_PHRASES.put(505, "Http Version Not Supported");
    REASON_PHRASES.put(507, "Insufficient Storage");
  }

  /**
   * Get the reason phrase for a particular status code.
   *
   * <p>This method always returns the English text as specified in the relevant RFCs and is not
   * internationalized.
   *
   * @param statusCode the numeric status code
   * @return the reason phrase associated with the given status code or null if the status code is
   *     not recognized.
   */
  public static String getReasonPhrase(int statusCode) {
    if (statusCode < 0) {
      throw new IllegalArgumentException("status code may not be negative");
    }
    return REASON_PHRASES.get(statusCode);
  }

  private HttpStatus() {}
}
