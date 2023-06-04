package org.example;
import java.util.Arrays;
import java.util.Random;
public class Individual implements Comparable{
    int [] chromosome=new int[GAUtils.CHROMOSOME_SIZE];
    int fitness;

    public Individual(){
        Random random=new Random();
        for(int i=0;i<GAUtils.CHROMOSOME_SIZE;i++){
            //chromosome[i]=Math.random()>=0.5?1:0;
            chromosome[i]=random.nextInt(2);
        }
        calculateFitness();
    }

    public Individual(int[] chromosome) {
        this.chromosome = Arrays.copyOf(chromosome,GAUtils.CHROMOSOME_SIZE);
        calculateFitness();
    }

    public void calculateFitness(){
        fitness= Arrays.stream(chromosome).sum();
        //for(int i=0;i<GAUtils.CHROMOSOME_SIZE;i++) fitness+=chromosome[i];
    }

    public int getFitness() {
        return fitness;
    }

    @Override
    public int compareTo(Object o) {
        Individual individual = (Individual) o;
        if (this.fitness > individual.fitness) {
            return 1;
        }else if(this.fitness < individual.fitness) {
            return -1;
        }
        return 0;
    }

}
