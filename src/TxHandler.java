import java.util.ArrayList;
import java.util.Arrays;

public class TxHandler {

  
	
	private UTXOPool utxopool;
    public TxHandler(UTXOPool utxoPool) {
        
    	this.utxopool = new UTXOPool(utxoPool);
    }

    
    public boolean isValidTx(Transaction tx) {
        
    	/** Returns true if
    	* (1) all outputs claimed by tx are in the current UTXO pool,
    	* (2) the signatures on each input of tx are valid,
    	* (3) no UTXO is claimed multiple times by tx,
    	* (4) all of txs output values are non-negative, and
    	* (5) the sum of txs input values is greater than or equal to the sum of
    	its output values; and false otherwise.
    	*/
    	
    	//(1)
    	for(int i = 0;i < tx.numInputs();i++){
    		UTXO utx = new UTXO(tx.getInput(i).prevTxHash,tx.getInput(i).outputIndex);
    		if(!utxopool.contains(utx)){
    			return false;
    		}
    	}
    	
//    	for(int i = 0 ;i < tx.numOutputs();i++){
//    		UTXO utx = new UTXO(tx.getHash(),i);
//    		utxopool.addUTXO(utx, tx.getOutputs().get(i));
//    	}
//    	
//    	for(int i = 0 ;i < tx.numOutputs();i++){
//    		UTXO utx = new UTXO(tx.getHash(),i);
//
//    		if(!utxopool.contains(utx)){
//    			return false;
//    		}
//    	}
    	
    	
//    	
//    	for(Transaction.Output output : tx.getOutputs()){
//    		for(UTXO ut : utxopool.getAllUTXO()){
//    			if(utxopool.getTxOutput(ut).value == output.value 
//    					&& utxopool.getTxOutput(ut).address == output.address){
//    				break;
//    			}
//    			return false;
//    		}
//    	}
    	
    	//(2)
    	for(int i = 0;i < tx.numInputs();i++){
    		UTXO ut = new UTXO(tx.getInput(i).prevTxHash,tx.getInput(i).outputIndex);
    		if(utxopool.getTxOutput(ut)!= null){
				 System.out.println("verifying signature");
				if (!Crypto.verifySignature(utxopool.getTxOutput(ut).address,
						tx.getRawDataToSign(i), tx.getInput(i).signature)) {
					 System.out.println("bad signature");
					return false;

				}
    		}
    		
    	}
    	
//    	for(int i = 0;i < tx.numInputs();i++){
//    		for(int j = i+1;j < tx.numInputs();j++){
//    			if(Arrays.equals(tx.getInputs().get(i).prevTxHash,tx.getInputs().get(j).prevTxHash) 
//    								&&tx.getInputs().get(i).outputIndex == 
//    									tx.getInputs().get(j).outputIndex){
//    				return false;
//    			}
//    		}
//    	}
//    	
    	//(4)
    	for(Transaction.Output output : tx.getOutputs()){
    		if(output.value < 0){
    			return false;
    		}
    	}
//    	
    	//(5)
    	int input_sum = 0;
    	int output_sum = 0;
    	boolean has_input = false;
    	for(Transaction.Input in: tx.getInputs()){
    		has_input = true;
    		UTXO utx = new UTXO(in.prevTxHash,in.outputIndex);
    		if(utxopool.getTxOutput(utx) != null)
    			input_sum += (utxopool.getTxOutput(utx)).value;
		}
    	for(Transaction.Output out : tx.getOutputs()){
    		output_sum+=out.value;
    	}
    	if(has_input && input_sum < output_sum)
    		return false;
    	
    	
    	//add unspent output to the utxopool
//    	for(int i = 0 ;i < tx.numOutputs();i++){
//			UTXO utx = new UTXO(tx.getHash(),i);
//			utxopool.addUTXO(utx, tx.getOutput(i));
//    	}
    	
    	//remove spent output
//    	for(int i = 0;i < tx.numInputs();i++){
//    		UTXO utx = new UTXO(tx.getInput(i).prevTxHash,tx.getInput(i).outputIndex);
//    		utxopool.removeUTXO(utx);
//    	}
    	
    	return true;
    	
    }

    public Transaction[] handleTxs(Transaction[] possibleTxs) {
    	Transaction[] copytxs;
    	ArrayList<Transaction> txAL = new ArrayList();
    	for(int i = 0;i < possibleTxs.length;i++){
    		if(isValidTx(possibleTxs[i])){
    			txAL.add(possibleTxs[i]);
    		}
    	}
    	copytxs = new Transaction[txAL.size()];
    	int count = 0;
    	for(Transaction t : txAL){
    		copytxs[count++] = t;
    		for(int i = 0 ;i < t.numOutputs();i++){
    			UTXO utx = new UTXO(t.getHash(),i);
    			utxopool.addUTXO(utx, t.getOutput(i));
        	}
    		//remove spent output
        	for(int i = 0;i < t.numInputs();i++){
        		UTXO utx = new UTXO(t.getInput(i).prevTxHash,t.getInput(i).outputIndex);
        		utxopool.removeUTXO(utx);
        	}
    		
    	}
    	return copytxs;
    	
    }

}
