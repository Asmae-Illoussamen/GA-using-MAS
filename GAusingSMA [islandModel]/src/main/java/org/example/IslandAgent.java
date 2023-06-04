package org.example;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class IslandAgent extends Agent {
    private Individual[] population=new Individual[GAUtils.POPULATION_SIZE];
    private Individual individual1;
    private Individual individual2;

    @Override
    protected void setup() {


        SequentialBehaviour sequentialBehaviour=new SequentialBehaviour();

        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                initialize();
                sortPopulation();
            }
        });

        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            int iter=0;
            @Override
            public void action() {
                crossover();
                mutation();
                sortPopulation();
                iter++;
            }

            @Override
            public boolean done() {
                return GAUtils.MAX_ITERATIONS <=iter || getBestFitness()==GAUtils.CHROMOSOME_SIZE;
            }
        });
        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfagentDescription=new DFAgentDescription();
                ServiceDescription serviceDescription=new ServiceDescription();
                serviceDescription.setType("ga");
                dfagentDescription.addServices(serviceDescription);
                DFAgentDescription[] dfAgentDescriptions=null;
                try {
                    dfAgentDescriptions = DFService.search(getAgent(),dfagentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }

                ACLMessage aclMessage=new ACLMessage(ACLMessage.INFORM);
                aclMessage.addReceiver(dfAgentDescriptions[0].getName());
                aclMessage.setContent(Arrays.toString(population[0].chromosome)+" " +population[0].getFitness());
                send(aclMessage);

            }
        });

        addBehaviour(sequentialBehaviour);

    }

    //initialze population
    public void initialize(){
        for(int i=0;i<GAUtils.POPULATION_SIZE;i++) {
            population[i] = new Individual();
            population[i].calculateFitness();
        }
        System.out.println("Initial population : ");
        sortPopulation();
        showPopulation();
    }
    public void showPopulation(){
        System.out.println("Population : ");
        for(Individual individual:population){
            System.out.println(Arrays.toString(individual.chromosome) + " | fitness : "+individual.fitness);
        }
    }
    public void crossover(){
        individual1=new Individual(population[0].chromosome);
        individual2=new Individual(population[1].chromosome);


        Random random=new Random();
        int crossoverPoint=random.nextInt(GAUtils.CHROMOSOME_SIZE-1)+1;
        //System.out.println("Crossover point : "+crossoverPoint);
        for(int i=0;i<=crossoverPoint;i++){
            individual1.chromosome[i]=population[1].chromosome[i];
            individual2.chromosome[i]=population[0].chromosome[i];
        }
        individual1.calculateFitness();
        individual2.calculateFitness();



        /*On remplace les derniers individus de la population par les nouveaux individus*/
        population[GAUtils.POPULATION_SIZE-2]=individual1;
        population[GAUtils.POPULATION_SIZE-1]=individual2;

    }

    public void sortPopulation(){
        Arrays.sort(population, Collections.reverseOrder());
    }

    public void mutation(){
        Random random=new Random();
        // Individu 1
        if(random.nextDouble()>GAUtils.MUTATION_PROB){
            int mutationPoint=random.nextInt(GAUtils.CHROMOSOME_SIZE);
            individual1.chromosome[mutationPoint]=1-individual1.chromosome[mutationPoint];
        }
        // Individu 2
        if(random.nextDouble()>GAUtils.MUTATION_PROB){
            int mutationPoint=random.nextInt(GAUtils.CHROMOSOME_SIZE);
            individual2.chromosome[mutationPoint]=1-individual2.chromosome[mutationPoint];
        }
    }
    public int getBestFitness(){
        return population[0].getFitness();
    }

    /*
    public boolean testSolution() {
        if (population[0].fitness == GAUtils.CHROMOSOME_SIZE) {
            System.out.println("Solution found : " + Arrays.toString(population[0].chromosome));
            return false;
        }
        return true;
    }*/

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }


}
