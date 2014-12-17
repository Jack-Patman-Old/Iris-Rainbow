package com.iris.rainbow.db;

import com.iris.rainbow.article.ProcessedArticle;

import java.util.List;

public interface ProcessedArticleDao
{
    /**
     * Attempts to Write a list of ProcessedArticles to the database
     *
     * @return True or false depending on if the write was successful or not.
     */
    public boolean WriteArticlesToDb(List<ProcessedArticle> processedArticles);
}
