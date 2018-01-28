import org.junit.Assert;
import org.junit.Test;

import java.security.*;

public class TxHandlerTests {

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        return generator.generateKeyPair();
    }

    @Test
    public void testCrypto() throws Exception {
        final String MESSAGE = "Hello World!";

        KeyPair pair = this.generateKeyPair();

        PublicKey publicKey = pair.getPublic();

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(pair.getPrivate());
        privateSignature.update(MESSAGE.getBytes());

        byte[] signature = privateSignature.sign();

        Assert.assertTrue(Crypto.verifySignature(publicKey, MESSAGE.getBytes(), signature));
        Assert.assertFalse(Crypto.verifySignature(publicKey, "FALSE".getBytes(), signature));
    }

    @Test
    public void testUTXOPool() {
        UTXOPool utxoPool = new UTXOPool();
        Assert.assertTrue(true);
    }

    @Test
    public void testIsValidOutputNegative() throws Exception {
        UTXOPool utxoPool = new UTXOPool();
        TxHandler txHandler = new TxHandler(utxoPool);
        Transaction tx = new Transaction();

        tx.addOutput(-1, generateKeyPair().getPublic());
        Assert.assertFalse(txHandler.isValidTx(tx));
    }


}
