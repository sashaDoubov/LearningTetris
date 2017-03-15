package tetris_2;

public class Chromosome {
	public double features[];
	private int featLength;
	
	public Chromosome(int n)
	{
		features = new double[n];
		featLength = n;
	}
	public double getFeat(int i)
	{
		return features[i];
	}
	public int getFeatLength()
	{
		return featLength;
	}
	
	public void randomInit(double Min, double Max) {
		for (int i = 0; i < featLength; i++)
		{
			features[i] = Min + (Math.random() * ((Max - Min) + 1));
		}
	}
	
	// random cross-over
	public static Chromosome crossOver(Chromosome par1, Chromosome par2)
	{
		for (int i = 0; i < par1.features.length; i++)
		{
			// 60 % crossover rate
			if (Math.random() < 0.6)
			{
				par1.features[i] = par2.getFeat(i);
			}
		}
		return par1;
	}
	
	public void mutate(double d, double e)
	{
		for (int i = 0; i < featLength; i++)
		{
			// 1 % mutation rate
			if (Math.random() < 0.01)
			{
				features[i] += e + (int)(Math.random() * ((d - e) + 1));
			}
		}
	}
}
