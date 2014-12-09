package com.iris.rainbow.db;

import com.iris.rainbow.article.ProcessedArticle;

import java.util.List;

public interface ProcessedArticleDao
{
    public boolean WriteArticlesToDb(List<ProcessedArticle> processedArticles);
}
