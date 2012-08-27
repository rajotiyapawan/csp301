import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import java.io.*;

import java.util.ArrayList;

import java.util.StringTokenizer;

class Point1{
	String stm;
	Object type;
}

public class polblogs {

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
    g1 = grp;
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
	return grp;
}

private static Graph graph;
private static Visualization vis;
private static Display d;
     
public static void setUpVisualization()
{
    vis = new Visualization();
    
    vis.add("graph", graph);
    
}


public static void setUpRenderers()
{
    FinalRenderer r = new FinalRenderer();
    
    vis.setRendererFactory(new DefaultRendererFactory(r));
}

public static void setUpActions()
{
	int[] palette = {ColorLib.rgb(0, 0, 200), ColorLib.rgb(200,0, 0),ColorLib.rgb(0,200,0)}; 
	
	DataColorAction fill = new DataColorAction("graph.nodes", "value", Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
   
    ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));
    
    ActionList color = new ActionList();
    color.add(fill);
    color.add(edges);
    
    ActionList layout = new ActionList(Activity.INFINITY);
    
    layout.add(new ForceDirectedLayout("graph", true));
    
    layout.add(new RepaintAction());
    
    vis.putAction("color", color);
    vis.putAction("layout", layout);
    
}

public static void setUpDisplay()
{
    d = new Display(vis);
    
    d.setSize(720, 500); 
    
    d.addControlListener(new DragControl());
   
    d.addControlListener(new PanControl()); 
    
    d.addControlListener(new ZoomControl());
        if(file.contains("polbooks.gml")){
        d.addControlListener(new FinalControlListener());}
        else{d.addControlListener(new FinalControlListener1());}
	}
	
	public static void main(String args[]) throws IOException{
		Scanner sc = new Scanner(System.in);
		file = sc.nextLine();
		graph = fileReader(file);
		//JOptionPane.showMessageDialog(null, graph.isDirected());
		numnodes = graph.getNodeCount();
        numedges = graph.getEdgeCount();
		//UILib.setPlatformLookAndFeel();
		setUpVisualization();
		setUpRenderers();
		setUpActions();
		setUpDisplay();
        JFrame frame = new JFrame(file);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(d);
        frame.pack();      
        frame.setVisible(true); 
        vis.run("color");
        vis.run("layout");
        
	}
}