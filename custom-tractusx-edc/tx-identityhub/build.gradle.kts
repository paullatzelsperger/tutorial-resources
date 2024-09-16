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

plugins {
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
}

dependencies {

    runtimeOnly(libs.bundles.identityhub)
    runtimeOnly(libs.edc.api.observability)
    runtimeOnly(libs.edc.vault.hashicorp)
    runtimeOnly(libs.bundles.sql.ih)

//    runtimeOnly(project(":extensions:superuser-seed"))

    runtimeOnly(libs.bundles.identity.api)

    implementation(libs.bundles.did)
//    implementation(project(":extensions:did-example-resolver"))
    runtimeOnly(libs.bundles.connector)
    runtimeOnly(libs.edc.ih.spi.store)
//    implementation(libs.edc.identity.vc.ldp)
    runtimeOnly(libs.edc.ih.lib.credentialquery)

    testImplementation(libs.edc.lib.crypto)
    testImplementation(libs.edc.lib.keys)

    runtimeOnly(libs.edc.sts.core)
    runtimeOnly(libs.edc.sts)
    runtimeOnly(libs.edc.sts.api)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("${project.name}.jar")
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}
