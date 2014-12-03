package recommand;

public class AlgorithmCosine implements Algorithm{
	public  double calculateSimilarity(Table table,int indexCurrent, int indexTemp)
	{	
		double score=0;
		for(int i=0;i<table.getColSize();i++)
			score+=table.getRateByIndex(indexCurrent, i)*table.getRateByIndex(indexTemp, i);

		return score/(table.getLengthByIndex(indexCurrent)*table.getLengthByIndex(indexTemp));  // cosine		
	}	
}
