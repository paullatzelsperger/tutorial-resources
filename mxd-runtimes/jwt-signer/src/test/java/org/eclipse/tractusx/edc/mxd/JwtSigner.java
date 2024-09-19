/*
 *  Copyright (c) 2024 Metaform Systems, Inc.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Metaform Systems, Inc. - initial API and implementation
 *
 */

package org.eclipse.tractusx.edc.mxd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.eclipse.edc.keys.keyparsers.JwkParser;
import org.eclipse.edc.keys.keyparsers.PemParser;
import org.eclipse.edc.security.token.jwt.CryptoConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

/**
 * Use this test to regenerate the JWT VCs for BOB and ALICE using the Issuer's private key
 */
public class JwtSigner {

    private static final String VC_TEMPLATE = """
            {
                        "@context": [
                            "https://www.w3.org/2018/credentials/v1",
                            "https://w3id.org/security/suites/jws-2020/v1",
                            "https://www.w3.org/ns/did/v1",
                            {
                                "mxd-credentials": "https://w3id.org/mxd/credentials/",
                                "membership": "mxd-credentials:membership",
                                "membershipType": "mxd-credentials:membershipType",
                                "website": "mxd-credentials:website",
                                "contact": "mxd-credentials:contact",
                                "since": "mxd-credentials:since"
                            }
                        ],
                        "id": "http://org.yourdataspace.com/credentials/2347",
                        "type": [
                            "VerifiableCredential",
                            "MembershipCredential"
                        ],
                        "issuer": "did:example:dataspace-issuer",
                        "issuanceDate": "2023-08-18T00:00:00Z",
                        "credentialSubject": {
                            "id": "%s",
                            "membership": {
                                "membershipType": "FullMember",
                                "website": "www.whatever.com",
                                "contact": "fizz.buzz@whatever.com",
                                "since": "2023-01-01T00:00:00Z"
                            }
                        }
                    }
            """;
    private final ObjectMapper mapper = new ObjectMapper();

    @ParameterizedTest(name = "JWT VC for {2}")
    @ArgumentsSource(IdentifierProvider.class)
    void generateJwt(String participantId, String did, String name) throws JOSEException, IOException {

        var header = new JWSHeader.Builder(JWSAlgorithm.EdDSA)
                .keyID("did:example:dataspace-issuer#key-1")
                .type(JOSEObjectType.JWT)
                .build();


        var credential = VC_TEMPLATE.formatted(participantId);

        var claims = new JWTClaimsSet.Builder()
                .audience(did)
                .subject(did)
                .issuer("did:example:dataspace-issuer")
                .claim("vc", credential)
                .issueTime(Date.from(Instant.now()))
                .build();

        // this must be the path to the Credential issuer's private key
        var privateKey = (PrivateKey) new JwkParser(mapper, mock()).parse(readFile(System.getProperty("user.dir") + "/../../mxd/assets/issuer.key.jwk")).orElseThrow(f -> new RuntimeException(f.getFailureDetail()));

        var jwt = new SignedJWT(header, claims);
        jwt.sign(CryptoConverter.createSignerFor(privateKey));

        var filePath = "%s/../../mxd/assets/%s.membership.jwt".formatted(System.getProperty("user.dir"), name);
        Files.write(Path.of(filePath), jwt.serialize().getBytes());
    }

    @Test
    void bar() throws JOSEException {
        var okp= new OctetKeyPairGenerator(Curve.Ed25519)
                .keyID("did:example:dataspace-issuer#key-1")
                .generate();

        System.out.println("Public key: ");
        System.out.println(okp.toPublicJWK().toJSONString());
        System.out.println("Private key: ");
        System.out.println(okp.toJSONString());
    }


    private String readFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class IdentifierProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("BPNL000000000001", "did:web:alice-ih%3A7083:alice-ih%3A7084", "alice"),
                    Arguments.of("BPNL000000000002", "did:web:bob-ih%3A7083:bob", "bob")
            );
        }
    }
}
