# Algorithms-Design-2

Dinu Dan
323CC
Topic 2 PA

Supercomputer Problem:
For the implementation of this problem, I used an internal class to store information about the dependency graph. Thus, by reading the dependencies between tasks, I created a directed graph with an adjacency list. The solution idea is based on the principle of topological sorting (using Khan's algorithm) while considering the data set of each task. Therefore, I used two queues: one for nodes with data set 1 and one for nodes with data set 2. In addition, I needed a vector to keep track of the incoming degree of each node.

Initially, I added to the queue nodes with an internal degree of 0 (which do not depend on any other task). Then, I created a while block with the condition that both queues are not empty. In this block, the variable "whichQueue" (1 or 2) keeps track of which queue should be traversed based on the node's data set. Thus, if the queue for data set 2 is not empty, we remove an element from it and traverse its neighbors. If the internal degree of a neighbor becomes 0, we add it to the corresponding queue for its data set. If the queue for data set 2 is empty, we proceed similarly for the queue for data set 1. In both cases, if we switch to another queue, we increment the number of context switches. Thus, in the end, we obtain a topological sorting of the dependency graph, considering also the data set of each task. Finally, we return the number of context switches. The complexity of the algorithm is: O(nrTasks + nrDependencies) (nodes + edges).

Railway Problem:
Similar to the first problem, I used an internal class for the graph. The solution idea is based on finding the strongly connected components of the graph, forming a new graph from these components, and then calculating the minimum number of edges that need to be added as follows:

I stored the connected component in which the depot is located.
I formed a frequency vector that will store the internal degree of the strongly connected components (later represented as nodes in a new graph).
The minimum number of edges that need to be added is represented by the number of strongly connected components with an internal degree of 0 excluding the depot (in case it also has an internal degree of 0).
The strongly connected components were stored in a list of lists and found using Tarjan's algorithm with a DFS traversal. To form the new graph, I checked if the index of the strongly connected component of a node is different from the index of the strongly connected component of a neighbor. In this case, I added an edge between the two strongly connected components. Having the new graph formed from strongly connected components, all that remained was to count how many strongly connected components have an internal degree of 0, excluding the depot, and this number represents the minimum number of edges that need to be added. For the above-described step, I used a method inside the graph class "computeSolution", and the frequency vector was initialized in the constructor of the graph class, later being updated in the method of adding edges (method "addEdge"). The complexity of the algorithm is: O(nrNodes + nrEdges).

Store Problem:
Similar to the first two problems, I used an internal class for the graph. The solution idea is based on iterative depth-first traversal (DFS) and storing the traversed paths for later use, thus avoiding traversing the same paths multiple times (calling dfs for each "question"). I believe this method of solution would have led to a higher number of passed tests, but I was not able to complete its implementation. Therefore, I created a list of lists where for each node "i" I stored the nodes through which it passes to reach the depot. Then, I went through each "question" and checked if node "i" is in the list of lists of node "j" (if node "j" can reach node "i") and displayed the corresponding result. For this implementation, I used an iterative DFS algorithm along with 2 methods for deleting and adding elements at the beginning of a vector. The complexity of the algorithm is: O(nrNodes + nrEdges).

Note:

to read the data from the file, I used the class given in the skeleton from the previous topic.
The algorithms for topological sorting (Khan), finding strongly connected components (Tarjan), and iterative DFS traversal were inspired by the laboratories as well as geeksforgeeks.org and modified to meet the requirements of the problems.
