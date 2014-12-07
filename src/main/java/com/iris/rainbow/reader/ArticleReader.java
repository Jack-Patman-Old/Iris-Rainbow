package com.iris.rainbow.reader;

import com.iris.rainbow.article.ProcessedArticle;
import com.iris.rainbow.article.RelatedArticleFinder;
import com.iris.rainbow.article.UnprocessedArticle;
import com.iris.rainbow.db.UnprocessedArticleDao;
import com.iris.rainbow.db.UnprocessedArticleDaoImpl;

import java.util.List;


public class ArticleReader
{

    public static void main(String[] args)
    {

        UnprocessedArticleDao unprocessedArticleDao = new UnprocessedArticleDaoImpl();

        List<UnprocessedArticle> unprocessedArticles = unprocessedArticleDao.GetUnprocessedArticles();

        RelatedArticleFinder finder = new RelatedArticleFinder();
        List<ProcessedArticle> processedArticles = finder.processArticles(unprocessedArticles);

        /* boolean successfullyProcessedArticles = processedArticleDao.WriteProcessedArticles(processedArticle);
        * if (successfullyProcessedArticles)
        * {
        *   unprocessedArticleDao.RemoveUnprocessedArticles(processedArticle);
        * }
        */
    }

}
