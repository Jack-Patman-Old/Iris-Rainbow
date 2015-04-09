package test.headline.generator.graph;

import static org.junit.Assert.*;

import com.iris.rainbow.headline.generator.graph.Edge;
import com.iris.rainbow.headline.generator.graph.Node;
import org.junit.Test;

public class NodeTest
{
    @Test
    public void getEdge_valid() throws Exception
    {
        Node node = new Node();
        node.setNodeText("Text");
        node.setNodePosTag("Tag");

        Node edgeNode = new Node();
        edgeNode.setNodeText("EdgeNode");
        edgeNode.setNodePosTag("EdgePosTag");
        node.addEdge(edgeNode);

        Edge foundEdge = node.getEdge("EdgeNode", "EdgePosTag");

        assertNotNull(foundEdge);
        assertEquals(foundEdge.getConnectedNode().getNodeText(), "edgenode");
        assertEquals(foundEdge.getConnectedNode().getNodePosTag(), "EdgePosTag");
    }

    @Test
    public void getEdge_nullParametersPassed_Exception() throws Exception
    {
        Node node = new Node();
        node.setNodeText("Text");
        node.setNodePosTag("Tag");

        Node edgeNode = new Node();
        edgeNode.setNodeText("EdgeNode");
        edgeNode.setNodePosTag("EdgePosTag");
        node.addEdge(edgeNode);

        Edge foundEdge = node.getEdge(null, null);

        assertNull(foundEdge);
    }

    @Test
    public void getEdge_noEdgeExists_exception() throws Exception
    {
        Node node = new Node();
        node.setNodeText("Text");
        node.setNodePosTag("Tag");

        Edge foundEdge = node.getEdge("EdgeNode", "EdgePosTag");

        assertNull(foundEdge);
    }

    @Test
    public void edgeExists_valid() throws Exception
    {
        Node node = new Node();
        node.setNodeText("Text");
        node.setNodePosTag("Tag");

        Node edgeNode = new Node();
        edgeNode.setNodeText("EdgeNode");
        edgeNode.setNodePosTag("EdgePosTag");
        node.addEdge(edgeNode);

        assertTrue(node.edgeExists("EdgeNode", "EdgePosTag"));
    }

    @Test
    public void edgeExists_nullParametersPassed_Exception() throws Exception
    {
        Node node = new Node();
        node.setNodeText("Text");
        node.setNodePosTag("Tag");

        Node edgeNode = new Node();
        edgeNode.setNodeText("EdgeNode");
        edgeNode.setNodePosTag("EdgePosTag");
        node.addEdge(edgeNode);

        assertFalse(node.edgeExists(null, null));
    }

    @Test
    public void edgeExists_noEdgeExists_exception() throws Exception
    {
        Node node = new Node();
        node.setNodeText("Text");
        node.setNodePosTag("Tag");

        assertFalse(node.edgeExists("EdgeNode", "EdgePosTag"));

    }

    @Test
    public void addEdge_valid() throws Exception
    {
        Node node = new Node();
        node.setNodeText("Text");
        node.setNodePosTag("Tag");

        Node edgeNode = new Node();
        edgeNode.setNodeText("EdgeNode");
        edgeNode.setNodePosTag("EdgePosTag");
        node.addEdge(edgeNode);

        assertTrue(node.getEdges().size() > 0);
    }

    @Test
    public void addEdge_invalid() throws Exception
    {
        Node node = new Node();
        node.setNodeText("Text");
        node.setNodePosTag("Tag");

        node.addEdge(null);

        assertTrue(node.getEdges().size() > 0);
    }
}