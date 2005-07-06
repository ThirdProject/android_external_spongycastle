package org.bouncycastle.mozilla.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.PublicKey;
import java.security.Security;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.mozilla.PublicKeyAndChallenge;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mozilla.SignedPublicKeyAndChallenge;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.test.SimpleTestResult;
import org.bouncycastle.util.test.Test;
import org.bouncycastle.util.test.TestResult;

public class SPKACTest implements Test
{
  byte[] spkac = Base64.decode(
         "MIIBOjCBpDCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEApne7ti0ibPhV8Iht"+
         "7Pws5iRckM7x4mtZYxEpeX5/IO8tDsBFdY86ewuY2f2KCca0oMWr43kdkZbPyzf4"+
         "CSV+0fZm9MJyNMywygZjoOCC+rS8kr0Ef31iHChhYsyejJnjw116Jnn96syhdHY6"+
         "lVD1rK0nn5ZkHjxU74gjoZu6BJMCAwEAARYAMA0GCSqGSIb3DQEBBAUAA4GBAKFL"+
         "g/luv0C7gMTI8ZKfFoSyi7Q7kiSQcmSj1WJgT56ouIRJO5NdvB/1n4GNik8VOAU0"+
         "NRztvGy3ZGqgbSav7lrxcNEvXH+dLbtS97s7yiaozpsOcEHqsBribpLOTRzYa8ci"+
         "CwkPmIiYqcby11diKLpd+W9RFYNme2v0rrbM2CyV");
 

  public String getName()
  {
    return "SignedPubicKeyAndChallenge";
  }

  public TestResult spkacTest(String testName, byte[] req)
  {
    SignedPublicKeyAndChallenge spkac;
    try {
      spkac = new SignedPublicKeyAndChallenge(req);
    } catch (Exception e) {
      return new SimpleTestResult(false, getName()+":Exception - "+testName+
                                         " failed decode test.");
    }

    try {
      PublicKeyAndChallenge pkac = spkac.getPublicKeyAndChallenge();
      PublicKey pubKey = spkac.getPublicKey("BC");
      DERObject obj = pkac.getDERObject();
      if (obj == null)
        return new SimpleTestResult(false, getName()+":Error - "+testName+
                                         " PKAC DERObject was null.");

      obj = spkac.getDERObject();
      if (obj == null)
        return new SimpleTestResult(false, getName()+":Error - "+testName+
                                         " SPKAC DERObject was null.");

      SubjectPublicKeyInfo spki = pkac.getSubjectPublicKeyInfo();
      if (spki == null)
        return new SimpleTestResult(false, getName()+":Error - "+testName+
                                         " SubjectPublicKeyInfo was null.");

      DERIA5String challenge = pkac.getChallenge();
      // Most cases this will be a string of length zero.
      if (challenge == null)
        return new SimpleTestResult(false, getName()+":Error - "+testName+
                                           " challenge was null.");
    
      ByteArrayInputStream    bIn = new ByteArrayInputStream(req);
      ASN1InputStream         dIn = new ASN1InputStream(bIn);


      ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
      DEROutputStream         dOut = new DEROutputStream(bOut);

      dOut.writeObject(spkac.getDERObject());

      byte[]                  bytes = bOut.toByteArray();

      if (bytes.length != req.length)
      {
        return new SimpleTestResult(false, getName() + ": " + testName + " failed length test");
      }

      for (int i = 0; i != req.length; i++)
      {
        if (bytes[i] != req[i])
        {
          return new SimpleTestResult(false, getName() + ": " + testName + " failed comparison test");
        }
      }

      if (!spkac.verify("BC"))
      {
          return new SimpleTestResult(false, getName() + ": " + testName + 
                                            " verification failed");
      }
    } catch (Exception e) {
      return new SimpleTestResult(false, getName() + ": Exception - " + 
                                         testName + " " + e.toString());
    }
    return new SimpleTestResult(true, getName()+": Okay");
  }

  public TestResult perform()
  {
    return spkacTest("spkac", spkac);
  }

  public static void main(String[] args)
  {
      Security.addProvider(new BouncyCastleProvider());
      
      Test test = new SPKACTest();
      TestResult result = test.perform();
      System.out.println(result);
  }
}
