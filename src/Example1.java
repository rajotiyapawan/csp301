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
    	graph = new Graph();
    	
    	graph.addColumn("id", Integer.class);
    	graph.addColumn("value", String.class);
    	graph.addColumn("label",String.class);
    	
    	File f = new File("polbooks.gml");
		//BufferedReader br = new BufferedReader(new FileReader(f));
		Scanner sc = new Scanner(f);
		sc.useDelimiter("\n\n|\n");
		String str = sc.next();
		int numNodes=0;
		//String str = br.readLine();
		while(sc.hasNext()){
			if(str.contains("node")){
				sc.next();sc.next();
				Node n = graph.addNode();
				n.set("id",numNodes);
				Scanner sc1 = new Scanner(sc.next());
				sc1.next();
				n.set("label", sc1.nextLine());
				Scanner sc2 = new Scanner(sc.next());
				sc2.next();
				n.set("value", sc2.next());
				numNodes++;
				sc.next();}
			if(str.contains("edge")){
				sc.next();
				String temp = sc.next();
				//System.out.println(temp);
				Scanner sc1 = new Scanner(temp);
				sc1.next();
				int n1 = Integer.parseInt(sc1.next());
				temp = sc.next();
				Scanner sc2 = new Scanner(temp);
				//System.out.println(temp);
				sc2.next();
				int n2 = Integer.parseInt(sc2.next());
				graph.addEdge(n1,n2);
				}
			//str=br.readLine();
			str = sc.next();
		}
		//System.out.println(numNodes);

		
		//UILib.setPlatformLookAndFeel();
    	
    	//setUpData();
		setUpVisualization();
		setUpRenderers();
		setUpActions();
		setUpDisplay();

        // launch the visualization -------------------------------------
        
        // The following is standard java.awt.
        // A JFrame is the basic window element in awt. 
        // It has a menu (minimize, maximize, close) and can hold
        // other gui elements. 
        
        // Create a new window to hold the visualization.  
        // We pass the text value to be displayed in the menubar to the constructor.
        JFrame frame = new JFrame("PAWAN POLBOOKS");
        
        // Ensure application exits when window is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // The Display object (d) is a subclass of JComponent, which
        // can be added to JFrames with the add method.
        frame.add(d);
        
        // Prepares the window.
        frame.pack();           
        
        // Shows the window.
        frame.setVisible(true); 
        
        // We have to start the ActionLists that we added to the visualization
        vis.run("color");
        vis.run("layout");
	}
     
    // -- 1. load the data ------------------------------------------------
		
    // -- 2. the visualization --------------------------------------------
	public static void setUpVisualization()
	{
        // Create the Visualization object.
		vis = new Visualization();
        
        // Now we add our previously created Graph object to the visualization.
        // The graph gets a textual label so that we can refer to it later on.
        vis.add("graph", graph);
        
	}
	
    // -- 3. the renderers and renderer factory ---------------------------
	public static void setUpRenderers()
	{
        // Create a default ShapeRenderer
        FinalRenderer r = new FinalRenderer();
        
        // create a new DefaultRendererFactory
        // This Factory will use the ShapeRenderer for all nodes.
        vis.setRendererFactory(new DefaultRendererFactory(r));
	}
	
	public static void setUpActions()
	{
		
        // -- 4. the processing actions ---------------------------------------
        
        // We must color the nodes of the graph.  
        // Notice that we refer to the nodes using the text label for the graph,
        // and then appending ".nodes".  The same will work for ".edges" when we
        // only want to access those items.
        // The ColorAction must know what to color, what aspect of those 
        // items to color, and the color that should be used.
		
		int[] palette = {ColorLib.rgb(200, 0, 0), ColorLib.rgb(0,0, 200),ColorLib.rgb(0,200,0)}; 
		
		// Now we create the DataColorAction
        // We give it the nodes to color, the data column to color by, a constant (don't worry about it (or check the api)), 
        // the way to color, and the palette to use.
        
        DataColorAction fill = new DataColorAction("graph.nodes", "value", Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
       
        // Similarly to the node coloring, we use a ColorAction for the 
        // edges
        ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));
        
        // Create an action list containing all color assignments
        // ActionLists are used for actions that will be executed
        // at the same time.  
        ActionList color = new ActionList();
        color.add(fill);
        color.add(edges);
        
     // The layout ActionList is constantly run to recalculate 
        // the positions of the nodes.
        ActionList layout = new ActionList(Activity.INFINITY);
        
        // We add the layout to the layout ActionList, and tell it
        // to operate on the "graph".
        layout.add(new ForceDirectedLayout("graph", true));
        
        // We add a RepaintAction so that every time the layout is 
        // changed, the Visualization updates it's screen.
        layout.add(new RepaintAction());
        
        // add the actions to the visualization
        vis.putAction("color", color);
        vis.putAction("layout", layout);
        
        /*// The layout ActionList recalculates 
        // the positions of the nodes.
        ActionList layout = new ActionList();
        
        // We add the layout to the layout ActionList, and tell it
        // to operate on the "graph".
        layout.add(new RandomLayout("graph"));
        
        // We add a RepaintAction so that every time the layout is 
        // changed, the Visualization updates it's screen.
        layout.add(new RepaintAction());
        
        // add the actions to the visualization
        vis.putAction("color", color);
        vis.putAction("layout", layout);*/
        
	}
	
	public static void setUpDisplay()
	{
        // -- 5. the display and interactive controls -------------------------
        
        // Create the Display object, and pass it the visualization that it 
        // will hold.
		d = new Display(vis);
        
        // Set the size of the display.
        d.setSize(720, 500); 
        
        // We use the addControlListener method to set up interaction.
        
        // The DragControl is a built in class for manually moving
        // nodes with the mouse. 
        d.addControlListener(new DragControl());
        // Pan with left-click drag on background
        d.addControlListener(new PanControl()); 
        // Zoom with right-click drag
        d.addControlListener(new ZoomControl());
        
        d.addControlListener(new FinalControlListener());
	}
    
}