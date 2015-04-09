package test.headline.generator.graph;

import com.iris.rainbow.headline.generator.graph.DirectedGraph;
import com.iris.rainbow.headline.generator.graph.Node;
import org.junit.Test;

import static org.junit.Assert.*;

public class DirectedGraphTest
{

    @Test
    public void testAddNode_valid() throws Exception
    {
        DirectedGraph graph = new DirectedGraph();
        graph.addNode(new Node());

        assertTrue(graph.getNodes().size()>0);
    }

    @Test
    public void testAddNode_exception() throws Exception
    {
        DirectedGraph graph = new DirectedGraph();
        graph.addNode(null);

        assertTrue(graph.getNodes().size() == 0);
    }

    @Test
    public void testFindNode_valid_nodeExists() throws Exception
    {
        String nodeText = "NodeText";
        String posTag = "PosTag";

        DirectedGraph graph = new DirectedGraph();

        Node node = new Node();
        node.setNodeText(nodeText);
        node.setNodePosTag(posTag);
        graph.addNode(node);

        Node searchedNode = graph.findNode(nodeText, posTag);
        assertNotNull(searchedNode);
        assertEquals(searchedNode.getNodeText(), nodeText);
        assertEquals(searchedNode.getNodePosTag(), posTag);
    }


    @Test
    public void testFindNode_valid_nodeDoesntExist() throws Exception
    {
        String nodeText = "NodeText";
        String posTag = "PosTag";

        DirectedGraph graph = new DirectedGraph();

        Node node = new Node();
        node.setNodeText(nodeText);
        node.setNodePosTag(posTag);
        graph.addNode(node);

        Node searchedNode = graph.findNode("invalidText", "invalidTag");
        assertNull(searchedNode);
    }


    @Test
    public void testFindNode_exception_nullsearchterms() throws Exception
    {
        String nodeText = "NodeText";
        String posTag = "PosTag";

        DirectedGraph graph = new DirectedGraph();

        Node node = new Node();
        node.setNodeText(nodeText);
        node.setNodePosTag(posTag);
        graph.addNode(node);

        Node searchedNode = graph.findNode(null, null);
        assertNull(searchedNode);
    }


    @Test
    public void testFindNode_exception_emptygraph() throws Exception
    {
        DirectedGraph graph = new DirectedGraph();

        Node searchedNode = graph.findNode(null, null);
        assertNull(searchedNode);
    }

    @Test
    public void testNodeExists_NodeExists_Valid() throws Exception
    {
        String nodeText = "NodeText";
        String posTag = "PosTag";

        DirectedGraph graph = new DirectedGraph();

        Node node = new Node();
        node.setNodeText(nodeText);
        node.setNodePosTag(posTag);
        graph.addNode(node);

        assertTrue(graph.NodeExists(node));
    }

    @Test
    public void testNodeExists_NodeDoesntExist_Valid() throws Exception
    {
        String nodeText = "NodeText";
        String posTag = "PosTag";

        DirectedGraph graph = new DirectedGraph();

        Node node = new Node();
        node.setNodeText(nodeText);
        node.setNodePosTag(posTag);

        assertFalse(graph.NodeExists(node));
    }

    @Test
    public void testNodeExists_NullSearchTerm_Exception() throws Exception
    {
        String nodeText = "NodeText";
        String posTag = "PosTag";

        DirectedGraph graph = new DirectedGraph();
        Node node = new Node();
        node.setNodeText(nodeText);
        node.setNodePosTag(posTag);
        graph.addNode(node);

        assertFalse(graph.NodeExists(null));
    }

    @Test
    public void testNodeExists_NoNodesInGraph_Exception() throws Exception
    {
        String nodeText = "NodeText";
        String posTag = "PosTag";

        DirectedGraph graph = new DirectedGraph();

        Node node = new Node();
        node.setNodeText(nodeText);
        node.setNodePosTag(posTag);

        assertFalse(graph.NodeExists(node));
    }


}