package recommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
public class Recommender {

	protected static Map<String, String> params = new HashMap<String, String>();
	static String usage = "Usage:  java "
			+ System.getProperty("sun.java.command") + " paramFile\n\n";

	//because the operations are taken in main without creating an instance of Recoomender, the fields should be set as static
	static int K=10; // top K 
	static HashMap<Integer,Integer[]> ID_index=new HashMap<Integer,Integer[]>(14000); // cache to store ID to top K index
	static HashMap<Integer,Double[]> ID_score=new HashMap<Integer,Double[]>(14000); // cache to store ID to top K similarity
	static Table table;  // date set, 2-D array
	static boolean user_user=false;  // whether set user-user similarity or movie-movie
	
	
	static ArrayList<Entity> testEntityList = new ArrayList<Entity>();
	
	
	static FileWriter writer;   //append the expanded query to the fbFile    
	public static void main(String[] argv) throws IOException {
		
		 //for Linux use 
/*		String trainFile = argv[0]; 
		String testFile = argv[1];
		String outputFile = argv[2];
		 
		 // If you see these outputs, it means you have successfully compiled
		 //and run the code.  Then you can remove these three lines if you want. 
		 System.out.println("Training File : " + trainFile);
		 System.out.println("Test File : " + testFile);
		 System.out.println("Output File : " + outputFile);
		  
		 // Implement your recommendation modules using trainFile and testFile. 
		 // And output the prediction scores to outputFile.
*/
	


    	Scanner scan = new Scanner(new File(argv[0]));
		String line = null;
		do {
			line = scan.nextLine();
			String[] pair = line.split("=");
			params.put(pair[0].trim(), pair[1].trim());
		} while (scan.hasNext());
		scan.close();

		String trainFile=params.get("TRAINFILE");
		String testFile=params.get("TESTFILE");
		String outputFile=params.get("OUTPUTFILE");
		
		/*
		 * read train set
		 */
		// String line;
		//Scanner scanTrainSet = new Scanner(new File(params.get("TRAINFILE")));
		 Scanner scanTrainSet = new Scanner(new File(trainFile));
		line = scanTrainSet.nextLine();// skip first line

		ArrayList<Entity> trainEntityList = new ArrayList<Entity>();
		HashSet<Integer> userSet=new HashSet<Integer>();
		HashSet<Integer> movieSet=new HashSet<Integer>();
		// insert the train set triple into entityList

		do {
			line = scanTrainSet.nextLine();
			String[] strings = line.split(",");
			int userID = Integer.parseInt(strings[1]);
			int movieID = Integer.parseInt(strings[0]);
			int score = Integer.parseInt(strings[2]);
			
			trainEntityList.add(new Entity(userID, movieID, score));
			userSet.add(userID);
			movieSet.add(movieID);
		} while (scanTrainSet.hasNext());

		// sort the entitylist by userID and movieID
		int userSize=userSet.size();
		int movieSize=movieSet.size();

		
		Integer[] userArray=new Integer[userSet.size()];
		userSet.toArray(userArray);
		int[] intUserArray=new int[userArray.length];
		for(int i=0;i<userArray.length;i++)
			intUserArray[i]=userArray[i];
		Arrays.sort(intUserArray);
		
		Integer[] movieArray=new Integer[movieSet.size()];
		movieSet.toArray(movieArray);
		int[] intMovieArray=new int[movieArray.length];
		for(int i=0;i<movieArray.length;i++)
			intMovieArray[i]=movieArray[i];
		Arrays.sort(intMovieArray);	
		
		if(user_user)
			table=Table.getInstance(userSize,movieSize,intUserArray,intMovieArray);
		else
			table=Table.getInstance(movieSize,userSize,intMovieArray,intUserArray);
		
		for(int i=0;i<trainEntityList.size();i++)
		{
			int uID=trainEntityList.get(i).userID;
			int mID=trainEntityList.get(i).movieID;
			double score=trainEntityList.get(i).score;
			if(user_user)
				table.add(uID, mID, score);
			else
				table.add(mID, uID, score);
		}
		table.formAveRateAndLength();

		
		/*
		 * Read the test set to a list
		 */
		
		Scanner scanTestSet = new Scanner(new File(testFile));
		line = scanTestSet.nextLine();// skip first line
		
		do {
			line = scanTestSet.nextLine();
			String[] strings = line.split(",");

			int userID = Integer.parseInt(strings[1]);
			int movieID = Integer.parseInt(strings[0]);

			testEntityList.add(new Entity(userID, movieID, -1));
		} while (scanTestSet.hasNext());

	
		writer = new FileWriter(outputFile, false);     
		

		/*
		 * Calculate rate for each test line
		 */
		calculate("weighted","PCC",10);
		writer.close();
	}
	
	
	/*
	 * Calculate rate for each test line
	 * input: rating method, metric, k
	 * output: expected result to standard output
	 */
	public static void calculate(String rating, String metric, int K) throws IOException
	{

		AlgorithmFactory aFactory=new AlgorithmFactory();
		Algorithm algorithm=aFactory.getAlgorithm(metric);  //decide which similarity metric to use
		KNN knn=new KNN(table,K,algorithm);  //decide K
		RatePredictor ratePredictor=new RatePredictor(table,rating); //decide which rating method to use
		
		//clear cache for each iteration
		ID_index=new HashMap<Integer,Integer[]>(14000); // cache to store ID to top K index
		ID_score=new HashMap<Integer,Double[]>(14000); // cache to store ID to top K similarity
			
				
		for (int i = 0; i < testEntityList.size(); i++) {

			double rate=0;
			Entity entity=testEntityList.get(i);
			int currentRowID=user_user? entity.userID:entity.movieID;	//row and column depends on user-user or movie-movie mode
			int currentColID=user_user? entity.movieID:entity.userID;
				
			if(!ID_index.containsKey(currentRowID))  //if the data has not been cached
			{
				Integer[] indexes=new Integer[K];
				Double[] similarity=new Double[K];

				knn.calculateKNN(currentRowID, indexes, similarity);

				ID_index.put(currentRowID, indexes); // cache
				ID_score.put(currentRowID, similarity);
			}		
			rate=ratePredictor.predict(ID_index.get(currentRowID), ID_score.get(currentRowID), table.getIndexByColID(currentColID)); //predict the rate
			//System.out.println(rate);
			writer.write(rate+"\n");  
		}
		

   
	}
}