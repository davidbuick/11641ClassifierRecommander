package recommand;

public class RatePredictor {
	
	/*
	 * construction parameter
	 * table, dataset
	 */
	
	Table table;
	String method;
	public RatePredictor(Table table,String method)
	{
		this.table=table;
		this.method=method;
	}
	
	/*
	 * input: top K index
	 * corresponding top K similarity
	 * column index
	 * predict method, weighted or mean 
	 */
	public double predict(Integer[] KIndex,Double[] KSimilarity, int colIndex)
	{

		int K=KIndex.length;
		double totalScore=0;
		for(int j=0;j<K;j++)
			totalScore+=KSimilarity[j];
		
		double rate=0;
		for(int j=0;j<K;j++)
		{
			double tempRate=table.getRateByIndex(KIndex[j],colIndex);
			if(tempRate==0)
				tempRate=table.getAveRateByIndex(KIndex[j]);			
			
			double weight=0;
			
			if(method.equalsIgnoreCase("weighted"))
			{
				if(totalScore==0)
					weight=1.0/K;
				else		
					weight=(KSimilarity[j]/totalScore);
			}
			else if(method.equalsIgnoreCase("mean"))
				weight=1.0/K;
			else 
				weight=1.0/K;
			
			rate+=tempRate*weight;
		}
		
		return rate;
	}

}
