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

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.restlet.data.Request;
import org.slf4j.Logger;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.attributes.DefaultAttributesHandler;
import org.sonatype.nexus.proxy.item.RepositoryItemUid;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.rest.AbstractArtifactViewProvider;
import org.sonatype.sisu.goodies.common.Loggers;

@Named("blackduck-hub-nexus-plugin")
@Singleton
public class BlackduckArtifactViewProvider extends AbstractArtifactViewProvider {
    final Logger logger = Loggers.getLogger(BlackduckArtifactViewProvider.class);

    private final DefaultAttributesHandler attributesHandler;

    @Inject
    public BlackduckArtifactViewProvider(final DefaultAttributesHandler attributesHandler) {
        this.attributesHandler = attributesHandler;
        this.getLogger().info("DATAAAAA");

        logger.info("Data provider");
    }

    @Override
    protected Object retrieveView(final ResourceStoreRequest resourceStoreRequest, final RepositoryItemUid repositoryItemUid, final StorageItem item, final Request request) throws IOException {
        logger.info("Retrieve view");
        return generateMetaData(item);
    }

    // @Override
    // public Object retrieveView(final ResourceStore store, final ResourceStoreRequest request, final StorageItem item, final Request req) {
    // return generateMetaData(item);
    // }

    private Object generateMetaData(final StorageItem storageItem) {
        final MetaDataResponse metaDataResponse = new MetaDataResponse();
        final HubMetaData hubMetaData = new HubMetaData();

        // final ItemAttributesHelper attHelper = new ItemAttributesHelper(attributesHandler);
        // if (attHelper.getAttributeLastScanned(storageItem) > 0) {
        // hubMetaData.setLastScanned(String.valueOf(attHelper.getAttributeLastScanned(storageItem)));
        // hubMetaData.setRiskReportUrl(attHelper.getAttributeRiskReportUrl(storageItem));
        // hubMetaData.setPolicyCheckResult(attHelper.getAttributePolicyResult(storageItem));
        // }

        hubMetaData.setScanTime("Test");
        hubMetaData.setPolicyStatus("Policy result");
        hubMetaData.setApiUrl("Google");

        logger.info("Last scanned: " + hubMetaData.getScanTime());
        logger.info("Risk report URL: " + hubMetaData.getApiUrl());
        logger.info("Policy check: " + hubMetaData.getPolicyStatus());

        // if (StringUtils.isNotBlank(hubMetaData.getLastScanned())) {
        metaDataResponse.setData(hubMetaData);
        // }

        return metaDataResponse;
    }

}
