package com.iris.rainbow.db;

import com.iris.rainbow.article.ProcessedArticle;
import com.iris.rainbow.article.UnprocessedArticle;

import java.util.List;

public interface UnprocessedArticleDao
{
    public List<UnprocessedArticle> GetUnprocessedArticles();

    public void RemoveUnprocessedArticles(List<UnprocessedArticle> processedArticles);
}
