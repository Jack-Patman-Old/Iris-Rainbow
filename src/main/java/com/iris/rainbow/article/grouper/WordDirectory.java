package com.iris.rainbow.article.grouper;

import com.iris.rainbow.article.UnprocessedArticle;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jack on 12/04/2015.
 */
public class WordDirectory
{
    private Map<String, HashMap<String, List<UnprocessedArticle>>> directory;
    private Tokenizer tokenizer;
    private POSTaggerME posTagger;

    public WordDirectory()
    {
        directory = new HashMap<String, HashMap<String, List<UnprocessedArticle>>>();

        try
        {
            InputStream tokenizerModelLoc = this.getClass().getResourceAsStream("/en-token.bin");
            TokenizerModel tokenizerModel = null;
            tokenizerModel = new TokenizerModel(tokenizerModelLoc);

            tokenizer = new TokenizerME(tokenizerModel);

            InputStream posModelLoc = getClass().getResourceAsStream("/en-pos-maxent.bin");
            POSModel posModel = null;

            posModel = new POSModel(posModelLoc);

            posTagger = new POSTaggerME(posModel);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<String> getNouns(String headline)
    {
        String words[] = tokenizer.tokenize(headline);
        String tags[] = posTagger.tag(words);
        ArrayList<String> nouns = new ArrayList<String>();

        for (int i = 0; i < tags.length; i++)
        {
            if (tags[i].contains("NN") && words.length >= i)
            {
                nouns.add(words[i].toLowerCase());
            }
        }

        return nouns;
    }


    public void addHeadline(UnprocessedArticle article)
    {

        List<String> filteredTerms = loadIgnoredTerms();
        List<String> nouns = getNouns(article.getHeadline());
        for (String filteredTerm: filteredTerms)
        {
            String newHeadline = article.getHeadline().toLowerCase().replace(filteredTerm, "");
            article.setHeadline(newHeadline);
        }
        for (String noun: nouns)
        {
            // If no entry exists for the noun
            if (!directoryEntryPartiallyExists(noun))
            {
                HashMap<String, List<UnprocessedArticle>> entry = new HashMap<String, List<UnprocessedArticle>>();

                for (String secondaryNoun: nouns)
                {
                    List<UnprocessedArticle> articles = new ArrayList<UnprocessedArticle>();
                    articles.add(article);
                    entry.put(secondaryNoun, articles);

                    directory.put(noun, entry);
                }
            }
            else
            {
                for (String secondaryNoun: nouns)
                {
                    if (noun.equals(secondaryNoun))
                    {
                        // Don't allow a noun to have itself as a child.
                        continue;
                    }

                    if (!directoryEntryExists(noun, secondaryNoun))
                    {
                        HashMap<String, List<UnprocessedArticle>> entry = directory.get(noun);
                        List<UnprocessedArticle> articles = new ArrayList<UnprocessedArticle>();
                        articles.add(article);
                        entry.put(secondaryNoun, articles);

                        directory.put(noun, entry);

                    }
                    else if (directoryEntryExists(noun, secondaryNoun))
                    {

                        HashMap<String, List<UnprocessedArticle>> entry = directory.get(noun);

                        List<UnprocessedArticle> articlesList = entry.get(secondaryNoun);
                        articlesList.add(article);

                    }
                }
            }
        }

    }

    private boolean directoryEntryPartiallyExists(String noun)
    {
        if (directory.get(noun) != null)
        {
            return true;
        }

        return false;
    }

    private boolean directoryEntryExists(String firstNoun, String secondNoun)
    {
        HashMap<String, List<UnprocessedArticle>> directoryEntry;
        directoryEntry = directory.get(firstNoun);

        if (directoryEntry != null && directoryEntry.get(secondNoun) != null)
        {
            return true;
        }

        return false;
    }

    private List<String> loadIgnoredTerms()
    {
        List<String> result = new ArrayList<String>();

        BufferedReader reader;

        try
        {

            InputStream in = getClass().getResourceAsStream("/ignoredMatchTerms.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            String currentLine;

            while ((currentLine = reader.readLine()) != null)
            {
                result.add(currentLine.toLowerCase());
            }

            reader.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;

    }

    public Map<String, HashMap<String, List<UnprocessedArticle>>> getDirectory()
    {
        return directory;
    }

    public Tokenizer getTokenizer()
    {
        return tokenizer;
    }

    public POSTaggerME getPosTagger()
    {
        return posTagger;
    }
}
