package com.iris.rainbow.reader;

import com.iris.rainbow.article.ProcessedArticle;
import com.iris.rainbow.article.UnprocessedArticle;
import com.iris.rainbow.db.UnprocessedArticleDao;
import com.iris.rainbow.db.UnprocessedArticleDaoImpl;

import java.util.ArrayList;
import java.util.List;

public class ArticleReader
{

    public static void main(String[] args)
    {
        UnprocessedArticleDao unprocessedArticleDao = new UnprocessedArticleDaoImpl();

        List<UnprocessedArticle> unprocessedArticles = unprocessedArticleDao.GetUnprocessedArticles();
        List<ProcessedArticle> processedArticles = ProcessArticles(unprocessedArticles);


        /* ProcessedArticleDao processedArticleDao = new ProcessedArticleDaoImpl();
        * boolean successfullyProcessedArticles = processedArticleDao.WriteProcessedArticles(processedArticle);
        * if (successfullyProcessedArticles)
        * {
        *   unprocessedArticleDao.RemoveUnprocessedArticles(processedArticle);
        * }
        */
    }


    private static List<ProcessedArticle> ProcessArticles(List<UnprocessedArticle> unprocessdArticle)
    {
        return null;
    }

    private static List<UnprocessedArticle> LoadUnprocessedArticles()
    {
        UnprocessedArticleDao unprocessedArticleDao = new UnprocessedArticleDaoImpl();
        return unprocessedArticleDao.GetUnprocessedArticles();
    }
}
