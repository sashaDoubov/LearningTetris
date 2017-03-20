package tetris_2;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import com.sun.xml.internal.fastinfoset.sax.Features;

public class Population {
	public Chromosome[] population;
	public int[] scores;
	public int[] ranking;
	public int popSum;
	
	public Population(int n, int num_features)
	{
		population = new Chromosome[n];
		scores = new int[n];
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
	public void fitSum(int[] fitness)
	{
		popSum = 0;
		for (int i : fitness)
			popSum += i;
	}
	public Chromosome rouletteSelection(int[] fitness)
	{
		int randomNum = ThreadLocalRandom.current().nextInt(0, popSum + 1);
		
		int sum = 0;
		for (int i = 0; i < fitness.length; i++)
		{
			sum += fitness[i];
			if (sum >= randomNum)
			{
				//System.out.println("score for parent: " + scores[i]);
				return population[i];
			}
		}
		return population[fitness.length - 1];
	}
	public void rankSelection()
	{
		quickSort(scores,0,scores.length -1);
		
		ranking = new int[scores.length];
		for(int i = 0; i < scores.length; i++)
		{
			ranking[i] = i;
		}
		
	}
	
	int partition(int arr[], int left, int right)
	{
	      int i = left, j = right;
	      int tmp;
	      int pivot = arr[(left + right) / 2];
	     
	      while (i <= j) {
	            while (arr[i] < pivot)
	                  i++;
	            while (arr[j] > pivot)
	                  j--;
	            if (i <= j) {
	            	  Chromosome tmpChromo = population[i];
	                  tmp = arr[i];
	                  
	                  population[i] = population[j];
	                  arr[i] = arr[j];
	                  
	                  population[j] = tmpChromo;
	                  arr[j] = tmp;
	                  i++;
	                  j--;
	            }
	      };
	     
	      return i;
	}
	 
	void quickSort(int arr[], int left, int right) {
	      int index = partition(arr, left, right);
	      if (left < index - 1)
	            quickSort(arr, left, index - 1);
	      if (index < right)
	            quickSort(arr, index, right);
	}
}
