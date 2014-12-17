package com.iris.rainbow.article;

import com.iris.rainbow.db.UnprocessedArticleDaoImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;

public class HeadlineGenerator
{

    private Logger logger = LogManager.getLogger(UnprocessedArticleDaoImpl.class.getName());

    /**
     * Uses DirectedGraph functionality from JGraphT to construct a word-graph
     * from the list of strings passed to the method, will then return the
     * shortest route through the word-graph, which should typically be the
     * shortest legible sentence.
     *
     * @param  headlines  A List of headlines to generate the aggregate headline.
     *
     * @return  The shortest possible headline based on the shortest route through a Directed Word graph of headlines.
     */
    public String GenerateAggregateHeadline(List<String> headlines)
    {
        List<String> startNodes = gatherStartNodes(headlines);
        List <String> endNodes = gatherEndNodes(headlines);

        DirectedGraph<String, DefaultEdge> wordGraph = constructWordgraph(headlines);

        List headlinePath = findShortestPath(wordGraph, startNodes, endNodes);

        StringBuilder headline = new StringBuilder();
        for (int i=0; i < headlinePath.size(); i++)
        {
            if (i == headlinePath.size()-1)
            {
                String edge = headlinePath.get(i).toString();
                edge = edge.replace("(", "");
                edge = edge.replace(":", "");
                edge = edge.replace(")", "");
                headline.append(edge);

                continue;
            }

            String edge = headlinePath.get(i).toString();
            String formattedHeadline = edge.substring(0, edge.lastIndexOf(":"));
            headline.append(formattedHeadline.replace("(",""));
        }

        return headline.toString();
    }

    /**
     * Gathers a list of words that strings end on from a list of sentences, used for determining
     * the shortest path in combination with startNodes.
     *
     * @param  headlines  A List of headlines to gather end-words from.
     *
     * @return  A list of the words that headlines end on.
     */
    private List<String> gatherEndNodes(List<String> headlines)
    {
        List<String> endNodes = new ArrayList<String>();

        for (String headline: headlines)
        {
            String[] headlineWords = headline.split("\\s+");
            if (headlineWords.length > 0)
            {
                endNodes.add(headlineWords[headlineWords.length -1]);
            }
        }

        return endNodes;
    }

    /**
     * Gathers a list of words that strings start on from a list of sentences, used for determining
     * the shortest path in combination with endNodes.
     *
     * @param  headlines  A List of headlines to gather end-words from.
     *
     * @return  A list of the words that headlines start on.
     */
    private List<String> gatherStartNodes(List<String> headlines)
    {
        List<String> startNodes = new ArrayList<String>();

        for (String headline: headlines)
        {
            String[] headlineWords = headline.split("\\s+");
            if (headlineWords.length > 0)
            {
                startNodes.add(headlineWords[0]);
            }
        }

        return startNodes;
    }


    /**
     * Checks each combination of start-word & end-word against the word graph to determine which route is the shortest
     *
     * @param  wordGraph  A Directed word graph generated from all the headlines in question.
     * @param  startNodes  A List of words that headlines start upon.
     * @param  endNodes  A List of words that headlines end upon.
     *
     * @return  A list of words which contains the shortest path through the word graph.
     */
    private List findShortestPath(DirectedGraph<String, DefaultEdge> wordGraph, List<String> startNodes, List<String> endNodes)
    {
        List shortestPath = new ArrayList();

        for (String startNode: startNodes)
        {
            for (String endNode: endNodes)
            {
                List wordPath = DijkstraShortestPath.findPathBetween(wordGraph, startNode, endNode);

                if (wordPath != null )
                {
                    if (shortestPath.size() == 0 && wordPath.size() > 0)
                    {
                        shortestPath = wordPath;
                    }
                    else if (wordPath.size() > 0 && wordPath.size() < shortestPath.size())
                    {
                        shortestPath = wordPath;
                    }
                }
            }
        }


        return shortestPath;
    }

    /**
     * Takes a list of sentences and turns them into a directed word graph for analyzing
     *
     * @param  headlines  The news headlines that we need to combine to generate an aggregate headline.
     * @return  A directed word graph constructed using JGraphT containing all words in the headlines entered.
     */
    private DirectedGraph<String,DefaultEdge> constructWordgraph(List<String> headlines)
    {

        DirectedGraph<String, DefaultEdge> wordGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        for (String headline: headlines)
        {
            String[] headlineWords = headline.split("\\s+");

            for (int i=0; i < headlineWords.length; i++)
            {

                if (!wordGraph.containsVertex(headlineWords[i]))
                {
                    wordGraph.addVertex(headlineWords[i]);
                    if (i != 0 )
                    {
                        wordGraph.addEdge(headlineWords[i-1], headlineWords[i]);
                    }
                }
            }
        }

        return wordGraph;
    }

}
