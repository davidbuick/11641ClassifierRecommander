package recommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/*
 * Apply Singleton to Table:
 * hide the construction with private. 
 * make a static getInstance function and check whether the single instance has already exist
 * make a static field to store the single instance of the class
 */

public class Table {
	private HashMap<Integer, Integer> colIndex;  // map movieID to movie index
	private HashMap<Integer, Integer> rowIndex;   // map userID to user index
	private double[][] matrix;   // raw data (each row is one user rating profile)
	private double[] aveRate; // average ratings for each user
	private double[] length; // rating vector length for each user
	private double[][] stdMatrix;
	private static Table table=null;
	private Table(int row,int col)
	{
		matrix=new double[row][col];
		stdMatrix=new double[row][col];
		for(int i=0;i<row;i++)
			for(int j=0;j<col;j++)
			{
				matrix[i][j]=0;
				stdMatrix[i][j]=0;
			}
		colIndex=new HashMap<Integer, Integer>(col,1);
		rowIndex=new HashMap<Integer,Integer>(row,1);
		aveRate=new double[row];
		length=new double[row];
	}
	
	
	private Table(int row,int col,int[] rowArray, int[] colArray)
	{
		matrix=new double[row][col];
		stdMatrix=new double[row][col];
		for(int i=0;i<row;i++)
			for(int j=0;j<col;j++)
			{
				matrix[i][j]=0;
				stdMatrix[i][j]=0;
			}
		
		colIndex=new HashMap<Integer, Integer>(col,1);
		rowIndex=new HashMap<Integer,Integer>(row,1);
		
		aveRate=new double[row];
		length=new double[row];
		
		formIndex(rowArray, colArray);
	}
	
	public static Table getInstance(int row,int col,int[] rowArray, int[] colArray)
	{
		if(table==null)
			table=new Table( row, col, rowArray, colArray);
		return table;
	}
	
	
	public static Table getInstance(int row,int col)
	{
		if(table==null)
			table=new Table( row, col);
		return table;
	}
	
	
	
	
	public void formIndex(int[] rowArray, int[] lineArray)
	{

		for(int i=0;i<rowArray.length;i++)
			rowIndex.put(rowArray[i], i);
			
		for(int i=0;i<lineArray.length;i++)
			colIndex.put(lineArray[i], i);
	
	}
	
	public void formAveRateAndLength()
	{
		for(int i=0;i<aveRate.length;i++)
		{
			double sum=0;
			double length0=0;
			double nonZero=0;
			for(int j=0;j<matrix[0].length;j++)
			   if(matrix[i][j]!=0)
			   {
				   sum+=matrix[i][j];
				   length0+=matrix[i][j]*matrix[i][j];
				   nonZero++;
			   }
			aveRate[i]=sum/nonZero;
			length[i]=Math.sqrt(length0);
				
		}
		standardization();
	}
	
	public void standardization()
	{
		for(int i=0;i<getRowSize();i++)
		{
			double sum=0;
			double power=0;
			for(int j=0;j<getColSize();j++)
			{
				sum+=matrix[i][j];
				power+=matrix[i][j]*matrix[i][j];
			}
			double ave=sum/getColSize();
			double root=Math.sqrt(power);
			
			for(int j=0;j<getColSize();j++)
			{	
				stdMatrix[i][j]=(matrix[i][j]-ave)/root;
			}
			
		}
	}
	
	public int getIndexByRowID(int rowID)
	{
		return rowIndex.get(rowID);
	}
	
	public int getIndexByColID(int colID)
	{
		return colIndex.get(colID);
	}
	
	public void add(int rowID,int colID,double score)
	{
		int rowIndex=getIndexByRowID(rowID);
		int colIndex=getIndexByColID(colID);
		matrix[rowIndex][colIndex]=score;
	}
	
	public double getRateByID(int rowID,int colID)
	{
		int rowIndex=getIndexByRowID(rowID);
		int colIndex=getIndexByColID(colID);
		return matrix[rowIndex][colIndex];
	}
	
	public double getRateByIndex(int rIndex,int cIndex)
	{
		return matrix[rIndex][cIndex];
	}
	
	public double getStdRateByIndex(int rIndex,int cIndex)
	{
		return stdMatrix[rIndex][cIndex];
	}
	
	
	public double getLengthByIndex(int index)
	{
		return length[index];
	}
	public double getLengthByID(int rID)
	{
		int index=rowIndex.get(rID);
		return length[index];
	}
	
	
	
	public double getAveRateByIndex(int index)
	{
		return aveRate[index];
	}
	public double getAveRateByID(int rID)
	{
		int index=rowIndex.get(rID);
		return aveRate[index];
	}

	
	public int getSize()
	{
		int count=0;
		for(int i=0;i<matrix.length;i++)
			for(int j=0;j<matrix[0].length;j++)
			{
				if(matrix[i][j]!=0)
					count++;
			}
		return count;
	}
	
	public int getRowSize()
	{
		return matrix.length;
	}
	
	public int getColSize()
	{
		return matrix[0].length;
	}
	
	
	public int getRowIDByIndex(int index)
	{
		for(int ID:rowIndex.keySet())
			if(rowIndex.get(ID)==index)
				return ID;
		return -1;
	}
	
}
