package com.iris.rainbow.headline.generator.sentencecompression;

import com.iris.rainbow.headline.generator.graph.DirectedGraph;
import com.iris.rainbow.headline.generator.graph.Edge;
import com.iris.rainbow.headline.generator.graph.Node;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class CompressedSentenceGenerator
{
    private Tokenizer tokenizer;
    private POSTaggerME posTagger;
    private DirectedGraph graph = new DirectedGraph();

    /**
     * Builds a directed word graph based on any number of sentences
     * entered into it. Will automatically add part-of-speech (POS)
     * tagging to each word entered into the directed graph.
     *
     * @param  sentences The sentences you wish to build the directed word graph from.
     */
    public CompressedSentenceGenerator(String[] sentences) throws IOException
    {

        InputStream tokenizerModelLoc = getClass().getResourceAsStream("/en-token.bin");
        TokenizerModel tokenizerModel = new TokenizerModel(tokenizerModelLoc);
        tokenizer = new TokenizerME(tokenizerModel);

        InputStream posModelLoc = getClass().getResourceAsStream("/en-pos-maxent.bin");
        POSModel posModel = new POSModel(posModelLoc);
        posTagger = new POSTaggerME(posModel);

        addWordsToGraph(sentences);
        createEdges(sentences);

    }

    /**
     * Based on the contents of the wordgraph, will use Djikstras shortest route and a number of
     * additional comparisons to determine the most appropriate route to make, and create a
     * compressed headline from said route.
     *
     * @return A (potentially) compressed headline generated from the sum of all headlines. May sometimes
     * opt to just use an existing headline, as that solution is already the best.
     */
    public String createCompressedSentence()
    {

        List<Node> shortestPath = graph.findShortestRoutes();
        if (shortestPath != null && shortestPath.size() > 0)
        {

            StringBuilder headline = new StringBuilder();
            for (Node node : shortestPath)
            {
                headline.append(node.getNodeText());
                headline.append(" ");
            }


            return headline.toString().trim();
        }

        return null;
    }


    private void createEdges(String[] sentences) throws IOException
    {
        for (String sentence : sentences)
        {
            String words[] = tokenizer.tokenize(sentence);
            String tags[] = posTagger.tag(words);

            if (tags.length != words.length)
            {
                continue;
            }

            for (int i = 0; i < words.length; i++)
            {
                if (i < words.length-1)
                {
                    Node originalNode = graph.findNode(words[i], tags[i]);

                    if (originalNode != null)
                    {
                        if (originalNode.edgeExists(words[i + 1], tags[i + 1]))
                        {
                            Edge edge = originalNode.getEdge(words[i + 1], tags[i + 1]);
                            edge.incrementEdgeWeight();
                        } else
                        {
                            Node nextNode = graph.findNode(words[i + 1], tags[i + 1]);
                            originalNode.addEdge(nextNode);
                        }
                    }
                }
            }
        }
    }


    private void addWordsToGraph(String[] sentences) throws IOException
    {
        for (String sentence : sentences)
        {
            String words[] = tokenizer.tokenize(sentence);
            String tags[] = posTagger.tag(words);

            if (tags.length != words.length)
            {
                continue;
            }

            for (int i = 0; i < words.length; i++)
            {
                Node node = new Node();
                node.setNodeText(words[i]);
                node.setNodePosTag(tags[i]);

                if (i == 0)
                {
                    node.setIsStartNode(true);
                }

                if (i == words.length - 1)
                {
                    node.setIsEndNode(true);
                }

                graph.addNode(node);
            }
        }
    }

}
