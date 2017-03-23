package tetris_2;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import com.sun.xml.internal.fastinfoset.sax.Features;

public class Population {
	public Chromosome[] population;
	public int[] ranking;
	public int popSum;
	
	public Population(int n, int num_features)
	{
		//System.out.println("n" + n);
		population = new Chromosome[n];
		popSum = 0;
		
		for (int i = 0; i < n; i++)
		{
			population[i] = new Chromosome(num_features);
		}
	}
	public void randPop()
	{
		for (int i = 0; i < population.length;i++)
		{
			population[i].randomInit(-100, 100);
		}
	}
	public void fitSum()
	{
		popSum = 0;
		for (int i = 0; i < population.length; i++)
		{
			popSum += population[i].getScore();
		}
	}
	public Chromosome rouletteSelection()
	{
		int randomNum = ThreadLocalRandom.current().nextInt(0, popSum + 1);
		
		int sum = 0;
		for (int i = 0; i < population.length; i++)
		{
			sum += population[i].getScore();
			if (sum >= randomNum)
			{
				return population[i];
			}
		}
		return population[population.length - 1];
	}
}
