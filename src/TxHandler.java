import java.util.Arrays;

public class TxHandler {

  
	
	private UTXOPool utxopool;
    public TxHandler(UTXOPool utxoPool) {
        
    	this.utxopool = new UTXOPool(utxoPool);
    }

    
    public boolean isValidTx(Transaction tx) {
        
    	for(Transaction.Output output : tx.getOutputs()){
    		for(UTXO ut : utxopool.getAllUTXO()){
    			if(utxopool.getTxOutput(ut) == output){
    				break;
    			}
    			return false;
    		}
    	}
    	for(int i = 0;i < tx.getInputs().size();i++){
    		if(Crypto.verifySignature(tx.getOutputs().get(i).address, tx.getRawDataToSign(i), tx.getInputs().get(i).signature)){
    			
    		}
    	}
    	
    	for(int i = 0;i < tx.numInputs();i++){
    		for(int j = i+1;j < tx.numInputs();j++){
    			if(Arrays.equals(tx.getInputs().get(i).prevTxHash,tx.getInputs().get(j).prevTxHash) 
    								&&tx.getInputs().get(i).outputIndex == 
    									tx.getInputs().get(j).outputIndex){
    				return false;
    			}
    		}
    	}
    	
    	for(Transaction.Output output : tx.getOutputs()){
    		if(output.value < 0){
    			return false;
    		}
    	}
    	
    	int input_sum = 0;
    	int output_sum = 0;
    	for(Transaction.Input in: tx.getInputs()){
    		for(UTXO ut : utxopool.getAllUTXO()){
        		if(ut.getTxHash() == in.prevTxHash){
        			input_sum += utxopool.getTxOutput(ut).value;
        			break;
        		}
        	}
    	}
    	for(Transaction.Output out : tx.getOutputs()){
    		output_sum+=out.value;
    	}
    	if(input_sum < output_sum)
    		return false;
    	
    	return true;
    	
    }

    public Transaction[] handleTxs(Transaction[] possibleTxs) {
    	Transaction[] copytxs = Arrays.copyOf(possibleTxs, possibleTxs.length);
    	for(int i = 0;i < copytxs.length;i++){
    		if(!isValidTx(copytxs[i])){
    			copytxs[i] = null;
    		}
    	}
    	
    	return copytxs;
    	
    }

}
