/*
 *
 * This file is part of the XiPKI project.
 * Copyright (c) 2013 - 2016 Lijun Liao
 * Author: Lijun Liao
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * THE AUTHOR LIJUN LIAO. LIJUN LIAO DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the XiPKI software without
 * disclosing the source code of your own applications.
 *
 * For more information, please contact Lijun Liao at this
 * address: lijun.liao@gmail.com
 */

package org.xipki.pki.ca.server.mgmt.api;

import java.util.List;

import org.xipki.commons.common.util.CollectionUtil;

/**
 * @author Lijun Liao
 * @since 2.0.0
 */

public class X509CAUris {
    private final List<String> cacertUris;
    private final List<String> ocspUris;
    private final List<String> crlUris;
    private final List<String> deltaCrlUris;

    public X509CAUris(
            final List<String> cacertUris,
            final List<String> ocspUris,
            final List<String> crlUris,
            final List<String> deltaCrlUris) {
        this.cacertUris = cacertUris;
        this.ocspUris = ocspUris;
        this.crlUris = crlUris;
        this.deltaCrlUris = deltaCrlUris;
    }

    public List<String> getCacertUris() {
        return CollectionUtil.unmodifiableList(cacertUris, true, true);
    }

    public List<String> getOcspUris() {
        return CollectionUtil.unmodifiableList(ocspUris, true, true);
    }

    public List<String> getCrlUris() {
        return CollectionUtil.unmodifiableList(crlUris, true, true);
    }

    public List<String> getDeltaCrlUris() {
        return CollectionUtil.unmodifiableList(deltaCrlUris, true, true);
    }

}