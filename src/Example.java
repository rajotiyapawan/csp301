
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;
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
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.data.io.TableReader;
public class Example {

// The data
private static Graph graph;

public static void main(String[] argv)
{
	 long t0 = System.currentTimeMillis();
randomgraphs();
setUpData();
long t1 = System.currentTimeMillis();
System.out.println(t1-t0);

}
// -- 1. load the data --------------------------------------------
public static void setUpData()
{
	graph = new Graph();
	
	int numnodes=0;
	int xyz=0;
	int counter=0;
	int neutral=0;
	int edgecount=0;
	
	String[] array=new String[3000];
	
	String[] edgearray=new String[1000];
	
	graph.addColumn("id", String.class);
	graph.addColumn("title", String.class);
	graph.addColumn("type", String.class);

	String string;
	 try{
	  FileInputStream fstream = new FileInputStream("polbooks.gml");
	  DataInputStream in = new DataInputStream(fstream);
	  BufferedReader br = new BufferedReader(new InputStreamReader(in));
	  String strLine;
	  while ((strLine = br.readLine()) != null)   {
	
		string=strLine.trim();
		string=string.replaceAll("id ","");
		string=string.replaceAll("label ","");
		string=string.replaceAll("value ","");
		string=string.replaceAll("source ","");
		string=string.replaceAll("target ","");
		//string=string.replaceAll("[","");
		//string=string.replaceAll("]","");
		//if(string.equals("node"))
		array[counter++]=string;
		  //System.out.println(array[counter-1]);
		  //if(string.equals("node")) xyz++;
	  }
	  in.close();
	    }catch (Exception e){
	  System.err.println("Error: " + e.getMessage());
	  }

	
	//System.out.println("total number of lines is "+counter+"nmbr of nodes is "+xyz);
	
	//System.out.println("total number of edges is "+edgecount);
	
	for(int i=0;i<2840;++i)
	{
		if(array[i].equals("edge"))
		{
			edgearray[edgecount++]=array[i+2];
			edgearray[edgecount++]=array[i+3];
			
		}
		//System.out.println(array[i]);
	}
	
	//System.out.println("number of edges "+edgecount);
	//System.out.println("edges are being printed");
	//for(int i=0;i<882;++i)
	//{
	//	System.out.println(edgearray[i]);
	//}
	
	for(int i=0;i<2480;++i)
	{
		if(array[i].equals("node"))
		{
			numnodes++;
			Node n= graph.addNode();
			n.set("id",array[i+2] );
			n.set("title", array[i+3]);
			n.set("type", array[i+4]); //System.out.println("the type here is "+n.get("type"));
			if(n.get("type").equals("\"n\"")) neutral++;
		}
		
	
	}
	
	System.out.println("the number of neutral nodes is "+(neutral));
	System.out.println("number of nodes is "+numnodes);
	
	
	
	 
	 
	 
	int same=0;
	int k=0;
	for(int i=0;i<880;i=i+2)
	{   int first=Integer.parseInt(edgearray[i]);
	    int second=Integer.parseInt(edgearray[i+1]);
	
	/*	char[] c1=edgearray[i].toCharArray();  System.out.println("this is edgearray "+edgearray[i]+"c1 is "+c1[0]+"length is "+c1.length);
		char[] c2=edgearray[i+1].toCharArray(); System.out.println("c2 is "+c2.toString());
		if(c1.length==1) {System.out.println("c1[0] here is "+c1[0]);first=Integer.parseInt(c1[0]);System.out.println("first here is "+first);}
		else if(c1.length==2) {first=((c1[0]*10)+c1[1]);}
		else if(c1.length==3) {first=((c1[0]*100)+(c1[1]*10)+c1[2]);}
		
		if(c2.length==1) {second=c2[0];}
		else if(c2.length==2) {second=((c2[0]*10)+c2[1]);}
		else if(c2.length==3) {second=((c2[0]*100)+(c2[1]*10)+c2[2]);}
       
		System.out.println("first is "+first+" and second is "+second);
		if(first>105||second>105) {System.out.println("fucking");}
		*/
		graph.addEdge(first,second);
		if(graph.getNode(first).get("type").equals(graph.getNode(second).get("type")))
			same++;
		k++;
	}
	 
	
	System.out.println("the number of edges is "+k+" and edges b/w same type of nodes is "+same); 
	System.out.println("edges added scuucessfully");
	
	System.out.println("method numnodes "+graph.getNodeCount());
	System.out.println("method numedges "+graph.getEdgeCount());
	int triad=0;
	for(int a=0;a<105;a++)
	{
		Node x=graph.getNode(a);
		
		for(int b=0;b<105;b++)
		{
			Node y=graph.getNode(b);
			
			if(graph.getEdge(x, y)!=null)
			{
				for(int c=0;c<105;c++)
				{
					if(!(c==b))
					{
						Node z=graph.getNode(c);
						
						if(graph.getEdge(z,x)!=null)
							triad++;
					}
					else continue;	
				}
			}
		}
	}

	System.out.println("number of triads in the graph is "+triad);
}

public static void randomgraphs()
{//random graph init
Graph graph2= new Graph();

String[] array=new String[60000];
int counter=0;
String string;
 try{
  FileInputStream fstream = new FileInputStream("polblogs.gml");
  DataInputStream in = new DataInputStream(fstream);
  BufferedReader br = new BufferedReader(new InputStreamReader(in));
  String strLine;
  while ((strLine = br.readLine()) != null)   {

	string=strLine.trim();
	string=string.replaceAll("id ","");
	string=string.replaceAll("label ","");
	string=string.replaceAll("value ","");
	string=string.replaceAll("source ","");
	string=string.replaceAll("target ","");
	
	array[counter++]=string;
	  
  }
  in.close();
    }catch (Exception e){
  System.err.println("Error: " + e.getMessage());
  }



for(int i=0;i<2480;++i)
{
	if(array[i].contains("node"))
	{
		Node n= graph2.addNode();
		}
	

}


Random rand=new Random();
for(int z=0;z<100;z++)
{  Graph g=new Graph();
  g=graph2;
  for(int rem=0;rem<440;++rem)
  g.removeEdge(rem);
	int r=0;
  for(int j=0;j<440;++j)
  {
	int f=rand.nextInt(105);
	int s=rand.nextInt(105);
	g.addEdge(f, s);
	if(g.getNode(f).get("type").equals(g.getNode(s).get("type")))
		r++;
		
  }
   System.out.println("nmbr of edges in random graph number "+z+" is "+g.getEdgeCount());
   System.out.println("edges b/w same type in random graph number "+z+" is"+r);
   int triad=0;
	for(int a=0;a<105;a++)
	{
		Node x=g.getNode(a);
		
		for(int b=0;b<105;b++)
		{
			Node y=g.getNode(b);
			
			if(g.getEdge(x, y)!=null)
			{
				for(int c=0;c<105;c++)
				{
					if(!(c==b))
					{
						Node z1=g.getNode(c);
						
						if(g.getEdge(z1,x)!=null)
							triad++;
					}
					else continue;	
				}
			}
		}
	}

	System.out.println("number of triads in the graph in graph number "+z+"is "+triad);

}

}
}