package fr.harkame.astparser.util;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import fr.harkame.astparser.example.TreeStructure;

public class MethodsList extends JFrame
{
	private static final long serialVersionUID = -2707712944901661771L;

	private static final int	ROOT_X	= 200;
	private static final int	ROOT_Y	= 20;

	private static final int	DEFAULT_WIDTH_SMALL	= 50;
	private static final int	DEFAULT_WIDTH	= 100;
	private static final int	DEFAULT_LENGTH	= 100;
	private static final int	DEFAULT_SPACE	= 90;

	private mxGraph	graph;
	private Object		parent;

	public MethodsList(TreeStructure treeStructure)
	{
		super("Hello, World!");

		graph = new mxGraph();
		parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();

		Object rootNode = graph.insertVertex(parent, null, treeStructure.className, ROOT_X, ROOT_Y, DEFAULT_WIDTH, DEFAULT_LENGTH,  "fillColor=#ff8080;");

		int xCounter = 50;
		
		for(Map.Entry<String, Set<String>> declarationInvocation : treeStructure.declarationInvocations.entrySet())
		{
			Object methodNode = graph.insertVertex(parent, null, declarationInvocation.getKey(), xCounter, 150, DEFAULT_WIDTH,
				DEFAULT_LENGTH);
			
			int xInvocationCounter = xCounter;
			
			graph.insertEdge(parent, null, null, rootNode, methodNode);
			
			for(String methodInvocation : declarationInvocation.getValue())
			{
				System.out.println("toto");
				
				Object methodInvocationName = graph.insertVertex(parent, null, methodInvocation, xInvocationCounter, 300, DEFAULT_WIDTH_SMALL,
					DEFAULT_LENGTH, "fillColor=#9dff96;");
				
				xInvocationCounter +=  DEFAULT_WIDTH_SMALL + 50;
				
				graph.insertEdge(parent, null, null, methodNode, methodInvocationName);
			}
			

			xCounter += DEFAULT_WIDTH + DEFAULT_SPACE;
		}

		graph.getModel().endUpdate();

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
	}
}
