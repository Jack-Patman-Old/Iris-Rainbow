package com.iris.rainbow.article.grouper;

import com.iris.rainbow.article.UnprocessedArticle;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RelatedArticleFinder
{
    private List<UnprocessedArticle> articles;


    public RelatedArticleFinder(List<UnprocessedArticle> articles)
    {
        this.articles = articles;
    }

    public List<List<UnprocessedArticle>> groupArticles()
    {
        List<String> processedUrls = new ArrayList<String>();

        List<List<UnprocessedArticle>> groupedArticles = new ArrayList<List<UnprocessedArticle>>();

        WordDirectory directory = new WordDirectory();
        for (UnprocessedArticle article : articles)
        {
            directory.addHeadline(article);
        }

        for (UnprocessedArticle article : articles)
        {
            if (processedUrls.contains(article.getUrl()))
            {
                continue;
            }

            List<UnprocessedArticle> articleGroup = new ArrayList<UnprocessedArticle>();

            List<String> nouns = directory.getNouns(article.getHeadline());
            for (String noun : nouns)
            {
                if (directory.getDirectory().get(noun) == null)
                {
                    continue;
                }

                HashMap<String, List<UnprocessedArticle>> initialEntry = directory.getDirectory().get(noun);

                for (String secondaryNoun : nouns)
                {
                    if (initialEntry.get(secondaryNoun) == null)
                    {
                        continue;
                    }

                    for (UnprocessedArticle addedArticle : initialEntry.get(secondaryNoun))
                    {
                        if (!processedUrls.contains(addedArticle.getUrl()))
                        {
                            articleGroup.add(addedArticle);
                            processedUrls.add(addedArticle.getUrl());
                        }
                    }

                }
            }

            if (articleGroup.size() > 1)
            {
                groupedArticles.add(articleGroup);
            }
        }


        return groupedArticles;
    }

}
