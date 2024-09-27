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

plugins {
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
}

configurations.all {
    resolutionStrategy {
        // Tractus-X depends on an earlier version of this, which does not yet contain ConsoleMonitor$Level#defaultLevel()
        force("org.eclipse.edc:boot-spi:0.10.0-SNAPSHOT")
    }
}

dependencies {

    runtimeOnly(libs.bundles.connector) // base runtime
    runtimeOnly(catalogLibs.api.management)
    runtimeOnly(catalogLibs.api.management.config)
    runtimeOnly(catalogLibs.controlplane.core) //default store impls, etc.
    runtimeOnly(catalogLibs.controlplane.services) // aggregate services
    runtimeOnly(catalogLibs.dsp) // protocol webhook
    runtimeOnly(catalogLibs.dcp) // DCP protocol impl
    runtimeOnly(catalogLibs.core.dcp) // DCP protocol impl
    runtimeOnly(catalogLibs.api.dsp.config) // json-ld expansion

    runtimeOnly(libs.edc.vault.hashicorp)
    runtimeOnly(catalogLibs.bundles.sql)
    runtimeOnly(catalogLibs.sts.remote.client)
    runtimeOnly(catalogLibs.bdrs.client) // audience mapper
    runtimeOnly(catalogLibs.tx.dcp) // the default scope mapper
    runtimeOnly(catalogLibs.config.trustedissuers) // to configure the trusted issuers via config
    runtimeOnly(catalogLibs.tx.fc) // file-based node directory

    runtimeOnly(libs.edc.did.core) // DidResolverRegistry, DidPublicKeyResolver
    runtimeOnly(libs.edc.did.web) // for registering the WebDidResolver
    runtimeOnly(catalogLibs.oauth2.client)
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xml")
    mergeServiceFiles()
    archiveFileName.set("${project.name}.jar")
}

edcBuild {
    publish.set(false)
}
