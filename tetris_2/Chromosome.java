package tetris_2;

import java.util.concurrent.ThreadLocalRandom;

public class Chromosome implements Comparable<Chromosome>{
	public double features[];
	private int featLength;
	public int score;
	
	public Chromosome(int n)
	{
		features = new double[n];
		featLength = n;
		score = 0;
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
	public static Chromosome randomCrossOver(Chromosome par1, Chromosome par2)
	{
		Chromosome chr = new Chromosome(par1.getFeatLength());
		for (int i = 0; i < par1.features.length; i++)
		{
			// 60 % crossover rate
			if (Math.random() < 0.6)
			{
				chr.features[i] = par1.features[i];			
			}
			else
			{
				chr.features[i] = par2.features[i];
			}
		}
		return chr;
	}
	
	public static Chromosome singleCrossOver(Chromosome par1, Chromosome par2)
	{
		Chromosome chr = new Chromosome(par1.getFeatLength());
		int randomNum = ThreadLocalRandom.current().nextInt(0, par1.getFeatLength() + 1);
		//System.out.println("random num: " + randomNum);
		
		for (int i = 0; i < par1.getFeatLength(); i++)
		{
			// 60 % crossover rate
			if (i < randomNum)
			{
				chr.features[i] = par1.features[i];			
			}
			else
			{
				chr.features[i] = par2.features[i];
			}
		}
		return chr;
	}
	
	public void mutate(double max, double min)
	{
		for (int i = 0; i < featLength; i++)
		{
			// 1 % mutation rate
			if (Math.random() < 0.01)
			{
				features[i] += min + (int)(Math.random() * ((max - min) + 1));
			}
		}
	}
	
	public void setScore(int sc)
	{
		score = sc;
	}
	public int getScore()
	{
		return score;
	}
	@Override
	public int compareTo(Chromosome otherChromo) {
		// TODO Auto-generated method stub
		return Integer.compare(otherChromo.getScore(), this.score);
	}
	
	
}
