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

package org.xipki.ca.dbtool.port.ocsp;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.bouncycastle.asn1.x509.Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xipki.ca.dbtool.jaxb.ocsp.CertStoreType;
import org.xipki.ca.dbtool.jaxb.ocsp.CertStoreType.Issuers;
import org.xipki.ca.dbtool.jaxb.ocsp.IssuerType;
import org.xipki.ca.dbtool.port.DbPortFileNameIterator;
import org.xipki.ca.dbtool.port.DbPorter;
import org.xipki.ca.dbtool.xmlio.ocsp.OcspCertType;
import org.xipki.ca.dbtool.xmlio.ocsp.OcspCertsReader;
import org.xipki.common.ProcessLog;
import org.xipki.common.util.Base64;
import org.xipki.common.util.IoUtil;
import org.xipki.common.util.ParamUtil;
import org.xipki.common.util.XmlUtil;
import org.xipki.datasource.DataAccessException;
import org.xipki.datasource.DataSourceWrapper;
import org.xipki.security.util.X509Util;

/**
 * TODO.
 * @author Lijun Liao
 * @since 2.0.0
 */

class OcspCertStoreDbImporter extends AbstractOcspCertStoreDbImporter {

  private static final Logger LOG = LoggerFactory.getLogger(OcspCertStoreDbImporter.class);

  private final Unmarshaller unmarshaller;

  private final boolean resume;

  private final int numCertsPerCommit;

  OcspCertStoreDbImporter(DataSourceWrapper datasource, Unmarshaller unmarshaller, String srcDir,
      int numCertsPerCommit, boolean resume, AtomicBoolean stopMe, boolean evaluateOnly)
      throws Exception {
    super(datasource, srcDir, stopMe, evaluateOnly);

    this.unmarshaller = ParamUtil.requireNonNull("unmarshaller", unmarshaller);
    this.numCertsPerCommit = ParamUtil.requireMin("numCertsPerCommit", numCertsPerCommit, 1);
    File processLogFile = new File(baseDir, DbPorter.IMPORT_PROCESS_LOG_FILENAME);
    if (resume) {
      if (!processLogFile.exists()) {
        throw new Exception("could not process with '--resume' option");
      }
    } else {
      if (processLogFile.exists()) {
        throw new Exception("please either specify '--resume' option or delete the file "
            + processLogFile.getPath() + " first");
      }
    }
    this.resume = resume;
  }

  public void importToDb() throws Exception {
    CertStoreType certstore;
    try {
      File file = new File(baseDir + File.separator + FILENAME_OCSP_CERTSTORE);
      @SuppressWarnings("unchecked")
      JAXBElement<CertStoreType> root = (JAXBElement<CertStoreType>)
          unmarshaller.unmarshal(file);
      certstore = root.getValue();
    } catch (JAXBException ex) {
      throw XmlUtil.convert(ex);
    }

    if (certstore.getVersion() > VERSION) {
      throw new Exception("could not import CertStore greater than " + VERSION + ": "
          + certstore.getVersion());
    }

    File processLogFile = new File(baseDir, DbPorter.IMPORT_PROCESS_LOG_FILENAME);
    System.out.println("importing OCSP certstore to database");
    try {
      if (!resume) {
        dropIndexes();
        importCertHashAlgo(certstore.getCerthashAlgo());
        importIssuer(certstore.getIssuers());
      }
      importCert(certstore, processLogFile);
      recoverIndexes();
      processLogFile.delete();
    } catch (Exception ex) {
      System.err.println("could not import OCSP certstore to database");
      throw ex;
    }
    System.out.println(" imported OCSP certstore to database");
  } // method importToDB

  private void importCertHashAlgo(String certHashAlgo) throws DataAccessException {
    String sql = "UPDATE DBSCHEMA SET VALUE2=? WHERE NAME='CERTHASH_ALGO'";
    PreparedStatement ps = prepareStatement(sql);
    try {
      ps.setString(1, certHashAlgo);
      ps.executeUpdate();
      dbSchemaInfo.setVariable("CERTHASH_ALGO", certHashAlgo);
    } catch (SQLException ex) {
      System.err.println("could not import DBSCHEMA");
      throw translate(sql, ex);
    } finally {
      releaseResources(ps, null);
    }
  }

