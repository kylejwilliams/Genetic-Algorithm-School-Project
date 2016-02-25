import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * @author kyle
 *
 */
public class Project2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
		
		System.out.println("population size: ");
		int popSize = reader.nextInt();
		System.out.println("string size: ");
		int stringSize = reader.nextInt();
		
		geneticAlgorithm(popSize, stringSize);
		
		reader.close();
	}

	
	/**
	 * @param popSize
	 * @param stringSize
	 * @return
	 */
	public static ArrayList<String> generatePopulation(int popSize, 
													   int stringSize) {
		ArrayList<String> population = new ArrayList<>();
		Random rand = new Random();
		String tmp = new String();
		int randNum;
		
		for (int i = 0; i < popSize; i++) {
			for (int j = 0; j < stringSize; j++) {
				randNum = rand.nextInt();
				tmp = tmp.concat((randNum % 2 == 0) ? "0" : "1");
			}
			
			String tmpBackup = tmp;
			population.add(tmpBackup);
			tmp = "";
		}
		
		return population;
	}
	
	/**
	 * @param s
	 * @return
	 */
	public static int oneMax(String s) {
		int numOnes = 0;
		
		for (int i = 0; i < s.length(); i++)
			numOnes = (s.charAt(i) == '1') ? numOnes + 1 : numOnes;
		
		return numOnes;
	}
	
	/**
	 * @param parentPop
	 * @return
	 */
	public static String tournamentSelect(ArrayList<String> parentPop) {
		Random rand = new Random();
		int parentIndex;
		
		parentIndex = Math.abs(rand.nextInt()) % parentPop.size();
		String parentOne = parentPop.get(parentIndex);

		parentIndex = Math.abs(rand.nextInt()) % parentPop.size();
		String parentTwo = parentPop.get(parentIndex);

		
		if (oneMax(parentOne) > oneMax(parentTwo)) return parentOne;
		else return parentTwo;
	}
	
	/**
	 * @param parentOne
	 * @param parentTwo
	 * @return
	 */
	public static ArrayList<String> uniformCrossover(String parentOne, 
													 String parentTwo) {
		double probOfPickingBitFromParentOne = 0.5;
		double probOfCrossover = 0.6;
		ArrayList<String> generatedChildren = new ArrayList<>();
		String childOne = "";
		String childTwo = "";
		
		double randNum = Math.random();
		
		if (randNum <= probOfCrossover) {
			
			for (int i = 0; i < parentOne.length(); i++) {
				randNum = Math.random();
				
				if (randNum <= probOfPickingBitFromParentOne) {
					childOne = childOne + parentOne.charAt(i);
					childTwo = childTwo + parentTwo.charAt(i);
				}
				else {
					childOne = childOne + parentTwo.charAt(i);
					childTwo = childTwo + parentOne.charAt(i);
				}
			}
		}
		
		else {
			childOne = parentOne;
			childTwo = parentTwo;
		}

		childOne = mutate(childOne);
		childTwo = mutate(childTwo);
		
		generatedChildren.add(childOne);
		generatedChildren.add(childTwo);
		
		return generatedChildren;
	}

	/**
	 * @param populationSize
	 * @param stringSize
	 */
	public static void geneticAlgorithm(int populationSize, int stringSize) {
		ArrayList<String> parentPop = 
				generatePopulation(populationSize, stringSize);
		ArrayList<String> childPop = new ArrayList<>();
		Boolean averageFitnessImproved = false;
		int generation = 0;
		
		do {
			generation++;
			System.out.println(" Generation: " + generation 
						+ " | Best fitness: " + bestFitness(parentPop) 
						+ " | Worst fitness: " + worstFitness(parentPop) 
						+ " | Average fitness: " + averageFitness(parentPop));
			
			while (childPop.size() < parentPop.size() - 1) {
				String parentOne = tournamentSelect(parentPop);
				String parentTwo = tournamentSelect(parentPop);
				if (childPop.size() <= parentPop.size() - 3)
					childPop.addAll(uniformCrossover(parentOne, parentTwo));
				else
					childPop.add(uniformCrossover(parentOne, parentTwo).get(0));
			}
			childPop.add(fittestParent(parentPop));
			if (averageFitness(childPop) > averageFitness(parentPop)) 
				averageFitnessImproved = true;
			else 
				averageFitnessImproved = false;
			
			parentPop = new ArrayList<>(childPop);
			childPop = new ArrayList<>();
		} while (averageFitnessImproved 
				&& !containsGlobalOptimum(parentPop, stringSize));
		
		generation++;
		System.out.println(" Generation: " + generation +
				   " | Best fitness: " + bestFitness(parentPop) +
				   " | Worst fitness: " + worstFitness(parentPop) +
				   " | Average fitness: " + averageFitness(parentPop));
		
	}
	
	/**
	 * @param parentPopulation
	 * @return
	 */
	public static String fittestParent(ArrayList<String> parentPopulation) {
		String mostFitParent = parentPopulation.get(0);
		for (int i = 0; i < parentPopulation.size(); i++) {
			mostFitParent = (oneMax(mostFitParent) 
						  > oneMax(parentPopulation.get(i))) 
						  ? mostFitParent 
						  : parentPopulation.get(i);
		}

		return mostFitParent;
	}
	
	/**
	 * @param child
	 * @return
	 */
	public static String mutate(String child) {
		double probOfMutation = 1.0 / child.length();
		char childArr[] = child.toCharArray();
		
		for (int i = 0; i < child.length(); i++) {
			if (Math.random() <= probOfMutation) {
				if (childArr[i] == '1') childArr[i] = '0';
				else childArr[i] = '1';
				
			}
		}
		
		child = String.valueOf(childArr);
		
		return child;
	}
	
	/**
	 * @param pop
	 * @return
	 */
	public static double averageFitness(ArrayList<String> pop) {
		double totalFitness = 0.0;
		
		for (int i = 0; i < pop.size(); i++)
			totalFitness += oneMax(pop.get(i));
		
		return totalFitness / pop.size();
	}
	
	/**
	 * @param pop
	 * @return
	 */
	public static double worstFitness(ArrayList<String> pop) {
		String worstFit = pop.get(0);
		
		for (int i = 0; i < pop.size(); i++) {
			if (oneMax(pop.get(i)) < oneMax(worstFit)) worstFit = pop.get(i);
		}
		
		return (oneMax(worstFit));
	}
	
	/**
	 * @param pop
	 * @return
	 */
	public static double bestFitness(ArrayList<String> pop) {
		String bestFit = pop.get(0);
		
		for (int i = 0; i < pop.size(); i++) {
			if (oneMax(pop.get(i)) > oneMax(bestFit)) bestFit = pop.get(i);
		}
		
		return (oneMax(bestFit));
	}
	
	/**
	 * @param pop
	 * @param stringSize
	 * @returncontainsGlobalOptimum
	 */
	public static Boolean containsGlobalOptimum(ArrayList<String> pop, 
											    int stringSize) {
		String globalOptimum = "";
		
		for (int i = 0; i < stringSize; i++) {
			globalOptimum += "1";
		}
		
		for (int i = 0; i < pop.size(); i++) {
			if (pop.get(i).equals(globalOptimum)) return true;
		}
		
		return false;
	}
	
}  