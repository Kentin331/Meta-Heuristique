package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.Vector;

public class GreedySolverEST_SPT implements Solver {
	
	private Task spt(Vector<Task> task, Instance instance, int [] duration_job, int [] duration_machine) {
		
		Vector<Task> est_tasks = est_spt(task,instance,duration_job, duration_machine);
		//System.out.println("est_task = " + est_tasks);
		Task est_min_lrpt = est_tasks.elementAt(0);
		
		for(int k=1; k<est_tasks.size(); k++) {
			if(instance.duration(est_tasks.elementAt(k)) < instance.duration(est_min_lrpt)) {
				est_min_lrpt = est_tasks.elementAt(k);
			}
		}
		return(est_min_lrpt);
	}
	
	private Vector<Task> est_spt(Vector<Task> task, Instance instance, int [] duration_job, int [] duration_machine){
		//priorité à la tâche la plus courte
		
		Task current = task.firstElement();
		Vector<Task> est_tasks = new Vector<Task>();
		
		//minimum starttime
		int min = 9999;
		for(int index = 0; index<task.size(); index++) {
			current = task.elementAt(index);
			if(Math.max(duration_job[current.job], duration_machine[instance.machine(current)]) < min)
				min = Math.max(duration_job[current.job], duration_machine[instance.machine(current)]);
			
		}
		
		for(int j=0; j<task.size(); j++) {
			current = task.elementAt(j);			
			if (Math.max(duration_job[current.job], duration_machine[instance.machine(current)]) == min) {
				
				est_tasks.add(current);
			}		
		}	
		
		
		return(est_tasks);
	}
		

	@Override
	public Result solve(Instance instance, long deadline) {
		//initialisations
		Vector<Task> realisable = new Vector<Task>();
		ResourceOrder sol = new ResourceOrder(instance);
		
		int [] duration_machine = new int [instance.numMachines];
		int [] duration_job = new int [instance.numJobs];
		
		
		
		//init
		for(int j=0; j<instance.numJobs; j++){
			realisable.add(new Task(j,0));
		}
		
		int remainingTasks = instance.numJobs*instance.numMachines;
		int machine;
		while(remainingTasks>0){
			Task current = spt(realisable,instance,duration_job, duration_machine);
			machine = instance.machine(current);
			sol.tasksByMachine[machine][sol.nextFreeSlot[machine]] = current;
			sol.nextFreeSlot[machine]++;
			if(current.task + 1 < instance.numMachines) {
			realisable.add(new Task(current.job, current.task + 1));
			}
			realisable.remove(current);
			int max = Math.max(duration_machine[machine], duration_job[current.job]);
			duration_machine[machine] = max + instance.duration(current);
			duration_job[current.job] = max + instance.duration(current);
			remainingTasks --;
		}
		
		return new Result(instance, sol.toSchedule(), Result.ExitCause.Blocked);
		}
}

 