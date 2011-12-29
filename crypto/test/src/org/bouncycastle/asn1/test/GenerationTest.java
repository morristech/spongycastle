package org.bouncycastle.asn1.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.RSAPublicKeyStructure;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V1TBSCertificateGenerator;
import org.bouncycastle.asn1.x509.V2TBSCertListGenerator;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.test.SimpleTest;

public class GenerationTest
    extends SimpleTest
{
    private byte[] v1Cert = Base64.decode(
          "MIGtAgEBMA0GCSqGSIb3DQEBBAUAMCUxCzAJBgNVBAMMAkFVMRYwFAYDVQQKDA1Cb"
        + "3VuY3kgQ2FzdGxlMB4XDTcwMDEwMTAwMDAwMVoXDTcwMDEwMTAwMDAxMlowNjELMA"
        + "kGA1UEAwwCQVUxFjAUBgNVBAoMDUJvdW5jeSBDYXN0bGUxDzANBgNVBAsMBlRlc3Q"
        + "gMTAaMA0GCSqGSIb3DQEBAQUAAwkAMAYCAQECAQI=");

    private byte[] v3Cert = Base64.decode(
          "MIIBSKADAgECAgECMA0GCSqGSIb3DQEBBAUAMCUxCzAJBgNVBAMMAkFVMRYwFAYD"
        + "VQQKDA1Cb3VuY3kgQ2FzdGxlMB4XDTcwMDEwMTAwMDAwMVoXDTcwMDEwMTAwMDAw"
        + "MlowNjELMAkGA1UEAwwCQVUxFjAUBgNVBAoMDUJvdW5jeSBDYXN0bGUxDzANBgNV"
        + "BAsMBlRlc3QgMjAYMBAGBisOBwIBATAGAgEBAgECAwQAAgEDo4GVMIGSMGEGA1Ud"
        + "IwEB/wRXMFWAFDZPdpHPzKi7o8EJokkQU2uqCHRRoTqkODA2MQswCQYDVQQDDAJB"
        + "VTEWMBQGA1UECgwNQm91bmN5IENhc3RsZTEPMA0GA1UECwwGVGVzdCAyggECMCAG"
        + "A1UdDgEB/wQWBBQ2T3aRz8you6PBCaJJEFNrqgh0UTALBgNVHQ8EBAMCBBA=");

    private byte[] v3CertNullSubject = Base64.decode(
          "MIHGoAMCAQICAQIwDQYJKoZIhvcNAQEEBQAwJTELMAkGA1UEAwwCQVUxFjAUBgNVB"
        + "AoMDUJvdW5jeSBDYXN0bGUwHhcNNzAwMTAxMDAwMDAxWhcNNzAwMTAxMDAwMDAyWj"
        + "AAMBgwEAYGKw4HAgEBMAYCAQECAQIDBAACAQOjSjBIMEYGA1UdEQEB/wQ8MDqkODA"
        + "2MQswCQYDVQQDDAJBVTEWMBQGA1UECgwNQm91bmN5IENhc3RsZTEPMA0GA1UECwwG"
        + "VGVzdCAy");

    private byte[] v2CertList = Base64.decode(
          "MIIBQwIBATANBgkqhkiG9w0BAQUFADAlMQswCQYDVQQDDAJBVTEWMBQGA1UECgwN" +
          "Qm91bmN5IENhc3RsZRcNNzAwMTAxMDAwMDAwWhcNNzAwMTAxMDAwMDAyWjAiMCAC" +
          "AQEXDTcwMDEwMTAwMDAwMVowDDAKBgNVHRUEAwoBCqCBxTCBwjBhBgNVHSMBAf8E" +
          "VzBVgBQ2T3aRz8you6PBCaJJEFNrqgh0UaE6pDgwNjELMAkGA1UEAwwCQVUxFjAU" +
          "BgNVBAoMDUJvdW5jeSBDYXN0bGUxDzANBgNVBAsMBlRlc3QgMoIBAjBDBgNVHRIE" +
          "PDA6pDgwNjELMAkGA1UEAwwCQVUxFjAUBgNVBAoMDUJvdW5jeSBDYXN0bGUxDzAN" +
          "BgNVBAsMBlRlc3QgMzAKBgNVHRQEAwIBATAMBgNVHRwBAf8EAjAA");
    
    private void tbsV1CertGen()
        throws IOException
    {
        V1TBSCertificateGenerator   gen = new V1TBSCertificateGenerator();
        Date                        startDate = new Date(1000);
        Date                        endDate = new Date(12000);

        gen.setSerialNumber(new DERInteger(1));

        gen.setStartDate(new Time(startDate));
        gen.setEndDate(new Time(endDate));

        gen.setIssuer(new X500Name("CN=AU,O=Bouncy Castle"));
        gen.setSubject(new X500Name("CN=AU,O=Bouncy Castle,OU=Test 1"));

        gen.setSignature(new AlgorithmIdentifier(PKCSObjectIdentifiers.md5WithRSAEncryption, new DERNull()));

        SubjectPublicKeyInfo    info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, new DERNull()),
                                                     new RSAPublicKeyStructure(BigInteger.valueOf(1), BigInteger.valueOf(2)));

        gen.setSubjectPublicKeyInfo(info);

        TBSCertificateStructure     tbs = gen.generateTBSCertificate();
        ByteArrayOutputStream       bOut = new ByteArrayOutputStream();
        ASN1OutputStream            aOut = new ASN1OutputStream(bOut);

        aOut.writeObject(tbs);

        if (!Arrays.areEqual(bOut.toByteArray(), v1Cert))
        {
            fail("failed v1 cert generation");
        }

        //
        // read back test
        //
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(v1Cert));
        ASN1Primitive       o = aIn.readObject();

        bOut = new ByteArrayOutputStream();
        aOut = new ASN1OutputStream(bOut);

        aOut.writeObject(o);

        if (!Arrays.areEqual(bOut.toByteArray(), v1Cert))
        {
            fail("failed v1 cert read back test");
        }
    }
    
    private AuthorityKeyIdentifier createAuthorityKeyId(
        SubjectPublicKeyInfo    info,
        X500Name                name,
        int                     sNumber)
    {
        GeneralName             genName = new GeneralName(name);
        ASN1EncodableVector     v = new ASN1EncodableVector();

        v.add(genName);

        return new AuthorityKeyIdentifier(
            info, new GeneralNames(new DERSequence(v)), BigInteger.valueOf(sNumber));
    }
    
    private void tbsV3CertGen()
        throws IOException
    {
        V3TBSCertificateGenerator   gen = new V3TBSCertificateGenerator();
        Date                        startDate = new Date(1000);
        Date                        endDate = new Date(2000);

        gen.setSerialNumber(new DERInteger(2));

        gen.setStartDate(new Time(startDate));
        gen.setEndDate(new Time(endDate));

        gen.setIssuer(new X500Name("CN=AU,O=Bouncy Castle"));
        gen.setSubject(new X500Name("CN=AU,O=Bouncy Castle,OU=Test 2"));

        gen.setSignature(new AlgorithmIdentifier(PKCSObjectIdentifiers.md5WithRSAEncryption, new DERNull()));

        SubjectPublicKeyInfo    info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalParameter(BigInteger.valueOf(1), BigInteger.valueOf(2))), new DERInteger(3));

        gen.setSubjectPublicKeyInfo(info);

        //
        // add extensions
        //
        Vector          order = new Vector();
        Hashtable       extensions = new Hashtable();

        order.addElement(X509Extension.authorityKeyIdentifier);
        order.addElement(X509Extension.subjectKeyIdentifier);
        order.addElement(X509Extension.keyUsage);

        extensions.put(X509Extension.authorityKeyIdentifier, new X509Extension(true, new DEROctetString(createAuthorityKeyId(info, new X500Name("CN=AU,O=Bouncy Castle,OU=Test 2"), 2))));
        extensions.put(X509Extension.subjectKeyIdentifier, new X509Extension(true, new DEROctetString(new SubjectKeyIdentifier(info))));
        extensions.put(X509Extension.keyUsage, new X509Extension(false, new DEROctetString(new KeyUsage(KeyUsage.dataEncipherment))));

        X509Extensions  ex = new X509Extensions(order, extensions);

        gen.setExtensions(ex);

        TBSCertificateStructure     tbs = gen.generateTBSCertificate();
        ByteArrayOutputStream       bOut = new ByteArrayOutputStream();
        ASN1OutputStream            aOut = new ASN1OutputStream(bOut);

        aOut.writeObject(tbs);

        if (!Arrays.areEqual(bOut.toByteArray(), v3Cert))
        {
            fail("failed v3 cert generation");
        }

        //
        // read back test
        //
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(v3Cert));
        ASN1Primitive       o = aIn.readObject();

        bOut = new ByteArrayOutputStream();
        aOut = new ASN1OutputStream(bOut);

        aOut.writeObject(o);

        if (!Arrays.areEqual(bOut.toByteArray(), v3Cert))
        {
            fail("failed v3 cert read back test");
        }
    }

    private void tbsV3CertGenWithNullSubject()
        throws IOException
    {
        V3TBSCertificateGenerator   gen = new V3TBSCertificateGenerator();
        Date                        startDate = new Date(1000);
        Date                        endDate = new Date(2000);

        gen.setSerialNumber(new DERInteger(2));

        gen.setStartDate(new Time(startDate));
        gen.setEndDate(new Time(endDate));

        gen.setIssuer(new X500Name("CN=AU,O=Bouncy Castle"));

        gen.setSignature(new AlgorithmIdentifier(PKCSObjectIdentifiers.md5WithRSAEncryption, new DERNull()));

        SubjectPublicKeyInfo    info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalParameter(BigInteger.valueOf(1), BigInteger.valueOf(2))), new DERInteger(3));

        gen.setSubjectPublicKeyInfo(info);

        try
        {
            gen.generateTBSCertificate();
            fail("null subject not caught!");
        }
        catch (IllegalStateException e)
        {
            if (!e.getMessage().equals("not all mandatory fields set in V3 TBScertificate generator"))
            {
                fail("unexpected exception", e);
            }
        }

        //
        // add extensions
        //
        Vector          order = new Vector();
        Hashtable       extensions = new Hashtable();

        order.addElement(X509Extension.subjectAlternativeName);

        extensions.put(X509Extension.subjectAlternativeName, new X509Extension(true, new DEROctetString(new GeneralNames(new GeneralName(new X509Name("CN=AU,O=Bouncy Castle,OU=Test 2"))))));

        X509Extensions  ex = new X509Extensions(order, extensions);

        gen.setExtensions(ex);

        TBSCertificateStructure     tbs = gen.generateTBSCertificate();
        ByteArrayOutputStream       bOut = new ByteArrayOutputStream();
        ASN1OutputStream            aOut = new ASN1OutputStream(bOut);

        aOut.writeObject(tbs);

        if (!Arrays.areEqual(bOut.toByteArray(), v3CertNullSubject))
        {
            fail("failed v3 null sub cert generation");
        }

        //
        // read back test
        //
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(v3CertNullSubject));
        ASN1Primitive       o = aIn.readObject();

        bOut = new ByteArrayOutputStream();
        aOut = new ASN1OutputStream(bOut);

        aOut.writeObject(o);

        if (!Arrays.areEqual(bOut.toByteArray(), v3CertNullSubject))
        {
            fail("failed v3 null sub cert read back test");
        }
    }

    private void tbsV2CertListGen()
        throws IOException
    {
        V2TBSCertListGenerator  gen = new V2TBSCertListGenerator();

        gen.setIssuer(new X500Name("CN=AU,O=Bouncy Castle"));

        gen.addCRLEntry(new ASN1Integer(1), new Time(new Date(1000)), CRLReason.aACompromise);

        gen.setNextUpdate(new Time(new Date(2000)));

        gen.setThisUpdate(new Time(new Date(500)));

        gen.setSignature(new AlgorithmIdentifier(PKCSObjectIdentifiers.sha1WithRSAEncryption, new DERNull()));

        //
        // extensions
        //
        Vector                  order = new Vector();
        Hashtable               extensions = new Hashtable();
        SubjectPublicKeyInfo    info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalParameter(BigInteger.valueOf(1), BigInteger.valueOf(2))), new DERInteger(3));

        order.addElement(X509Extension.authorityKeyIdentifier);
        order.addElement(X509Extension.issuerAlternativeName);
        order.addElement(X509Extension.cRLNumber);
        order.addElement(X509Extension.issuingDistributionPoint);

        extensions.put(X509Extension.authorityKeyIdentifier, new X509Extension(true, new DEROctetString(createAuthorityKeyId(info, new X500Name("CN=AU,O=Bouncy Castle,OU=Test 2"), 2))));
        extensions.put(X509Extension.issuerAlternativeName, new X509Extension(false, new DEROctetString(new GeneralNames(new DERSequence(new GeneralName(new X500Name("CN=AU,O=Bouncy Castle,OU=Test 3")))))));
        extensions.put(X509Extension.cRLNumber, new X509Extension(false, new DEROctetString(new DERInteger(1))));
        extensions.put(X509Extension.issuingDistributionPoint, new X509Extension(true, new DEROctetString(new IssuingDistributionPoint(new DERSequence()))));

        X509Extensions          ex = new X509Extensions(order, extensions);

        gen.setExtensions(ex);

        TBSCertList                 tbs = gen.generateTBSCertList();
        ByteArrayOutputStream       bOut = new ByteArrayOutputStream();
        ASN1OutputStream            aOut = new ASN1OutputStream(bOut);

        aOut.writeObject(tbs);

        if (!Arrays.areEqual(bOut.toByteArray(), v2CertList))
        {
            System.out.println(new String(Base64.encode(bOut.toByteArray())));
            fail("failed v2 cert list generation");
        }

        //
        // read back test
        //
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(v2CertList));
        ASN1Primitive o = aIn.readObject();

        bOut = new ByteArrayOutputStream();
        aOut = new ASN1OutputStream(bOut);

        aOut.writeObject(o);

        if (!Arrays.areEqual(bOut.toByteArray(), v2CertList))
        {
            fail("failed v2 cert list read back test");
        }
    }
    
    public void performTest()
        throws Exception
    {
        tbsV1CertGen();
        tbsV3CertGen();
        tbsV3CertGenWithNullSubject();
        tbsV2CertListGen();
    }

    public String getName()
    {
        return "Generation";
    }
    
    public static void main(
        String[] args)
    {
        runTest(new GenerationTest());
    }
}
