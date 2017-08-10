/*
 * hub-nexus
 *
 * 	Copyright (C) 2017 Black Duck Software, Inc.
 * 	http://www.blackducksoftware.com/
 *
 * 	Licensed to the Apache Software Foundation (ASF) under one
 * 	or more contributor license agreements. See the NOTICE file
 * 	distributed with this work for additional information
 * 	regarding copyright ownership. The ASF licenses this file
 * 	to you under the Apache License, Version 2.0 (the
 * 	"License"); you may not use this file except in compliance
 * 	with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing,
 * 	software distributed under the License is distributed on an
 * 	"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * 	KIND, either express or implied. See the License for the
 * 	specific language governing permissions and limitations
 * 	under the License.
 */
package com.blackducksoftware.integration.hub.nexus.http;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.sonatype.sisu.goodies.common.Loggers;

import com.google.common.base.Objects;

@XmlType(name = "blackduck-info")
@XmlAccessorType(XmlAccessType.FIELD)
public class HubMetaData implements Serializable {
    final Logger logger = Loggers.getLogger(HubMetaData.class);

    private String riskReportUrl;
    private String policyCheckResult;
    private String lastScanned;

    public HubMetaData() {
        logger.info("HubMetaData");
    }

    public String getRiskReportUrl() {
        return riskReportUrl;
    }

    public void setRiskReportUrl(final String riskReportUrl) {
        this.riskReportUrl = riskReportUrl;
    }

    public String getPolicyCheckResult() {
        return policyCheckResult;
    }

    public void setPolicyCheckResult(final String policyCheckResult) {
        this.policyCheckResult = policyCheckResult;
    }

    public String getLastScanned() {
        return lastScanned;
    }

    public void setLastScanned(final String lastScanned) {
        this.lastScanned = lastScanned;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("riskReportUrl", riskReportUrl).add("policyCheckResult", policyCheckResult).add("lastScanned", lastScanned).toString();
    }
}
