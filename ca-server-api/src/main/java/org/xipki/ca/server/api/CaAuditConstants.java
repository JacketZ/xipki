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

package org.xipki.ca.server.api;

/**
 * TODO.
 * @author Lijun Liao
 * @since 3.0.1
 */

public class CaAuditConstants {

  public static final String APPNAME = "ca";

  public static final String MSGID_ca_routine = "ca_routine";

  public static final String MSGID_ca_mgmt = "ca_mgmt";

  public static final String NAME_ca = "ca";

  public static final String NAME_certprofile = "certprofile";

  public static final String NAME_crlNumber = "crlNumber";

  public static final String NAME_crlType = "crlType";

  public static final String NAME_expiredAt = "expiredAt";

  public static final String NAME_id = "id";

  public static final String NAME_invalidityTime = "invalidityTime";

  public static final String NAME_issuer = "issuer";

  public static final String NAME_message = "message";

  public static final String NAME_mid = "mid";

  public static final String NAME_nextUpdate = "nextUpdate";

  public static final String NAME_notAfter = "notAfter";

  public static final String NAME_notBefore = "notBefore";

  public static final String NAME_num = "num";

  public static final String NAME_PERF = "PERF";

  public static final String NAME_reason = "reason";

  public static final String NAME_reqType = "reqType";

  public static final String NAME_reqSubject = "reqSubject";

  public static final String NAME_requestor = "requestor";

  public static final String NAME_SCEP_decryption = "decryption";

  public static final String NAME_SCEP_failInfo = "failInfo";

  public static final String NAME_SCEP_failureMessage = "failureMessage";

  public static final String NAME_SCEP_messageType = "messageType";

  public static final String NAME_SCEP_name = "name";

  public static final String NAME_SCEP_pkiStatus = "pkiStatus";

  public static final String NAME_SCEP_operation = "operation";

  public static final String NAME_serial = "serial";

  public static final String NAME_thisUpdate = "thisUpdate";

  public static final String NAME_tid = "tid";

  public static final String NAME_user = "user";

  public static final String NAME_SCEP_signature = "signature";

  public static final String NAME_subject = "subject";

  // eventType
  public static final String TYPE_cleanup_crl = "cleanup_crl";

  public static final String TYPE_CMP_ccr = "ccr";

  public static final String TYPE_CMP_certConf = "certConf";

  public static final String TYPE_CMP_cr = "cr";

  public static final String TYPE_CMP_error = "error";

  public static final String TYPE_CMP_genm_cacerts = "genm_cacerts";

  public static final String TYPE_CMP_genm_cainfo = "genm_cainfo";

  public static final String TYPE_CMP_genm_crlForNumber = "genm_crl4number";

  public static final String TYPE_CMP_genm_currentCrl = "genm_currentCrl";

  public static final String TYPE_CMP_genm_genCrl = "genm_genCrl";

  public static final String TYPE_CMP_kur = "kur";

  public static final String TYPE_CMP_p10Cr = "p10Cr";

  public static final String TYPE_CMP_pkiConf = "pkiConf";

  public static final String TYPE_CMP_rr_remove = "rr_remove";

  public static final String TYPE_CMP_rr_revoke = "rr_revoke";

  public static final String TYPE_CMP_rr_unrevoke = "rr_unrevoke";

  public static final String TYPE_downlaod_crl4number = "download_crl4number";

  public static final String TYPE_download_crl = "download_crl";

  public static final String TYPE_gen_cert = "gen_cert";

  public static final String TYPE_gen_crl = "gen_crl";

  public static final String TYPE_get_systeminfo = "get_systeminfo";

  public static final String TYPE_regen_cert = "regenerate_cert";

  public static final String TYPE_unrevoke_ca = "unrevoke_ca";

  public static final String TYPE_unrevoke_cert = "unrevoke_cert";

  public static final String TYPE_remove_cert = "remove_cert";

  public static final String TYPE_remove_expiredCerts = "remove_expiredCerts";

  public static final String TYPE_revoke_ca = "revoke_ca";

  public static final String TYPE_revoke_cert = "revoke_cert";

  public static final String TYPE_revoke_suspendedCert = "revoke_suspendedCert";

}
