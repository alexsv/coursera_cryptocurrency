import org.junit.Assert;
import org.junit.Test;

import java.security.*;

public class TxHandlerTests {
    @Test
    public void testCrypto() throws Exception {
        final String MESSAGE = "Hello World!";

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        PublicKey publicKey = pair.getPublic();

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(pair.getPrivate());
        privateSignature.update(MESSAGE.getBytes());

        byte[] signature = privateSignature.sign();

        Assert.assertTrue(Crypto.verifySignature(publicKey, MESSAGE.getBytes(), signature));
        Assert.assertFalse(Crypto.verifySignature(publicKey, "FALSE".getBytes(), signature));
    }
}