  private void importIssuer(Issuers issuers)
      throws DataAccessException, CertificateException, IOException {
    System.out.println("importing table ISSUER");
    PreparedStatement ps = prepareStatement(SQL_ADD_ISSUER);

    try {
      for (IssuerType issuer : issuers.getIssuer()) {
        importIssuer0(issuer, ps);
      }
    } finally {
      releaseResources(ps, null);
    }
    System.out.println(" imported table ISSUER");
  }

  private void importIssuer0(IssuerType issuer, PreparedStatement ps)
      throws DataAccessException, CertificateException, IOException {
    try {
      String certFilename = issuer.getCertFile();
      String b64Cert = new String(
          IoUtil.read(new File(baseDir, certFilename)));
      byte[] encodedCert = Base64.decode(b64Cert);

      Certificate cert;
      try {
        cert = Certificate.getInstance(encodedCert);
      } catch (Exception ex) {
        LOG.error("could not parse certificate of issuer {}", issuer.getId());
        LOG.debug("could not parse certificate of issuer " + issuer.getId(), ex);
        if (ex instanceof CertificateException) {
          throw (CertificateException) ex;
        } else {
          throw new CertificateException(ex.getMessage(), ex);
        }
      }

      int idx = 1;
      ps.setInt(idx++, issuer.getId());
      ps.setString(idx++, X509Util.cutX500Name(cert.getSubject(), maxX500nameLen));
      ps.setLong(idx++, cert.getTBSCertificate().getStartDate().getDate().getTime() / 1000);
      ps.setLong(idx++, cert.getTBSCertificate().getEndDate().getDate().getTime() / 1000);
      ps.setString(idx++, sha1(encodedCert));
      setBoolean(ps, idx++, issuer.isRevoked());
      setInt(ps, idx++, issuer.getRevReason());
      setLong(ps, idx++, issuer.getRevTime());
      setLong(ps, idx++, issuer.getRevInvTime());
      ps.setString(idx++, b64Cert);

      ps.execute();
    } catch (SQLException ex) {
      System.err.println("could not import issuer with id=" + issuer.getId());
      throw translate(SQL_ADD_ISSUER, ex);
    } catch (CertificateException ex) {
      System.err.println("could not import issuer with id=" + issuer.getId());
      throw ex;
    }
  } // method importIssuer0

  private void importCert(CertStoreType certstore, File processLogFile) throws Exception {
    int numProcessedBefore = 0;
    long minId = 1;
    if (processLogFile.exists()) {
      byte[] content = IoUtil.read(processLogFile);
      if (content != null && content.length > 2) {
        String str = new String(content);
        if (str.trim().equalsIgnoreCase(MSG_CERTS_FINISHED)) {
          return;
        }

        StringTokenizer st = new StringTokenizer(str, ":");
        numProcessedBefore = Integer.parseInt(st.nextToken());
        minId = Long.parseLong(st.nextToken());
        minId++;
      }
    }

    deleteCertGreatherThan(minId - 1, LOG);

    final long total = certstore.getCountCerts() - numProcessedBefore;
    final ProcessLog processLog = new ProcessLog(total);

    System.out.println(importingText() + "certificates from ID " + minId);
    processLog.printHeader();

    PreparedStatement psCert = prepareStatement(SQL_ADD_CERT);

    OcspDbEntryType type = OcspDbEntryType.CERT;

    DbPortFileNameIterator certsFileIterator = new DbPortFileNameIterator(
        baseDir + File.separator + type.dirName() + ".mf");
    try {
      while (certsFileIterator.hasNext()) {
        String certsFile = baseDir + File.separator + type.dirName() + File.separator
            + certsFileIterator.next();

        // extract the toId from the filename
        int fromIdx = certsFile.indexOf('-');
        int toIdx = certsFile.indexOf(".zip");
        if (fromIdx != -1 && toIdx != -1) {
          try {
            long toId = Long.parseLong(certsFile.substring(fromIdx + 1, toIdx));
            if (toId < minId) {
              // try next file
              continue;
            }
          } catch (Exception ex) {
            LOG.warn("invalid file name '{}', but will still be processed", certsFile);
          }
        } else {
          LOG.warn("invalid file name '{}', but will still be processed", certsFile);
        }

        try {
          long lastId = importCert0(psCert, certsFile, minId,
              processLogFile, processLog, numProcessedBefore);
          minId = lastId + 1;
        } catch (Exception ex) {
          System.err.println("\ncould not import certificates from file " + certsFile
              + ".\nplease continue with the option '--resume'");
          LOG.error("Exception", ex);
          throw ex;
        }
      } // end for
    } finally {
      releaseResources(psCert, null);
      certsFileIterator.close();
    }

    processLog.printTrailer();
    echoToFile(MSG_CERTS_FINISHED, processLogFile);
    System.out.println(importedText() + processLog.numProcessed() + " certificates");
  } // method importCert

