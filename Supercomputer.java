import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Supercomputer {

	public class Graph {

		// Clasa interna pentru graf

		private ArrayList<Integer>[] adjList;

		// Contructor pentru lista de adiacenta
		public Graph(int nrNodes) {
			adjList = new ArrayList[nrNodes];
			for (int i = 0; i < nrNodes; i++) {
				adjList[i] = new ArrayList<>();
			}
		}

		// Metoda pentru adaugarea unei muchii
		public void addEdge(int u, int v) {
			adjList[u].add(v);
		}

		public ArrayList<Integer> getAdjacent(int u) {
			return adjList[u];
		}
	}

	public static void main(String[] args) {

		int nrTasks;
		int nrDependecies;

		try {

			FileReader inputFile = new FileReader("supercomputer.in");
			var data = new MyScanner(inputFile);

			nrTasks = data.nextInt();
			nrDependecies = data.nextInt();

			int[] dataSets = new int[nrTasks + 1];
			int[] inDegree = new int[nrTasks + 1];

			Supercomputer s = new Supercomputer();
			Graph graph = s.new Graph(nrTasks + 1);

			// Citirea seturilor de date
			for (int i = 0; i < nrTasks; i++) {
				dataSets[i] = data.nextInt();
			}

			/* Citirea dependentelor intre task-uri
			si crearea grafului de dependente */
			for (int i = 1; i <= nrDependecies; i++) {
				int u = data.nextInt();
				int v = data.nextInt();
				graph.addEdge(u, v);
				inDegree[v]++;
			}

			// Crearea cozilor pentru task-urile cu set de date 1 si 2
			Queue<Integer> Qset1 = new ArrayDeque<>();
			Queue<Integer> Qset2 = new ArrayDeque<>();

			int minContextSwitch1 = 0;
			int minContextSwitch2 = 0;

			int[] inDegree1 = new int[nrTasks + 1];
			int[] inDegree2 = new int[nrTasks + 1];

			// Copierea vectorului de grade interne pentru a putea fi modificat
			for (int i = 1; i <= nrTasks; i++) {
				inDegree1[i] = inDegree[i];
				inDegree2[i] = inDegree[i];
			}

			/* Apelam functia avand ca prima coada cea pentru setul de date 1, 
			apoi cea pentru setul 2 */
			minContextSwitch1 = returnMin(Qset1, Qset2, dataSets, 
											inDegree1, nrTasks, 1, graph.adjList);
			minContextSwitch2 = returnMin(Qset1, Qset2, dataSets, 
											inDegree2, nrTasks, 2, graph.adjList);

			try {

				PrintStream outFile = new PrintStream("supercomputer.out");

				// Afisarea numarului minim returnat de functie
				if (minContextSwitch1 < minContextSwitch2) {
					outFile.println(minContextSwitch1);
				} else {
					outFile.println(minContextSwitch2);
				}

			} catch (FileNotFoundException e) {
				System.out.println("Error: output file not found");
				return;

			}

		} catch (FileNotFoundException e) {

			System.out.println("Error: input file not found");
			return;
		}

	}

	public static int returnMin(Queue<Integer> Qset1, Queue<Integer> Qset2,
			int[] dataSets, int[] inDegree, int nrTasks,
			int whichQueue, ArrayList<Integer>[] adjList) {

		// Adaugam in coada task-urile care nu au dependente (au grad intern 0)
		for (int i = 1; i <= nrTasks; i++) {
			if (inDegree[i] == 0 && dataSets[i - 1] == 1) {
				Qset1.add(i);
			} else if (inDegree[i] == 0 && dataSets[i - 1] == 2) {
				Qset2.add(i);
			}
		}

		int cnt = 0;
		int contextSwitch = 0;

		while (!Qset1.isEmpty() || !Qset2.isEmpty()) { // Verificare caz de oprire

			if ((whichQueue == 2 && !Qset2.isEmpty()) 
					|| (whichQueue == 1 && Qset1.isEmpty())) {

				/*  Daca coada pentru setul de date 2 nu este goala, 
				scoatem un element din ea */
				int u = Qset2.poll();

				/* Daca am trecut la alta coada, 
				incrementam numarul de context switch-uri */
				if (whichQueue == 1) {
					whichQueue = 2;
					contextSwitch++;
				}

				// Parcurgem vecinii nodului scos din coada
				for (int v : adjList[u]) {
					/* Daca gradul intern al unui vecin devine 0, 
					il adaugam in coada corespunzatoare pentru setul de date */
					if (--inDegree[v] == 0) {
						if (dataSets[v - 1] == 1) {
							Qset1.add(v);
						} else {
							Qset2.add(v);
						}
					}
				}

			} else if ((whichQueue == 1 && !Qset1.isEmpty()) 
						|| (whichQueue == 2 && Qset2.isEmpty())) {

				// Analog pentru coada pentru setul de date 1
				int u = Qset1.poll();

				/* Daca am trecut la alta coada, 
				incrementam numarul de context switch-uri */
				if (whichQueue == 2) {
					whichQueue = 1;
					contextSwitch++;
				}

				// Parcurgem vecinii nodului scos din coada
				for (int v : adjList[u]) {
					/* Daca gradul intern al unui vecin devine 0, 
					il adaugam in coada corespunzatoare pentru setul de date */
					if (--inDegree[v] == 0) {
						if (dataSets[v - 1] == 1) {
							Qset1.add(v);
						} else {
							Qset2.add(v);
						}
					}
				}

			}
			cnt++;
		}

		if (cnt != nrTasks) { // Verificare daca exista ciclu in graf
			System.out.println("There exists a cycle in the graph");
			return -1;
		}

		return contextSwitch; // Returnam numarul minim de context switch-uri
	}

	private static class MyScanner {

		private BufferedReader br;
		private StringTokenizer st;

		public MyScanner(Reader reader) {
			br = new BufferedReader(reader);
		}

		public String next() {
			while (st == null || !st.hasMoreElements()) {
				try {
					st = new StringTokenizer(br.readLine());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return st.nextToken();
		}

		public int nextInt() {
			return Integer.parseInt(next());
		}

		public long nextLong() {
			return Long.parseLong(next());
		}

		public double nextDouble() {
			return Double.parseDouble(next());
		}

		public String nextLine() {
			String str = "";
			try {
				str = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return str;
		}
	}
}
