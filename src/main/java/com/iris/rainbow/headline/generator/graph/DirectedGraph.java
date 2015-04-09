package com.iris.rainbow.headline.generator.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Jack on 18/03/2015.
 */
public class DirectedGraph
{
    List<Node> nodes = new ArrayList<Node>();
    List<Node> startNodes = new ArrayList<Node>();
    List<Node> endNodes = new ArrayList<Node>();
    List<List<Node>> shortestRoutes = new ArrayList<List<Node>>();


    /**
     * Adds a new node (or vertex) to the Directed Graph - or if node already exists,
     * appends any new properties of the node to the existing node.
     *
     * @param  node The node to be added to the directed graph.
     */
    public void addNode(Node node)
    {
        if (node != null)
        {
            if (!NodeExists(node))
            {
                nodes.add(node);
                if (node.isEndNode())
                {
                    endNodes.add(node);
                }

                if (node.isStartNode())
                {
                    startNodes.add(node);
                }

            } else
            {
                Node existingNode = findNode(node.getNodeText(), node.getNodePosTag());

                if (node.isEndNode() && !endNodes.contains(node))
                {
                    endNodes.add(existingNode);
                    existingNode.setIsEndNode(true);
                }

                if (node.isStartNode() && !endNodes.contains(node))
                {
                    startNodes.add(existingNode);
                    existingNode.setIsStartNode(true);
                }
            }
        }
    }

    /**
     * Attempts to find a node in the Directed Graph with the same text and
     * part-of-speech (POS) tag.
     *
     * @param  nodeText The text of the node that you wish to find.
     * @param  nodeTag The part-of-speech (POS) tag of the node that you wish to find.
     *
     * @return A node matching the node text &  tag entered, if none are found - returns null.
     */
    public Node findNode(String nodeText, String nodeTag)
    {
        for (Node node : nodes)
        {
            if (node.getNodeText().equals(nodeText.toLowerCase()))
            {
                if (node.getNodePosTag().contains(nodeTag) || nodeTag.contains(node.getNodePosTag()))
                {
                    return node;
                }
            }
        }

        return null;
    }

    /**
     * Checks whether a given node already exists in the dircted graph,
     * based on the text and part-of-speech (POS) tag of that node.
     *
     * @param  comparisonNode The node whose existence we wish to check.
     *
     * @return True or False depending on whether the node exists.
     */
    public boolean NodeExists(Node comparisonNode)
    {
        for (Node node : nodes)
        {
            if (node.getNodePosTag().contains(comparisonNode.getNodePosTag())  || comparisonNode.getNodePosTag().contains(node.getNodePosTag()))
            {
                if (node.getNodeText().equals(comparisonNode.getNodeText()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Uses a custom implementation of Djikstras Shortest Path algorithm to return a list of the shortest
     * route through the directed graph. Additional conditions such as requiring 3 words, one of which
     * must be a verb, are applied during the process.
     *
     * @return The shortest path as a list of nodes, in sequential order.
     */
    public List<Node> findShortestRoutes()
    {
        for (Node startNode : startNodes)
        {
            for (Node endNode : endNodes)
            {
                startNode.setMinDistance(0);

                PriorityQueue<Node> nodeQueue = new PriorityQueue<Node>();
                nodeQueue.add(startNode);

                while (!nodeQueue.isEmpty())
                {
                    Node currentNode = nodeQueue.poll();

                    for (Edge edge : currentNode.getEdges())
                    {
                        Node edgeNode = edge.getConnectedNode();

                        double distanceThroughNodes = currentNode.getMinDistance();
                        if (distanceThroughNodes < edgeNode.getMinDistance())
                        {
                            nodeQueue.remove(edgeNode);
                            edgeNode.setMinDistance(distanceThroughNodes);
                            edgeNode.setPrevious(currentNode);
                            nodeQueue.add(edgeNode);

                            if (edgeNode.equals(endNode))
                            {
                                endNode.setPrevious(currentNode);
                                nodeQueue.clear();
                                break;
                            }
                        }
                    }
                }
                List<Node> path = new ArrayList<Node>();

                for (Node targetNode = endNode; targetNode != null; targetNode = targetNode.getPrevious())
                {
                    path.add(targetNode);
                }

                Collections.reverse(path);
                shortestRoutes.add(path);


                // reset minimum distances ready for next iteration
                for (Node node : nodes)
                {
                    node.setMinDistance(Integer.MAX_VALUE);
                    node.setPrevious(null);
                }
            }
        }


        return shortestValidRoute();
    }


    private List<Node> shortestValidRoute()
    {
        if (!shortestRoutes.isEmpty())
        {
            List<Node> bestRoute = new ArrayList<Node>();

            for (List<Node> route : shortestRoutes)
            {
                if (bestRoute == null || bestRoute.isEmpty())
                {
                    if (pathIsValid(shortestRoutes.get(0)))
                    {
                        bestRoute = shortestRoutes.get(0);
                    }
                }

                if (pathIsValid(route))
                {
                    bestRoute = findBestRoute(route, bestRoute);
                }
            }

            return bestRoute;
        }

        return null;
    }

    /* Compares the weighting of two routes to determine which route is best - based on considerations
       such as commonly used terms in headlines.
     */
    private List<Node> findBestRoute(List<Node> comparisonRoute, List<Node> bestRoute)
    {
        double routeOneWeight = 0;
        double routeTwoWeight = 0;

        for (int i=0; i < comparisonRoute.size()-1; i++)
        {
            routeOneWeight++;
            Edge edge = (comparisonRoute.get(i).getEdge(comparisonRoute.get(i+1).getNodeText(), comparisonRoute.get(i+1).getNodePosTag()));
            routeOneWeight -= edge.getEdgeWeight();
        }

        for (int i=0; i < bestRoute.size()-1; i++)
        {
            routeTwoWeight++;
            Edge edge = (bestRoute.get(i).getEdge(bestRoute.get(i+1).getNodeText(), bestRoute.get(i+1).getNodePosTag()));
            routeTwoWeight -= edge.getEdgeWeight();
        }

        if (routeOneWeight < routeTwoWeight)
        {
            return comparisonRoute;
        }
        else
        {
            return bestRoute;
        }
    }

    public List<Node> getNodes()
    {
        return nodes;
    }

    public void setNodes(List<Node> nodes)
    {
        this.nodes = nodes;
    }

    /* Checks whether path contains one verb and has more than three words - conditions
    specified in Filippova 2010 */
    private boolean pathIsValid(List<Node> currentPath)
    {
        boolean sentenceContainsVerb = false;

        for (Node node : currentPath)
        {
            if (node.getNodePosTag().contains("V"))
            {
                sentenceContainsVerb = true;
            }
        }

        if (currentPath.size() >= 3 && sentenceContainsVerb)
        {
            return true;
        }
        return false;
    }
}

