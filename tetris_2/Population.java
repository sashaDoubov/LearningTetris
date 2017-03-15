package tetris_2;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Population {
	public Chromosome[] population;
	public int[] scores;
	public Population(int n, int num_features)
	{
		population = new Chromosome[n];
		scores = new int[n];
		
		for (int i = 0; i < n; i++)
		{
			population[i] = new Chromosome(num_features);
		}
	}
	public void randPop()
	{
		for (int i = 0; i < population.length;i++)
		{
			population[i].randomInit(-10, 10);
		}
	}
	
	public Chromosome rouletteSelection()
	{
		int sum = 0;
		for (int i = 0; i <  scores.length; i++)
			sum += scores[i];
		
		int randomNum = ThreadLocalRandom.current().nextInt(0, sum + 1);
		
		sum = 0;
		for (int i = 0; i < scores.length; i++)
		{
			sum += scores[i];
			if (sum >= randomNum)
			{
				return population[i];
			}
		}
		return population[scores.length - 1];
	}
	
}
