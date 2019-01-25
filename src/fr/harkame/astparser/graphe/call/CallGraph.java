package fr.harkame.astparser.graphe.call;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import fr.harkame.astparser.structure.tree.ClassTree;
import fr.harkame.astparser.structure.tree.InvocationTree;
import fr.harkame.astparser.structure.tree.MethodTree;

public class CallGraph extends JFrame
{
	private static final long serialVersionUID = -2707712944901661771L;

	private static final int	ROOT_X	= 200;
	private static final int	ROOT_Y	= 20;

	private static final int DEFAULT_SPACE = 150;

	private static final int	CLASS_WIDTH	= 600;
	private static final int	CLASS_HEIGHT	= 200;

	private static final int	METHOD_WIDTH	= 200;
	private static final int	METHOD_HEIGHT	= 75;

	private mxGraph	graph;
	private Object		parent;

	private Collection<NodeContent> nodeContents = new ArrayList<NodeContent>();

	public CallGraph(String graphName, ClassTree treeStructure)
	{
		super("CallGraphe : " + graphName);

		graph = new mxGraph();
		parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		int xCounter = 0;
		int yCounter = 0;

		for(Map.Entry<String, MethodTree> classEntry : treeStructure.classTree.entrySet())
		{

			Object classFigure = graph.insertVertex(parent, null, classEntry.getKey(), ROOT_X + xCounter,
				ROOT_Y + yCounter, CLASS_WIDTH, CLASS_HEIGHT, "fillColor=#b3e6ff");
			
			int methodCounter = 0;

			for(Map.Entry<String, InvocationTree> methodDeclarationEntry : classEntry.getValue().methodTree
				.entrySet())
			{
				Object methodFigure = graph.insertVertex(classFigure, null, methodDeclarationEntry.getKey(), methodCounter, 0,
					METHOD_WIDTH, METHOD_HEIGHT, "fillColor=#ffcc80");

				NodeContent nodeContent = new NodeContent(classEntry.getKey(), methodDeclarationEntry.getKey(),
					methodFigure);

				for(Map.Entry<String, Collection<String>> methodInvocationEntry : methodDeclarationEntry
					.getValue().invocationTree.entrySet())
					for(String methodInvocation : methodInvocationEntry.getValue())
						nodeContent.nodeReferences
							.add(new NodeReference(methodInvocationEntry.getKey(), methodInvocation));
				
				nodeContents.add(nodeContent);
				
				methodCounter += METHOD_WIDTH + 20;

			}
			xCounter += DEFAULT_SPACE;
			yCounter += DEFAULT_SPACE;
		}

		connect();

		graph.setAllowDanglingEdges(false);
		graph.setEdgeLabelsMovable(true);
		graph.setConnectableEdges(false);

		graph.setCellsDeletable(false);
		graph.setCellsCloneable(false);
		graph.setCellsDisconnectable(false);
		graph.setDropEnabled(false);
		graph.setSplitEnabled(false);
		graph.setDisconnectOnMove(false);

		graph.setCellsBendable(true);

		graph.getModel().endUpdate();

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
	}

	public void connect()
	{
		for(NodeContent nodeContent : nodeContents)
			for(NodeReference nodeReference : nodeContent.nodeReferences)
				for(NodeContent otherNodeContent : nodeContents)
					if(nodeReference.className.equals(otherNodeContent.className)
						&& nodeReference.methodName.equals(otherNodeContent.methodName))
						graph.insertEdge(parent, null, null, nodeContent.methodFigure,
							otherNodeContent.methodFigure);
	}
}
