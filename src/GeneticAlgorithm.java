import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GeneticAlgorithm {
	private ArrayList <Individual> individuals =  new ArrayList <Individual>();
	private double mutationRate;
	private int popSize;
	private int numInputs;

/**
 * @author - Sri Kondapalli 
 * @param mutationRate - 
 * @param popSize
 * @param numInputs
 */

	public GeneticAlgorithm(double mutationRate, int popSize, int numInputs) {
		this.mutationRate = mutationRate; 
		this.popSize = popSize;
		this.numInputs = numInputs;
	}

	public void start(int times) throws InterruptedException, ExecutionException {
		for (int i=0; i<times; i++) {
			main();
		}
	}


	private void main() throws InterruptedException, ExecutionException {
		ExecutorService service = Executors.newFixedThreadPool(10);
		ArrayList<Future<Individual>> futures = new ArrayList<Future<Individual>>();
		for(int i = 0; i < popSize; i++) {
			Individual ind = new Individual(numInputs, new Game());
			futures.add(service.submit(ind));
		}
		/**
		 * @author Sri Kondapalli 
		 * places an Individual ind into a sorted ArrayList
		 */

		for (int i=0; i<futures.size()-1; i++) {
			Individual ind = futures.get(i).get();
			// add in a sorted manner into individuals ArrayList.
			if (individuals.size() == 0) individuals.add(ind);

			for(int j = 0; j < individuals.size(); j++) {
				if (ind.getFitness()  >= individuals.get(j).getFitness()) 
					individuals.add(j, ind);

			}
		}
		select();
	}


	private void select() {

		/**
		 * @author Sri Kondapalli
		 * 
		 * Adds the best individuals from the individuals arrayList, and reproduces pairs of best 70 individuals
		 * adds 30 new individuals to maintain population size(100). 
		 */

		ArrayList<Individual> theBest = new ArrayList<Individual>();
		for(int i = 0; i < individuals.size()*0.03; i++) {
			theBest.add(individuals.get(i));

		}
		for(int i = 0; i < individuals.size()*0.69; i++) {
			NeuralNetwork m1 = NeuralNetwork.reproduce(individuals.get(i).getNN(), individuals.get(i + 1).getNN(), mutationRate);
			NeuralNetwork m2 = NeuralNetwork.reproduce(individuals.get(i).getNN(), individuals.get(i + 1).getNN(), mutationRate);

			theBest.add(new Individual(m1, new Game()));
			theBest.add(new Individual(m2, new Game()));
		}
		for(int i = 0; i < individuals.size() * 0.30; i++) {
			theBest.add(new Individual(numInputs, new Game()));
		}
		individuals = theBest;
		while(mutationRate - 0.1 >= 0.01) {
			mutationRate -=0.1;
		}
	}

}






