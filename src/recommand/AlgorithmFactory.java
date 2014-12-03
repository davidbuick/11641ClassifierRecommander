package recommand;

public class AlgorithmFactory {

	//Factory class decide which kind of instance to be created
	public Algorithm getAlgorithm(String type)
	{
		if(type.equalsIgnoreCase("PCC"))
			return new AlgorithmPCC();
		else if(type.equalsIgnoreCase("Cosine"))
			return new AlgorithmCosine();
		else if(type.equalsIgnoreCase("DotProduct"))
			return new AlgorithmDotProduct();
		
		else 
			return null;
	}
}
