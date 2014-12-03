package recommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class KNN {

	/*
	 * construction parameter:
	 * table, dataset
	 * algorithm to calculate KNN
	 */
	Table table;
	Algorithm algorithm;
	int K;
	
	public KNN(Table table, int K, Algorithm algorithm)
	{
		this.table=table;
		this.K=K;
		this.algorithm=algorithm;
	}
	
	/*
	 * input : rowID, index Cache, similarity Cache
	 * output :non
	 */
	public void calculateKNN(int currentID, Integer[] index, Double similarity[])
	{
		ArrayList<Similarity_Index> simi_indexList=new ArrayList<Similarity_Index>(); 

		int indexOfCurrentID=table.getIndexByRowID(currentID);
		for(int i=0;i<table.getRowSize();i++)
		{
			if(i==indexOfCurrentID)
				continue;
			

			double score =algorithm.calculateSimilarity(table,indexOfCurrentID,i);
			simi_indexList.add(new Similarity_Index(score,i));	
		}
		
		Collections.sort(simi_indexList, new ComparatorSmililarity_Index());
		
		for(int i=0;i<K;i++)
		{
			index[i]=simi_indexList.get(i).index;
			similarity[i]=simi_indexList.get(i).similarity;
		}
	}
	

	public static class ComparatorSmililarity_Index implements Comparator {

		public int compare(Object arg0, Object arg1) {
			Similarity_Index entry1 = (Similarity_Index) arg0;
			Similarity_Index entry2 = (Similarity_Index) arg1;

			//from big to small
			double flag=entry2.similarity - entry1.similarity;
			if(flag>0)
				return 1;
			else if(flag<0)
				return -1;
			else 
				return entry1.index-entry2.index;
		}

	}
}
