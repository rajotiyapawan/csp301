import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

import prefuse.data.Graph;
import prefuse.data.Node;


public class randomgraphs{	

	static Graph g1;
	static boolean found = false;
	static boolean enter = false;
	static boolean dir = false;
	static int numnodes;
	static int numedges;
	static String file;

	static boolean check(String word1,String word2){
		StringTokenizer tokens = new StringTokenizer(word1, " ");
		word1 = tokens.nextToken();
		if (word1.equals(word2)){
			return true;
		}else{
			return false;}
	}	

	static ArrayList<Point1> columnAdder(Graph graph,File file) throws IOException{

		ArrayList<Point1> index = new ArrayList<Point1>();	

		BufferedReader bfr = new BufferedReader(new FileReader(file));
		String stream = bfr.readLine();	

		String b;
		boolean foo = false;
		boolean c = true;


		while(!stream.startsWith("  n")){
			stream = bfr.readLine();}

		while(!(stream = bfr.readLine()).startsWith("  ]")){
			if (!stream.startsWith("  [")){
				StringTokenizer tokens = new StringTokenizer(stream, " ");
				stream = tokens.nextToken();
				b = tokens.nextToken();
				try { 
					int x = Integer.parseInt(b); 
				} 
				catch(NumberFormatException nFE) { 
					c = false;
				}
				if (c == true){ 
					graph.addColumn(stream,Integer.class);
					Point1 x = new Point1();
					x.stm = stream;
					x.type = "Integer";		
					index.add(x);
				}else{
					graph.addColumn(stream,String.class);
					Point1 x = new Point1();
					x.stm = stream;
					x.type = "String";
					index.add(x); }
			}
		}
		return index;
	}

	static Graph fileReader(String fi) throws IOException{
		Graph grp;
		File file = new File(fi);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String str = br.readLine();

		while(!str.contains("directed")){
			str=br.readLine();
		}
		StringTokenizer toke = new StringTokenizer(str, " ");
		str = toke.nextToken();
		String c = toke.nextToken();	
		if ( Integer.parseInt(c) == 1){
			dir = true;	}System.out.println(str+"   "+c+"  "+dir);

			grp = new Graph(dir);
			ArrayList<Point1> in = columnAdder(grp,file); 	

			int i = 0;
			int j = 0;
			int k = 0;
			int first = -1;
			int second = -1;
			Node n = grp.addNode();
			while (check(str,"edge") != true){	
				String[] arr = new String[in.size()]; 
				k = 0;
				while(k < in.size() && check(str,"edge") != true){
					str = br.readLine();
					if( found == true|check(str,"node") == true){
						if(!str.startsWith("[") && check(str,"edge") != true && !str.startsWith("]") && !str.startsWith("  ]") && !str.startsWith("  [") && !str.startsWith("  n") && !str.startsWith("  e")){
							str = str.replaceAll("    [^ ]* ", " ");
							arr[k] = str;
							if (str.equals(" 1")){
								enter = true;
							}
							if (i%(in.size()) == 0){
								if (enter == false){
									grp.removeNode(0);
									enter = true;
								}
								n = grp.addNode();
							}
							if((in.get(i%(in.size()))).type == "Integer"){

								StringTokenizer tokens = new StringTokenizer(arr[k], " ");

								arr[k] = tokens.nextToken();

								int st = Integer.parseInt(arr[k]);

								n.set(in.get(i%(in.size())).stm,st);

							}else{

								arr[k] = arr[k].trim();

								arr[k] = arr[k].replaceAll("^\"|\"$", "");
								n.set(in.get(i%(in.size())).stm,arr[k]);
							}
							i++;
							k++;
						}
						found = true;
					}
				}
			}
			while ((str = br.readLine()) != null){
				if(!str.startsWith("[") && !str.startsWith("]") && !str.startsWith("  ]") && !str.startsWith("  [") && !str.startsWith("  e")){
					str = str.replaceAll("    [^ ]* ", " ");
					StringTokenizer tokens = new StringTokenizer(str, " ");
					str = tokens.nextToken();
					switch(j%2){
					case 0: first = Integer.parseInt(str);
					break;
					case 1: second = Integer.parseInt(str);
					grp.addEdge(first,second);
					break;
					}j++;	
				}
			}
			return grp;}

public static void main(String args[])/*ran(Graph g, int numnodes,int numedges)*/{

	Scanner sc = new Scanner(System.in);
	file = sc.nextLine();
	Graph g = new Graph();
	try {
		  g = fileReader(file);
	} catch (IOException e) {
		e.printStackTrace();
	}
	numnodes = g.getNodeCount();
    numedges = g.getEdgeCount();
	
	Random rand=new Random();

	for(int z=0;z<10;z++)
	{ 
		
		Graph graph2 = new Graph();
		graph2 = g;
	int r=0;
	for(int k=0;k<numedges;++k){
		graph2.removeEdge(k);
	}
	for(int j=0;j<numedges;++j)
	{
		int f=rand.nextInt(numnodes);
		int s=rand.nextInt(numnodes);
		graph2.addEdge(f, s);
		if(graph2.getNode(f).get("value").equals(graph2.getNode(s).get("value"))){r++;}
	}
	System.out.println("nmbr of edges in random graph number "+z+" is "+graph2.getNodeCount());
	System.out.println("edges b/w same type in random graph number "+z+" is"+r);
	int triad=0;
	for(int a=0;a<numnodes;a++)
	{
		Node x=graph2.getNode(a);
		for(int b=0;b<numnodes;b++)
		{
			Node y=graph2.getNode(b);

			if(graph2.getEdge(x, y)!=null)
			{
				for(int c=0;c<numnodes;c++)
				{
					if(!(c==b))
					{
						Node z1=graph2.getNode(c);
						if(graph2.getEdge(z1,x)!=null)
							triad++;
					}
					else continue;	
				}}}}
	System.out.println("number of triads in the graph in graph number "+z+"is "+triad);
	}
}
}