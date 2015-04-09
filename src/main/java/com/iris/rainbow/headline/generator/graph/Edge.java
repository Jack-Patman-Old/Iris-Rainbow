package com.iris.rainbow.headline.generator.graph;

/**
 * Created by Jack on 18/03/2015.
 */
public class Edge
{
    private Node connectedNode;
    public double edgeWeight = 0;

    public Node getConnectedNode()
    {
        return connectedNode;
    }

    public void setConnectedNode(Node connectedNode)
    {
        this.connectedNode = connectedNode;
    }

    public double getEdgeWeight()
    {
        return edgeWeight;
    }

    public void setEdgeWeight(double weight)
    {
        edgeWeight = weight;
    }

    public void incrementEdgeWeight()
    {
        edgeWeight++;
    }
}
