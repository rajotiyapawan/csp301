import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.CircleLayout;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.io.CSVTableReader;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.data.io.TableReader;

public class Example1 {

	private static Graph graph;
	private static Visualization vis;
	private static Display d;
	
    public static void main(String[] argv) throws FileNotFoundException
	{
    	graph = new Graph(true);
    	
    	graph.addColumn("id", Integer.class);
    	graph.addColumn("value", String.class);
    	graph.addColumn("label",String.class);
    	
    	File f = new File("polbooks.gml");
		
		Scanner sc = new Scanner(f);
		sc.useDelimiter("\n\n|\n");
		String str = sc.next();
		int numNodes=0;
	
		while(sc.hasNext()){
			if(str.contains("node")){
				sc.next();sc.next();Node n = graph.addNode();
				n.set("id",numNodes);
				Scanner sc1 = new Scanner(sc.next());
				sc1.next();	n.set("label", sc1.nextLine());
				Scanner sc2 = new Scanner(sc.next());
				sc2.next();	n.set("value", sc2.next());
				numNodes++;	sc.next();}
			if(str.contains("edge")){
				sc.next();String temp = sc.next();
				Scanner sc1 = new Scanner(temp);
				sc1.next();	int n1 = Integer.parseInt(sc1.next());
				temp = sc.next();Scanner sc2 = new Scanner(temp);
				sc2.next();	int n2 = Integer.parseInt(sc2.next());
				graph.addEdge(n1,n2);}
				str = sc.next();
		}
		System.out.println(graph.getEdgeCount());
		//JOptionPane.showMessageDialog(null,graph.isDirected());
		//System.out.println(numNodes);

		setUpVisualization();
		setUpRenderers();
		setUpActions();
		setUpDisplay();

        JFrame frame = new JFrame("PAWAN POLBOOKS");
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.add(d);
        
        frame.pack();           
        
        frame.setVisible(true); 
        
        vis.run("color");
        vis.run("layout");
	}
     
    
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
        
        d.addControlListener(new FinalControlListener());
	}
    
}