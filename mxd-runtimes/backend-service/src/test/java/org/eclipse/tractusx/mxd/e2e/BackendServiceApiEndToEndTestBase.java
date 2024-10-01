/********************************************************************************
 *  Copyright (c) 2024 SAP SE
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       SAP SE - initial API and implementation
 *
 ********************************************************************************/

package org.eclipse.tractusx.mxd.e2e;

import io.restassured.specification.RequestSpecification;
import org.eclipse.edc.junit.extensions.EdcRuntimeExtension;
import org.eclipse.edc.junit.extensions.EmbeddedRuntime;
import org.eclipse.edc.junit.extensions.RuntimeExtension;
import org.eclipse.edc.junit.extensions.RuntimePerClassExtension;

import java.net.URI;
import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class BackendServiceApiEndToEndTestBase {

    protected final RuntimeExtension runtime;

    public BackendServiceApiEndToEndTestBase(RuntimeExtension runtime) {
        this.runtime = runtime;
    }

    protected RequestSpecification baseRequest() {
        URI uri = getBaseUri();

        return given()
                .baseUri(uri.toString())
                .when();
    }

    public URI getBaseUri() {
        return URI.create("http://localhost:8080/api");
    }

}
