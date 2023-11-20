import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;

public class Ferate {

	public static int Time;

	public class Graph {

		// Clasa interna pentru graf

		private ArrayList<Integer>[] adjList;
		private int[] freqDegree;

		// Contructor pentru lista de adiacenta si vectorul de frecventa
		public Graph(int nrNodes) {
			adjList = new ArrayList[nrNodes];
			freqDegree = new int[nrNodes];
			for (int i = 0; i < nrNodes; i++) {
				adjList[i] = new ArrayList<>();
				freqDegree[i] = 0;
			}
		}

		/* Metoda pentru adaugarea unei muchii 
		si incrementarea frecventei gradului intern */
		public void addEdge(int u, int v) {
			adjList[u].add(v);
			freqDegree[v - 1]++;
		}

		public ArrayList<Integer> getAdjacent(int u) {
			return adjList[u];
		}

		/* Metoda pentru calcularea numarului de noduri 
		cu grad intern 0 exceptand depozitul */
		public int computeSolution(int depo) {
			int rez = 0;
			for (int i = 0; i < freqDegree.length; i++) {
				if (freqDegree[i] == 0 && i != depo) {
					rez++;
				}
			}
			return rez;
		}
	}

	public static void main(String[] args) {

		int nrNodes;
		int nrEdges;
		int depo;

		try {

			FileReader inputFile = new FileReader("ferate.in");
			var data = new MyScanner(inputFile);

			// Citire date din fisier
			nrNodes = data.nextInt();
			nrEdges = data.nextInt();
			depo = data.nextInt();

			Ferate f = new Ferate();
			Graph graph = f.new Graph(nrNodes + 1);

			// Adaugare muchii pentru nodurile din graf
			for (int i = 0; i < nrEdges; i++) {
				int u = data.nextInt();
				int v = data.nextInt();
				graph.addEdge(u, v);
			}

			// Lista de liste cu componentele tare conexe
			ArrayList<ArrayList<Integer>> foundSccs = new ArrayList<>();

			for (int i = 1; i <= nrNodes; i++) {
				foundSccs.add(new ArrayList<>());
			}

			// Calculare componentele tare conexe folosind algoritmul lui Tarjan
			foundSccs = tarjan(nrNodes, graph.adjList, depo);

			// Determinare componenta conexa in care se afla depozitul
			int setDepo = 0;
			for (int i = 0; i < foundSccs.size(); i++) {
				for (int j = 0; j < foundSccs.get(i).size(); j++) {
					if (foundSccs.get(i).get(j) == depo) {
						setDepo = i;
					}
				}
			}

			// Calculare numar minim de muchii care trebuie adaugate
			int rez = addMinimum(nrNodes, graph, graph.adjList, depo, setDepo, foundSccs);

			try {

				PrintStream outFile = new PrintStream("ferate.out");

				// Afisare rezultat
				outFile.println(rez);

			} catch (FileNotFoundException e) {
				System.out.println("Error: output file not found");
				return;

			}

		} catch (FileNotFoundException e) {

			System.out.println("Error: input file not found");
			return;
		}
	}

	// Functie recursiva pentru gasirea componentelor tare conexe folosind DFS
	public static ArrayList<ArrayList<Integer>> scc(int node, ArrayList<Integer>[] adjList, 
			int[] low, int[] found, boolean[] stackMember, Stack<Integer> st, int nrNodes) {
		
		// Lista pentru a stoca componentele tare conexe
		ArrayList<ArrayList<Integer>> sccList = new ArrayList<>();

		found[node] = Time;
		low[node] = Time;
		Time += 1;
		stackMember[node] = true;
		st.push(node);

		int n;

		// Parcurgere noduri adiacente
		Iterator<Integer> i = adjList[node].iterator();

		while (i.hasNext()) {
			n = i.next();

			if (found[n] == -1) {
				/* Daca nodul n nu a fost inca descoperit, 
				se apeleaza recursiv functia scc pentru el */
				ArrayList<ArrayList<Integer>> scc = 
					scc(n, adjList, low, found, stackMember, st, nrNodes);
				// Actualizam low[node] cu minimul dintre low[node] si low[n]
				low[node] = Math.min(low[node], low[n]); 

				// Adaugam componentele tare conexe returnate de apelurile recursive in lista
				for (ArrayList<Integer> component : scc) {
					sccList.add(component);
				}

			} else if (stackMember[n] == true) {
				/* Daca nodul n este inca in stiva, 
				se actualizeaza low[node] cu minimul dintre low[node] si found[n] */
				low[node] = Math.min(low[node], found[n]);
			}
		}

		int w = -1;
		if (low[node] == found[node]) {
			// S-a gasit o componenta tare conexa noua
			// Initializam lista pentru a stoca nodurile componentei tare conexe
			ArrayList<Integer> scc = new ArrayList<>();
			while (w != node) {
				w = st.pop(); // Extragem nodul din varful stivei
				stackMember[w] = false; // Marcam nodul ca fiind scos din stiva
				scc.add(w); // Adaugam nodul in lista componentei tare conexe
			}
			// Adaugam lista componentei tare conexe in lista principala
			sccList.add(scc);
		}
		return sccList;
	}

	public static ArrayList<ArrayList<Integer>> tarjan(int nrNodes, 
														ArrayList<Integer>[] adjList, int depo) {

		ArrayList<ArrayList<Integer>> Sccs = new ArrayList<>();
		int[] found = new int[nrNodes + 1];
		int[] low = new int[nrNodes + 1];
		boolean[] stackMember = new boolean[nrNodes + 1];
		Stack<Integer> st = new Stack<>();

		for (int i = 1; i <= nrNodes; i++) {
			found[i] = -1;  // Niciun nod nu a fost descoperit
			low[i] = -1; // Low-ul fiecarui nod nu este cnoscut
		}

		for (int i = 1; i <= nrNodes; i++) {
			if (found[i] == -1) {
				/* Daca nodul i nu a fost inca descoperit, 
				apelam functia scc pentru a-i gasi componentele tare conexe care il contin */
				ArrayList<ArrayList<Integer>> Scc = 
					scc(i, adjList, low, found, stackMember, st, nrNodes);

				for (ArrayList<Integer> component : Scc) {
					// Adaugam componentele tare conexe returnate de functia scc in lista
					Sccs.add(component);
				}
			}
		}
		return Sccs;
	}

	// Functie pentru crearea unui graf nou din componentele tare conexe obtinute
	public static int addMinimum(int nrNodes, Graph graph, ArrayList<Integer>[] adjList, 
									int depo, int setDepo, ArrayList<ArrayList<Integer>> sccs) {

		int sccsSize = sccs.size(); // numarul de componente tare conexe
		Ferate f = new Ferate();
		Graph newGraph = f.new Graph(sccsSize + 1); // graful nou
		int[] sccsIndex = new int[nrNodes + 1]; // vector cu indexul componentei tare conexe

		// Initializare vector cu indexul componentei tare conexe
		for (int i = 0; i < sccsSize; i++) {
			for (int node : sccs.get(i)) {
				sccsIndex[node] = i + 1;
			}
		}

		// Adaugare muchii in graful nou
		for (int u = 0; u < nrNodes; u++) {
			for (int v : graph.getAdjacent(u)) {
				if (sccsIndex[u] != sccsIndex[v]) {
					newGraph.addEdge(sccsIndex[u], sccsIndex[v]);
				}
			}
		}

		// Apelarea metodei de calculare a numarului minim de muchii
		int k = newGraph.computeSolution(setDepo);
		return k - 1;
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