  private long importCert0(PreparedStatement psCert, String certsZipFile, long minId,
      File processLogFile, ProcessLog processLog, int numProcessedInLastProcess)
      throws Exception {
    ZipFile zipFile = new ZipFile(new File(certsZipFile));
    ZipEntry certsXmlEntry = zipFile.getEntry("certs.xml");

    OcspCertsReader certs;
    try {
      certs = new OcspCertsReader(zipFile.getInputStream(certsXmlEntry));
    } catch (Exception ex) {
      try {
        zipFile.close();
      } catch (Exception e2) {
        LOG.error("could not close ZIP file {}: {}", certsZipFile, e2.getMessage());
        LOG.debug("could not close ZIP file " + certsZipFile, e2);
      }
      throw ex;
    }

    disableAutoCommit();

    try {
      int numEntriesInBatch = 0;
      long lastSuccessfulCertId = 0;

      while (certs.hasNext()) {
        if (stopMe.get()) {
          throw new InterruptedException("interrupted by the user");
        }

        OcspCertType cert = (OcspCertType) certs.next();

        long id = cert.id();
        if (id < minId) {
          continue;
        }

        numEntriesInBatch++;

        // cert
        try {
          int idx = 1;
          psCert.setLong(idx++, id);
          psCert.setInt(idx++, cert.iid());
          psCert.setString(idx++, cert.sn());
          psCert.setLong(idx++, cert.update());
          psCert.setLong(idx++, cert.nbefore());
          psCert.setLong(idx++, cert.nafter());
          setBoolean(psCert, idx++, cert.rev().booleanValue());
          setInt(psCert, idx++, cert.rr());
          setLong(psCert, idx++, cert.rt());
          setLong(psCert, idx++, cert.rit());
          psCert.setString(idx++, cert.profile());
          psCert.setString(idx++, cert.hash());
          psCert.setString(idx++, cert.subject());
          psCert.addBatch();
        } catch (SQLException ex) {
          throw translate(SQL_ADD_CERT, ex);
        }

        boolean isLastBlock = !certs.hasNext();

        if (numEntriesInBatch > 0
            && (numEntriesInBatch % this.numCertsPerCommit == 0 || isLastBlock)) {
          if (evaulateOnly) {
            psCert.clearBatch();
          } else {
            try {
              psCert.executeBatch();
              commit("(commit import cert to OCSP)");
            } catch (Throwable th) {
              rollback();
              deleteCertGreatherThan(lastSuccessfulCertId, LOG);
              if (th instanceof SQLException) {
                throw translate(SQL_ADD_CERT, (SQLException) th);
              } else if (th instanceof Exception) {
                throw (Exception) th;
              } else {
                throw new Exception(th);
              }
            }
          }

          lastSuccessfulCertId = id;
          processLog.addNumProcessed(numEntriesInBatch);
          numEntriesInBatch = 0;
          echoToFile((numProcessedInLastProcess + processLog.numProcessed())
              + ":" + lastSuccessfulCertId, processLogFile);
          processLog.printStatus();
        }
      } // end for

      return lastSuccessfulCertId;
    } finally {
      recoverAutoCommit();
      zipFile.close();
    }
  } // method importCert0

}
