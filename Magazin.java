import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Magazin {

	public class Graph {

		// Clasa interna pentru graf

		private ArrayList<Integer>[] adjList;
		private boolean[] visited;

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

		int nrDepo;
		int nrQuestions;

		try {

			FileReader inputFile = new FileReader("magazin.in");
			var data = new MyScanner(inputFile);

			// Citirea date din fisier
			nrDepo = data.nextInt();
			nrQuestions = data.nextInt();

			Magazin m = new Magazin();
			Graph graph = m.new Graph(nrDepo + 1);

			// Citire noduri si adaugarea muchiilor
			for (int i = 1; i < nrDepo; i++) {
				int depo = data.nextInt();
				graph.addEdge(depo, i + 1);
			}

			ArrayList<Integer>[] paths = new ArrayList[nrDepo + 1];

			// Calcularea drumurilor de la fiecare depou la celelalte
			for (int i = 1; i <= nrDepo; i++) {
				paths[i] = new ArrayList<>();
				paths[i] = idfs(graph.adjList, i, nrDepo);
			}

			try (var printer = new PrintStream("magazin.out")) {

				// Afisarea raspunsurilor
				for (int i = 1; i <= nrQuestions; i++) {
					int node = data.nextInt();
					int range = data.nextInt();

					if (paths[node].size() - 1 < range) {
						printer.println(-1);

					} else {
						printer.println(paths[node].get(range));
					}
				}
			} catch (FileNotFoundException e) {

				System.out.println("Error: input file not found");
				return;
			}

		} catch (FileNotFoundException e) {

			System.out.println("Error: input file not found");
			return;
		}
	}

	// Functie pentru a adauga un element la inceputul unui vector
	public static int[] addElementToHead(int[] v, ArrayList<Integer> element, int cnt) {

		for (int j = 0; j < cnt; j++) {
			for (int i = v.length - 1; i > 0; i--) {
				v[i] = v[i - 1];
			}
		}

		for (int i = 0; i < cnt; i++) {
			v[i] = element.get(i);
		}

		return v;
	}

	// Functie pentru stergerea unui element de pe prima pozitie a vectorului
	public static int deleteElementFromHead(int[] v) {
		int rez = v[0];
		for (int i = 0; i < v.length - 2; i++) {
			v[i] = v[i + 1];
		}
		v[v.length - 1] = 0;
		return rez;
	}

	// Functie pentru parcurgerea in adancime iterativa
	public static ArrayList<Integer> idfs(ArrayList<Integer>[] adjList, 
											int startVertex, int nrDepo) {

		// Array pentru a marca nodurile vizitate										
		boolean[] visited = new boolean[adjList.length]; 
		// Marcam nodul de start ca vizitat
		visited[startVertex] = true;
		// Lista temporara pentru a retine nodurile parcurse
		ArrayList<Integer> temp = new ArrayList<>();
		// Lista rezultat care va contine parcurgerea in adancime
		ArrayList<Integer> rez = new ArrayList<>();

		// Array pentru a retine calea parcursa
		int[] path = new int[2 * nrDepo];
		int currVertex = startVertex;
		temp.add(currVertex);

		// Adaug elementele din lista temporara la inceputul vectorului path
		path = addElementToHead(path, temp, 1);

		int c = 1;

		while (c != 0) {
			// Extrage primul element din lista path ca nod curent
			currVertex = deleteElementFromHead(path);
			// Adauga nodul curent in lista rezultat
			rez.add(currVertex);
			c--;
			int k = 0;
			// Parcurge vecinii nodului curent
			for (int neighbor : adjList[currVertex]) {
				if (!visited[neighbor]) { // Daca vecinul nu a fost vizitat
					currVertex = neighbor; // Nodul curent devine vecinul
					visited[neighbor] = true; // Marcheaza vecinul ca vizitat
					// Adauga vecinii nevizitati in lista temporara
					temp.add(k++, neighbor);
				}
			}
			if (k > 0) { // Daca exista vecini nevizitati
				c += k; // Actualizeaza numarul de vecini nevizitati
				path = addElementToHead(path, temp, k); // Adauga vecinii in lista path
			}
		}

		return rez;
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
