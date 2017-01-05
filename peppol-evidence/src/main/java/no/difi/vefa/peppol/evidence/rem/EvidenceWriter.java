/*
 * Copyright 2016 Direktoratet for forvaltning og IKT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.vefa.peppol.evidence.rem;

import no.difi.vefa.peppol.common.model.Receipt;
import no.difi.vefa.peppol.evidence.jaxb.receipt.OriginalReceiptType;
import no.difi.vefa.peppol.evidence.jaxb.receipt.PeppolRemExtension;
import no.difi.vefa.peppol.evidence.jaxb.rem.*;
import no.difi.vefa.peppol.evidence.jaxb.xmldsig.DigestMethodType;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;

public class EvidenceWriter {

    private Evidence evidence;

    private REMEvidenceType remEvidence = new REMEvidenceType();

    public static void write(OutputStream outputStream, Evidence evidence) throws RemEvidenceException {
        EvidenceWriter evidenceWriter = new EvidenceWriter(evidence);
        evidenceWriter.prepare();
        evidenceWriter.write(outputStream);
    }

    public static void write(Node node, Evidence evidence) throws RemEvidenceException {
        EvidenceWriter evidenceWriter = new EvidenceWriter(evidence);
        evidenceWriter.prepare();
        evidenceWriter.write(node);
    }

    private EvidenceWriter(Evidence evidence) {
        this.evidence = evidence;
    }

    private void prepare() throws RemEvidenceException {
        // Version
        remEvidence.setVersion("2");

        // Event Code
        remEvidence.setEventCode(evidence.getEventCode().getValue());

        // Event Reason
        remEvidence.setEventReasons(new EventReasonsType());
        remEvidence.getEventReasons().getEventReason().add(RemHelper.createEventReasonType(evidence.getEventReason()));

        // Evidence Identifier
        remEvidence.setEvidenceIdentifier(evidence.getEvidenceIdentifier().getValue());

        // Event Time
        remEvidence.setEventTime(RemHelper.toXmlGregorianCalendar(evidence.getTimestamp()));

        // Sender
        remEvidence.setSenderDetails(new EntityDetailsType());
        remEvidence.getSenderDetails().getAttributedElectronicAddressOrElectronicAddress().add(RemHelper.createElectronicAddressType(evidence.getSender()));

        // Receiver
        remEvidence.setRecipientsDetails(new EntityDetailsListType());
        remEvidence.getRecipientsDetails().getEntityDetails().add(new EntityDetailsType());
        remEvidence.getRecipientsDetails().getEntityDetails().get(0).getAttributedElectronicAddressOrElectronicAddress().add(RemHelper.createElectronicAddressType(evidence.getReceiver()));

        // Sender Message Details
        MessageDetailsType messageDetailsType = new MessageDetailsType();
        messageDetailsType.setMessageSubject(evidence.getDocumentTypeIdentifier().getIdentifier());
        messageDetailsType.setUAMessageIdentifier(evidence.getMessageIdentifier().getValue());
        DigestMethodType digestMethodType = new DigestMethodType();
        digestMethodType.setAlgorithm(evidence.getDigest().getMethod().getUri());
        messageDetailsType.setDigestMethod(digestMethodType);
        messageDetailsType.setDigestValue(evidence.getDigest().getValue());
        remEvidence.setSenderMessageDetails(messageDetailsType);

        // Extensions
        remEvidence.setExtensions(new ExtensionsListType());

        // PEPPOL REM Extension
        PeppolRemExtension peppolRemExtension = new PeppolRemExtension();
        peppolRemExtension.setTransmissionProtocol(evidence.getTransportProtocol().getIdentifier());
        peppolRemExtension.setTransmissionRole(evidence.getTransmissionRole());

        for (Receipt receipt : evidence.getOriginalReceipts()) {
            OriginalReceiptType originalReceiptType = new OriginalReceiptType();
            originalReceiptType.setType(receipt.getType());
            originalReceiptType.setValue(receipt.getValue());
            peppolRemExtension.getOriginalReceipt().add(originalReceiptType);
        }

        ExtensionType extensionType = new ExtensionType();
        extensionType.getContent().add(peppolRemExtension);
        remEvidence.getExtensions().getExtension().add(extensionType);
    }

    private void write(OutputStream outputStream) throws RemEvidenceException {
        try {
            Marshaller marshaller = RemHelper.jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(evidence.getType().toJAXBElement(remEvidence), outputStream);
        } catch (JAXBException e) {
            throw new RemEvidenceException("Unable to marshal content.", e);
        }
    }

    private void write(Node node) throws RemEvidenceException {
        try {
            Marshaller marshaller = RemHelper.jaxbContext.createMarshaller();
            marshaller.marshal(evidence.getType().toJAXBElement(remEvidence), node);
        } catch (JAXBException e) {
            throw new RemEvidenceException("Unable to marshal content.", e);
        }
    }
}
