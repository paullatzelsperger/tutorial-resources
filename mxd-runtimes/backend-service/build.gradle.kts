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

import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
    id("java")
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
}

group = "org.eclipse.tractusx.mxd.backendservice"
version = "1.0.0"

repositories {
    mavenCentral()
}
application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

dependencies {
    implementation(libs.restAssured)
    implementation(libs.edc.configuration.filesystem)
    implementation(libs.edc.ext.jsonld)
    implementation(libs.edc.http)
    implementation(libs.edc.http.lib)
    implementation(libs.edc.core.connector)
    implementation(libs.edc.sql.core)
    implementation(libs.apache.commons)
    implementation(libs.postgres)
    implementation(libs.edc.boot)
    runtimeOnly(libs.edc.api.observability)
    runtimeOnly(libs.edc.sql.transactionlocal)

    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.assertj)
    testImplementation(libs.edc.junit)
    testImplementation(libs.testcontainers.postgres)
    testImplementation(testFixtures(libs.edc.sql.core))

}
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("backend-service.jar")
}