package com.iris.rainbow.db;

import com.iris.rainbow.article.ProcessedArticle;
import com.iris.rainbow.article.UnprocessedArticle;

import java.util.List;

public interface UnprocessedArticleDao
{
    /**
     * Loads a list of all UnprocessedArticles currently waiting in the database that were
     * added to the database within the last one hour.
     *
     *
     * @return List of all UnprocessedArticles added to the database within the last hour.
     */
    public List<UnprocessedArticle> GetUnprocessedArticles();


    /**
     * Takes a list of UnprocessedArticles that have been Processed and removes them from the Database to
     * avoid repeat processing of articles.
     *
     * @param  processedArticles A list of UnprocessedArticles that have been succesfully Processed.
     */
    public void RemoveUnprocessedArticles(List<UnprocessedArticle> processedArticles);
}
