/*
 *
 * Copyright (c) 2013 - 2018 Lijun Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xipki.ca.server.mgmt.api;

/**
 * TODO.
 * @author Lijun Liao
 * @since 2.0.0
 */

public enum CertArt {

  X509PKC(1),
  X509AC(2),
  CVC(3);

  private final int code;

  CertArt(int code) {
    this.code = code;
  }

  public int code() {
    return code;
  }

  public static CertArt forValue(int code) {
    for (CertArt value : values()) {
      if (value.code == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("invalid CertArt " + code);
  }

}
