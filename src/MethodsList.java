import java.util.Collection;

import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class MethodsList extends JFrame
{
	private static final long serialVersionUID = -2707712944901661771L;

	private static final int	ROOT_X	= 200;
	private static final int	ROOT_Y	= 20;

	private static final int	DEFAULT_WIDTH	= 100;
	private static final int	DEFAULT_LENGTH	= 100;
	private static final int	DEFAULT_SPACE	= 50;

	private mxGraph	graph;
	private Object		parent;

	public MethodsList(String className, Collection<String> methodsName)
	{
		super("Hello, World!");

		graph = new mxGraph();
		parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();

		Object rootNode = graph.insertVertex(parent, null, className, ROOT_X, ROOT_Y, DEFAULT_WIDTH, DEFAULT_LENGTH);

		int xCounter = 50;

		for(String methodName : methodsName)
		{
			Object methodNode = graph.insertVertex(parent, null, methodName, xCounter, 150, DEFAULT_WIDTH,
				DEFAULT_LENGTH);
			graph.insertEdge(parent, null, null, rootNode, methodNode);

			xCounter += DEFAULT_WIDTH + DEFAULT_SPACE;
		}

		graph.getModel().endUpdate();

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
	}
}
