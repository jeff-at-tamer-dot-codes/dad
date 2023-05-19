package net.jtamer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/** a task is a node in a DAG (directed acyclic graph) */
interface Task {

  /** the set of all tasks which could come after this task, without needing a changeover */
  Set<Task> getAllNextTasks();
}

/** @author Jeff Tamer (jeff@tortuga.coop) */
public final class ChangeoverSolver {

  static final Task CHANGEOVER = new Task() {
    @Override public String toString() { return "(changeover)"; }
    @Override public Set<Task> getAllNextTasks() { throw new AssertionError(); }
  };

  public static void main(String[] args) throws FileNotFoundException {
    String taskString = getTaskString(args);
    Set<Task> tasks = parseTasks(taskString);
    List<Task> orderedTasks = findOptimalOrdering(tasks);
    System.out.println(orderedTasks);
  }

  /** gets the input string, e.g. "A, A B, A B C, A B D, A B D F", from a file or from stdin */
  static String getTaskString(String[] args) throws FileNotFoundException {
    if (args.length == 0) {
      return new Scanner(System.in).nextLine();
    } else if (args.length == 1) {
      return new Scanner(new File(args[0])).nextLine();
    } else {
      System.err.println("Usage: java ChangeoverSolver [filename]");
      System.exit(1);
      throw new AssertionError();
    }
  }

  /** parses the input string into a set of Task objects (a directed acyclic graph) */
  static Set<Task> parseTasks(String taskString) {
    String[] taskStrings = taskString.split(",");
    int numTasks = taskStrings.length;

    List<Task> allTasks = new ArrayList<Task>(numTasks);
    List<Set<Task>> allNextTasks = new ArrayList<Set<Task>>(numTasks);
    List<Set<String>> allAttributeSets = new ArrayList<Set<String>>(numTasks);

    for (int index = 0; index < numTasks; ++index) {
      final String toString = taskStrings[index].trim();
      final Set<Task> nextTasks = new HashSet<Task>();
      Set<String> attributeSet = new HashSet<String>(Arrays.asList(toString.split(" ")));

      Task task = new Task() {
        @Override public String toString() { return toString; }
        @Override public Set<Task> getAllNextTasks() { return nextTasks; }
      };

      // loop through the other tasks to find all the subsets and supersets of this task's attributes
      for (int otherIndex = 0; otherIndex < index; ++otherIndex) {
        if (attributeSet.containsAll(allAttributeSets.get(otherIndex))) {
          allNextTasks.get(otherIndex).add(task);
        } else if (allAttributeSets.get(otherIndex).containsAll(attributeSet)) {
          nextTasks.add(allTasks.get(otherIndex));
        }
      }

      allTasks.add(task);
      allNextTasks.add(nextTasks);
      allAttributeSets.add(attributeSet);
    }

    return new HashSet<Task>(allTasks);
  }

  /** takes a DAG and computes an optimal ordering in polynomial time */
  static List<Task> findOptimalOrdering(Set<Task> tasks) {

    // start with the worst solution: a changeover in between every task
    Map<Task, Task> previousTaskMap = new HashMap<Task, Task>();
    Map<Task, Task> nextTaskMap = new HashMap<Task, Task>();

    // repeatedly improve the ordering by removing one changeover, until optimal
    while (improveOrdering(tasks, previousTaskMap, nextTaskMap)) { }

    // use the ordering (previousTaskMap and nextTaskMap) to build the list to be returned
    List<Task> ordering = new ArrayList<Task>((tasks.size() * 2) - nextTaskMap.size() - 1);
    for (Task task : tasks) {
      if (previousTaskMap.containsKey(task)) continue;
      if (!ordering.isEmpty()) ordering.add(CHANGEOVER);
      while (task != null) {
        ordering.add(task);
        task = nextTaskMap.get(task);
      }
    }
    return ordering;
  }

  /** returns true iff exactly one changeover is removed, or false iff the ordering was already optimal */
  static boolean improveOrdering(Set<Task> tasks,
                                 Map<Task, Task> previousTaskMap,
                                 Map<Task, Task> nextTaskMap) {
    Set<Task> previousTasks = new HashSet<Task>();
    Set<Task> nextTasks = new HashSet<Task>();
    Map<Task, Task> improvedPreviousTaskMap = new HashMap<Task, Task>();

    // start with the set of tasks which are immediately followed by a changeover
    for (Task task : tasks) {
      if (!nextTaskMap.containsKey(task)) previousTasks.add(task);
    }

    // run a breadth first search for an ordering that uses one fewer changeover in total
    while (true) {
      for (Task previousTask : previousTasks) {
        for (Task nextTask : previousTask.getAllNextTasks()) {
          if (improvedPreviousTaskMap.containsKey(nextTask)) continue;

          if (previousTaskMap.containsKey(nextTask)) {
            improvedPreviousTaskMap.put(nextTask, previousTask);
            nextTasks.add(nextTask);
            continue;
          }

          // success! save the changes to the ordering and return true
          while (true) {
            previousTaskMap.put(nextTask, previousTask);
            nextTask = nextTaskMap.put(previousTask, nextTask);
            if (nextTask == null) return true;
            previousTask = improvedPreviousTaskMap.get(nextTask);
          }
        }
      }

      // if we run out of tasks, then we've hit the optimal ordering and the algorithm terminates
      if (nextTasks.isEmpty()) return false;

      // compute the next layer of nodes in the breadth first search
      previousTasks.clear();
      for (Task nextTask : nextTasks) {
        previousTasks.add(previousTaskMap.get(nextTask));
      }
      nextTasks.clear();
    }
  }
}
