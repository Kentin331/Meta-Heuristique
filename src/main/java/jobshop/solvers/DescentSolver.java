package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.List;

public class DescentSolver implements Solver {

    /** A block represents a subsequence of the critical path such that all tasks in it execute on the same machine.
     * This class identifies a block in a ResourceOrder representation.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The block with : machine = 1, firstTask= 0 and lastTask = 1
     * Represent the task sequence : [(0,2) (2,1)]
     *
     * */
    static class Block {
        /** machine on which the block is identified */
        final int machine;
        /** index of the first task of the block */
        final int firstTask;
        /** index of the last task of the block */
        final int lastTask;

        Block(int machine, int firstTask, int lastTask) {
            this.machine = machine;
            this.firstTask = firstTask;
            this.lastTask = lastTask;
        }
        
        public String toString() {
        	return("machine : " + this.machine + " First Task : " + this.firstTask + " Last Task : "+ this.lastTask);
        }
        
    }
    

    /**
     * Represents a swap of two tasks on the same machine in a ResourceOrder encoding.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The swam with : machine = 1, t1= 0 and t2 = 1
     * Represent inversion of the two tasks : (0,2) and (2,1)
     * Applying this swap on the above resource order should result in the following one :
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (2,1) (0,2) (1,1)
     * machine 2 : ...
     */
    static class Swap {
        // machine on which to perform the swap
        final int machine;
        // index of one task to be swapped
        final int t1;
        // index of the other task to be swapped
        final int t2;

        Swap(int machine, int t1, int t2) {
            this.machine = machine;
            this.t1 = t1;
            this.t2 = t2;
        }

        /** Apply this swap on the given resource order, transforming it into a new solution. */
        public void applyOn(ResourceOrder order) {
            Task tampon = order.tasksByMachine[machine][this.t1];
            order.tasksByMachine[machine][this.t1] = order.tasksByMachine[machine][this.t2];
            order.tasksByMachine[machine][this.t2] = tampon;
        }
        
    }


    @Override
    public Result solve(Instance instance, long deadline) {
    	//initialisation
    	Solver s = new GreedySolverEST_LRPT();
    	Result sInit = s.solve(instance,deadline);
    	Result sOpti = sInit;
    	
    	ResourceOrder order = new ResourceOrder(sOpti.schedule);
    	ResourceOrder orderInit = order.copy();
    	Boolean check = false;
    	
    	while(check == false && (deadline - System.currentTimeMillis() > 1)) {
    		
	    	List<Block> lblock = blocksOfCriticalPath(order);	    	
	    	int bestMakespan = order.toSchedule().makespan();
	    	
	    	for(Block b : lblock) {
	    		//récupérer les voisins de ce block
	    		List<Swap> neig = neighbors(b);
	    		//Evaluer chaque voisin
	    		for(Swap n : neig) {
	    			ResourceOrder tamp = order.copy();
	    			n.applyOn(tamp);
	    			if(tamp.toSchedule().makespan() < bestMakespan) {
	    				order = tamp;
	    				bestMakespan = tamp.toSchedule().makespan();
	    			}    			
	    		}    		
	    	}	    	
	    	if(order.equals(orderInit)) {
	    		check = true;
	    	}
	    	else {
	    		orderInit = order;
	    	}	    		
    	}    	
    	return new Result(instance, order.toSchedule(), Result.ExitCause.Blocked);
    }
    
     static int taskIndex(ResourceOrder order, Task task) {
    	int index=0;
    	for(int i =0; i<order.instance.numJobs;i++) {
    		if(order.tasksByMachine[order.instance.machine(task)][i].equals(task)) {
    			index = i;
    			break;
    		}
    			
    	}
    	return (index);   	
    }

    /** Returns a list of all blocks of the critical path. */
    public static List<Block> blocksOfCriticalPath(ResourceOrder order) {
        List<Task> criticalPath = order.toSchedule().criticalPath();
        List<Block> blockOfPath = new ArrayList<Block>();
        Task current, next;
        
        for(int i = 0; i<criticalPath.size()-1; i++) {
        	current = criticalPath.get(i);
        	next = criticalPath.get(i+1);
        	
        	if(blockOfPath.size() != 0 && (blockOfPath.get(blockOfPath.size()-1).machine) == order.instance.machine(current)) {
        		Block newBlock = new Block(order.instance.machine(current), blockOfPath.get(blockOfPath.size()-1).firstTask, taskIndex(order,current));
        		blockOfPath.remove(blockOfPath.size()-1);
        		blockOfPath.add(newBlock);
        	}
        	else {
        		if(order.instance.machine(current)== order.instance.machine(next)) {
        			blockOfPath.add(new Block(order.instance.machine(current), taskIndex(order,current), taskIndex(order,next)));
        		}
        	}
        }
        current = criticalPath.get(criticalPath.size()-1);
        return(blockOfPath);
    }

    /** For a given block, return the possible swaps for the Nowicki and Smutnicki neighborhood */
    List<Swap> neighbors(Block block) {
        //si que 2 elt dans bloc -> 1 swap avec mm numMach Tfirst tlast
    	//sinon -> 2 swap avecc 1er voisin et last voisin
    	List<Swap> res = new ArrayList<Swap>();
    	
    	if((block.lastTask - block.firstTask) == 1) {
    		res.add(new Swap(block.machine, block.firstTask, block.lastTask));
    	}
    	else
    	{
    		res.add(new Swap(block.machine, block.firstTask, block.firstTask +1));
    		res.add(new Swap(block.machine, block.lastTask - 1, block.lastTask));
    	}
    	
    	return(res);
    	
    }
   

}
