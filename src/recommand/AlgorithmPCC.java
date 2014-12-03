package recommand;

public class AlgorithmPCC implements Algorithm{
	public  double calculateSimilarity(Table table,int indexCurrent, int indexTemp)
	{	
		double score=0;
		for(int i=0;i<table.getColSize();i++)
			score+=table.getStdRateByIndex(indexCurrent, i)*table.getStdRateByIndex(indexTemp, i);
		return score;  	
	}	
}
