package no.difi.vefa.peppol.icd;

import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import no.difi.vefa.peppol.icd.api.Icd;
import no.difi.vefa.peppol.icd.code.GenericIcd;
import no.difi.vefa.peppol.icd.code.PeppolIcd;
import no.difi.vefa.peppol.icd.model.IcdIdentifier;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author erlend
 */
public class IcdsTest {

    private static final Icd ICD_TT_ORGNR = GenericIcd.of("TT:ORGNR", "9908", Scheme.of("iso6523-actorid-upis-test"));

    private static final Icd ICD_TT_TEST = GenericIcd.of("TT:TEST", "9999", Scheme.of("iso6523-actorid-upis-test"));

    private Icds icds = Icds.of(PeppolIcd.values(), new Icd[]{ICD_TT_ORGNR, ICD_TT_TEST});

    @Test
    public void simple() throws Exception {
        ParticipantIdentifier participantIdentifier = ParticipantIdentifier.of("9908:991825827");

        IcdIdentifier icdIdentifier = icds.parse(participantIdentifier.toString());

        Assert.assertEquals(icdIdentifier.getIcd(), PeppolIcd.NO_ORGNR);
        Assert.assertEquals(icdIdentifier.getIdentifier(), "991825827");

        Assert.assertEquals(icdIdentifier.toParticipantIdentifier(), participantIdentifier);
        Assert.assertEquals(icdIdentifier.toString(), participantIdentifier.toString());

        Assert.assertEquals(icds.parse("NO:ORGNR", "991825827").toParticipantIdentifier(), participantIdentifier);
    }

    @Test
    public void simpleUseOfGeneric() throws Exception {
        Assert.assertTrue(icds.findBySchemeAndCode(ICD_TT_ORGNR.getScheme(), ICD_TT_ORGNR.getCode()) == ICD_TT_ORGNR);
    }

    @Test(expectedExceptions = PeppolParsingException.class)
    public void triggerExceptionInCode() throws Exception {
        icds.parse(ParticipantIdentifier.of("0000:123456789"));
    }

    @Test(expectedExceptions = PeppolParsingException.class)
    public void triggerExceptionOnIdentifier() throws Exception {
        icds.parse("II:INVALID", "123456789");
    }

    @Test
    public void simpleGetCode() {
        Assert.assertEquals(icds.findByCode("9909"), PeppolIcd.NO_VAT);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void simpleGetCodeException() {
        icds.findByCode("0000");
    }
}
