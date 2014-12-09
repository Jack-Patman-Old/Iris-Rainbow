package com.iris.rainbow.reader;

import com.iris.rainbow.article.ProcessedArticle;
import com.iris.rainbow.article.RelatedArticleFinder;
import com.iris.rainbow.article.UnprocessedArticle;
import com.iris.rainbow.db.ProcessedArticleDao;
import com.iris.rainbow.db.ProcessedArticleDaoImpl;
import com.iris.rainbow.db.UnprocessedArticleDao;
import com.iris.rainbow.db.UnprocessedArticleDaoImpl;

import java.util.List;


public class ArticleReader
{

    public static void main(String[] args)
    {

        UnprocessedArticleDao unprocessedArticleDb = new UnprocessedArticleDaoImpl();
        List<UnprocessedArticle> unprocessedArticles = unprocessedArticleDb.GetUnprocessedArticles();

        RelatedArticleFinder finder = new RelatedArticleFinder();
        List<ProcessedArticle> processedArticles = finder.processArticles(unprocessedArticles);

        ProcessedArticleDao processedArticleDb = new ProcessedArticleDaoImpl();
        boolean successfulWrite = processedArticleDb.WriteArticlesToDb(processedArticles);

        if (successfulWrite)
        {
            unprocessedArticleDb.RemoveUnprocessedArticles(unprocessedArticles);
        }
    }

}
