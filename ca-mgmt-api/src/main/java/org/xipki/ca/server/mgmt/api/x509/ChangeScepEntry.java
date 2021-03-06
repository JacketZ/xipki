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

package org.xipki.ca.server.mgmt.api.x509;

import java.util.Set;

import org.xipki.ca.api.NameId;
import org.xipki.common.util.CollectionUtil;
import org.xipki.common.util.ParamUtil;

/**
 * TODO.
 * @author Lijun Liao
 * @since 2.0.0
 */

public class ChangeScepEntry {

  private final String name;

  private NameId caIdent;

  private Boolean active;

  private String responderType;

  private String responderConf;

  private String base64Cert;

  private Set<String> certProfiles;

  private String control;

  public ChangeScepEntry(String name) {
    this.name = ParamUtil.requireNonBlank("name", name).toLowerCase();
  }

  public String name() {
    return name;
  }

  public void setCa(NameId caIdent) {
    this.caIdent = caIdent;
  }

  public NameId caIdent() {
    return caIdent;
  }

  public Boolean active() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public String responderType() {
    return responderType;
  }

  public void setResponderType(String responderType) {
    this.responderType = responderType;
  }

  public String responderConf() {
    return responderConf;
  }

  public void setResponderConf(String responderConf) {
    this.responderConf = responderConf;
  }

  public String base64Cert() {
    return base64Cert;
  }

  public void setBase64Cert(String base64Cert) {
    this.base64Cert = base64Cert;
  }

  public Set<String> certProfiles() {
    return certProfiles;
  }

  public void setCertProfiles(Set<String> certProfiles) {
    if (certProfiles == null) {
      this.certProfiles = null;
    } else {
      this.certProfiles = CollectionUtil.unmodifiableSet(
          CollectionUtil.toLowerCaseSet(certProfiles));
    }
  }

  public String control() {
    return control;
  }

  public void setControl(String control) {
    this.control = control;
  }

}
