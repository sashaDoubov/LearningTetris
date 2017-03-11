package tetris_2;

public class Population {
	public Chromosome[] population;
	
	public Population(int n, int num_features)
	{
		population = new Chromosome[n];
		
		for (int i = 0; i < n; i++)
		{
			population[i] = new Chromosome(num_features);
		}
	}
	public void randPop()
	{
		for (int i = 0; i < population.length;i++)
		{
			population[i].randomInit(-10, -10);
		}
	}
}
