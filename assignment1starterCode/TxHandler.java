import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;

public class TxHandler {
    UTXOPool utxoPool;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
        this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     *  (1) all outputs claimed by {@code tx} are in the current UTXO pool,
     *  (2) the signatures on each input of {@code tx} are valid,
     *  (3) no UTXO is claimed multiple times by {@code tx},
     * +(4) all of {@code tx}s output values are non-negative, and
     *  (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // IMPLEMENT THIS

        double sumOutputs = 0;
        double sumInputs = 0;
        HashSet<UTXO> doubleCheck = new HashSet();
        int index = 0;

        for(Transaction.Output txOutput : tx.getOutputs()) {
            if (txOutput.value < 0) {
                return false;
            }
            sumOutputs += txOutput.value;
        }

        for(Transaction.Input txInput : tx.getInputs()) {
            UTXO utxo = new UTXO(txInput.prevTxHash, txInput.outputIndex);

            Transaction.Output txOutput = this.utxoPool.getTxOutput(utxo);
            if (txOutput == null || doubleCheck.contains(utxo)) {
                return false;
            }
            doubleCheck.add(utxo);

            byte[] rawDataToSign = tx.getRawDataToSign(index);
            PublicKey publicKey = txOutput.address;
            if (!Crypto.verifySignature(publicKey, rawDataToSign, txInput.signature)) {
                return false;
            }
            sumInputs += txOutput.value;
            index++;
        }
        if (sumOutputs > sumInputs) {
            return false;
        }

        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
        ArrayList<Transaction> txList = new ArrayList<>();

        for(Transaction tx : possibleTxs) {
            if (!isValidTx(tx)) {
                continue;
            }
            txList.add(tx);

            for(Transaction.Input txInput : tx.getInputs()) {
                UTXO utxo = new UTXO(txInput.prevTxHash, txInput.outputIndex);
                this.utxoPool.removeUTXO(utxo);
            }

            int i = 0;
            byte[] txHash = tx.getHash();
            for(Transaction.Output txOutput : tx.getOutputs()) {
                this.utxoPool.addUTXO(new UTXO(txHash, i++), txOutput);
            }
        }
        return txList.toArray(new Transaction[txList.size()]);
    }

}
