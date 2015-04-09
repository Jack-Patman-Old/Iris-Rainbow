package com.iris.rainbow.headline.generator.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 18/03/2015.
 */
public class Node implements Comparable<Node>
{
    private List<Edge> edges = new ArrayList<Edge>();
    private Node previous;
    private String nodeText;
    private String nodePosTag;
    private boolean isStartNode;
    private boolean isEndNode;
    private double minDistance = Integer.MAX_VALUE;

    public void setNodeText(String text)
    {
        this.nodeText = text.toLowerCase();
    }

    public void setNodePosTag(String nodePosTag)
    {
        this.nodePosTag = nodePosTag;
    }

    public void setIsStartNode(boolean isStartNode)
    {
        this.isStartNode = isStartNode;
    }

    public boolean isStartNode()
    {
        return isStartNode;
    }

    public void setIsEndNode(boolean isEndNode)
    {
        this.isEndNode = isEndNode;
    }

    public boolean isEndNode()
    {
        return this.isEndNode;
    }

    public void addEdge(Node node)
    {
        Edge edge = new Edge();
        edge.setConnectedNode(node);
        edges.add(edge);
    }


    public boolean edgeExists(String nodeText, String nodePosTag)
    {
        if (nodeText == null || nodeText == null )
        {
            return false;
        }

        for (Edge edge : edges)
        {
            if (edge.getConnectedNode().getNodeText().equals(nodeText.toLowerCase()))
            {
                if (edge.getConnectedNode().getNodePosTag().equals(nodePosTag))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public Edge getEdge(String nodeText, String nodePosTag)
    {
        if (nodeText == null || nodePosTag == null)
        {
            return null;
        }

        for (Edge edge : edges)
        {
            if (edge.getConnectedNode().getNodeText().equals(nodeText.toLowerCase()))
            {
                if (edge.getConnectedNode().getNodePosTag().equals(nodePosTag))
                {
                    return edge;
                }
            }
        }

        return null;
    }

    public List<Edge> getEdges()
    {
        return edges;
    }

    public String getNodeText()
    {
        return nodeText;
    }

    public String getNodePosTag()
    {
        return nodePosTag;
    }

    public Node getPrevious()
    {
        return previous;
    }

    public void setPrevious(Node previous)
    {
        this.previous = previous;
    }

    public double getMinDistance()
    {
        return minDistance;
    }

    public void setMinDistance(double minDistance)
    {
        this.minDistance = minDistance;
    }

    @Override
    public int compareTo(Node otherNode)
    {
        return Double.compare(this.minDistance, otherNode.getMinDistance());
    }

    @Override
    public boolean equals(Object o){
        if(o == null)
        {
            return false;
        }

        Node other = (Node) o;
        if(!this.getNodeText().equals(other.getNodeText()))
        {
            return false;
        }

        if (!this.getNodePosTag().contains(other.getNodePosTag()) || !other.getNodePosTag().contains(this.getNodePosTag()))
        {
            return false;
        }

        return true;
    }
}
