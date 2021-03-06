/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.lookup.reader;

import no.difi.vefa.peppol.common.lang.EndpointNotFoundException;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.ServiceMetadata;
import no.difi.vefa.peppol.common.model.ServiceReference;
import no.difi.vefa.peppol.lookup.api.FetcherResponse;
import no.difi.vefa.peppol.lookup.api.MetadataReader;
import org.testng.annotations.Test;

import java.util.List;

import static no.difi.vefa.peppol.common.model.TransportProfile.AS2_1_0;
import static no.difi.vefa.peppol.common.model.TransportProfile.START;
import static org.testng.Assert.*;

public class MultiReaderTest {

    private MetadataReader reader = new MultiReader();

    @Test
    public void busdoxDocumentIdentifers() throws Exception {
        List<ServiceReference> result = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);
    }

    @Test
    public void bdxr201407DocumentIdentifers() throws Exception {
        List<ServiceReference> result = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/bdxr201407-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);
    }

    @Test
    public void bdxr201605DocumentIdentifers() throws Exception {
        List<ServiceReference> result = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/bdxr201605-servicegroup-9908-991825827.xml"), null));

        assertEquals(result.size(), 7);
    }

    @Test
    public void busdoxServiceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9908-991825827.xml"))).getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii05:ver2.0");

        try {
            result.getEndpoint(processIdentifier, START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        /*
        assertNotNull(result.getEndpoint(processIdentifier, AS2_1_0));

        assertEquals(
                result.getEndpoint(processIdentifier, AS2_1_0).getCertificate().getSubjectDN().toString(),
                "O=EVRY AS, CN=APP_1000000025, C=NO")
        ;
         */
    }

    @Test
    public void busdoxServiceMetadataMultiProcess() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicemetadata-9933-061828591.xml"))).getContent();

        ProcessIdentifier processIdentifier1 = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0");
        ProcessIdentifier processIdentifier2 = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii46:ver1.0");

        try {
            result.getEndpoint(processIdentifier1, START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(result.getEndpoint(processIdentifier1, AS2_1_0));

        assertEquals(
                result.getEndpoint(processIdentifier1, AS2_1_0).getCertificate().getSubjectDN().toString(),
                "O=University of Piraeus Research Center, CN=APP_1000000088, C=GR"
        );

        try {
            result.getEndpoint(processIdentifier2, START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(result.getEndpoint(processIdentifier2, AS2_1_0));

        assertEquals(
                result.getEndpoint(processIdentifier2, AS2_1_0).getCertificate().getSubjectDN().toString(),
                "O=University of Piraeus Research Center, CN=APP_1000000088, C=GR"
        );
    }

    @Test
    public void bdxrServiceMetadata() throws Exception {
        ServiceMetadata result = reader.parseServiceMetadata(
                new FetcherResponse(getClass().getResourceAsStream("/bdxr201407-servicemetadata-9908-810418052.xml")))
                .getContent();

        ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii04:ver1.0");

        try {
            result.getEndpoint(processIdentifier, START);
            fail("Expected exception.");
        } catch (EndpointNotFoundException e) {
            // Expected
        }

        assertNotNull(result.getEndpoint(processIdentifier, AS2_1_0));

        assertEquals(
                result.getEndpoint(processIdentifier, AS2_1_0).getCertificate().getSubjectDN().toString(),
                "CN=APP_1000000005, O=DIFI, C=NO"
        );
    }

    @Test
    public void busdoxServiceGroup() throws Exception {
        List<ServiceReference> serviceReferences = reader.parseServiceGroup(new FetcherResponse(
                getClass().getResourceAsStream("/busdox-servicegroup-9915-setcce-test.xml"), null));
        assertEquals(serviceReferences.size(), 1);
    }
}
