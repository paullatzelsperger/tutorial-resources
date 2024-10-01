/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.tractusx.mxd.dataservice;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.SettingContext;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebServer;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.WebServiceConfigurer;
import org.eclipse.edc.web.spi.configuration.WebServiceSettings;
import org.eclipse.tractusx.mxd.dataservice.api.DataServiceApiController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Extension(DataServiceExtension.NAME)
public class DataServiceExtension implements ServiceExtension {

    public static final String NAME = "MXD Demo Backend Services";
    private final static int DEFAULT_PORT = 8080;
    public static final String DEFAULT_PATH = "/";
    public static final String DATA_API_CONTEXT_NAME = "data";
    @SettingContext("Version API context setting key")
    private static final String DATA_API_CONFIG_KEY = "web.http.data";


    @Inject
    private WebService webService;
    @Inject
    private WebServiceConfigurer configurer;
    @Inject
    private WebServer webServer;


    public static final WebServiceSettings SETTINGS = WebServiceSettings.Builder.newInstance()
            .apiConfigKey(DATA_API_CONFIG_KEY)
            .contextAlias(DATA_API_CONTEXT_NAME)
            .defaultPath(DEFAULT_PATH)
            .defaultPort(DEFAULT_PORT)
            .useDefaultContext(false)
            .name("Data Service API")
            .build();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var config = context.getConfig(DATA_API_CONFIG_KEY);
        configurer.configure(config, webServer, SETTINGS);
        webService.registerResource(DATA_API_CONTEXT_NAME, new DataServiceApiController(new ConcurrentHashMap<>()));
    }

}
