package com.iris.rainbow.article;

import com.iris.rainbow.db.UnprocessedArticleDaoImpl;
import org.apache.logging.log4j.Level;
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

    private List<String> gatherEndNodes(List<String> headlines)
    {
        List<String> endNodes = new ArrayList<>();

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

    private List<String> gatherStartNodes(List<String> headlines)
    {
        List<String> startNodes = new ArrayList<>();

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

    private DirectedGraph<String,DefaultEdge> constructWordgraph(List<String> headlines)
    {

        DirectedGraph<String, DefaultEdge> wordGraph = new DefaultDirectedGraph< >(DefaultEdge.class);
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
