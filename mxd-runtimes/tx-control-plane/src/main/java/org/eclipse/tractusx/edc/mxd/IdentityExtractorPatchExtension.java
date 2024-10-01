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

package org.eclipse.tractusx.edc.mxd;

import org.eclipse.edc.iam.identitytrust.spi.DcpParticipantAgentServiceExtension;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


/**F
 * @deprecated Remove as soon as <a href="https://github.com/eclipse-tractusx/tractusx-edc/pull/1597">...</a> is merged
 */
@Deprecated
public class IdentityExtractorPatchExtension implements ServiceExtension {

    private static @NotNull DcpParticipantAgentServiceExtension noopExtractor() {
        return new DcpParticipantAgentServiceExtension() {
            @Override
            public @NotNull Map<String, String> attributesFor(ClaimToken token) {
                return Map.of();
            }
        };
    }

    @Provider
    public DcpParticipantAgentServiceExtension identityExtractor(ServiceExtensionContext context) {
        context.getMonitor().warning("The IdentityExtractorPatchExtension is deprecated and should be removed as soon as possible!");
        return noopExtractor();
    }
}
