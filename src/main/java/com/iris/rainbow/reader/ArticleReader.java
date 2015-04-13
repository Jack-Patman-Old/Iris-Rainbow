package com.iris.rainbow.reader;

import com.iris.rainbow.article.ProcessedArticle;
import com.iris.rainbow.article.grouper.RelatedArticleFinder;
import com.iris.rainbow.article.UnprocessedArticle;
import com.iris.rainbow.db.ProcessedArticleDao;
import com.iris.rainbow.db.ProcessedArticleDaoImpl;
import com.iris.rainbow.db.UnprocessedArticleDao;
import com.iris.rainbow.db.UnprocessedArticleDaoImpl;
import com.iris.rainbow.headline.generator.sentencecompression.CompressedSentenceGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ArticleReader
{

    public static void main(String[] args)
    {
        try
        {
            // Read unprocessed articles from database
            UnprocessedArticleDao unprocessedArticleDb = new UnprocessedArticleDaoImpl();
            List<UnprocessedArticle> unprocessedArticles = unprocessedArticleDb.GetUnprocessedArticles();

            // Group articles
            RelatedArticleFinder grouper = new RelatedArticleFinder(unprocessedArticles);
            List<List<UnprocessedArticle>> groupedArticles = grouper.groupArticles();

            // Process into articles ready for reading
            List<ProcessedArticle> processedArticles = processArticles(groupedArticles);


            // Read articles back to db
            ProcessedArticleDao processedArticleDb = new ProcessedArticleDaoImpl();
            boolean successfulWrite = processedArticleDb.WriteArticlesToDb(processedArticles);


            if (successfulWrite)
            {
                // if succesfull, remove processed unprocessedArticles.
                unprocessedArticleDb.RemoveUnprocessedArticles(unprocessedArticles);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    private static List<ProcessedArticle> processArticles(List<List<UnprocessedArticle>> unprocessedArticles) throws IOException
    {
        List<ProcessedArticle> processedArticles = new ArrayList<ProcessedArticle>();

        for (List<UnprocessedArticle> groupedArticles : unprocessedArticles)
        {
            List<Integer> feedIds = new ArrayList<Integer>();
            List<Integer> urlIds = new ArrayList<Integer>();
            List<String> headlines = new ArrayList<String>();
            List<String> urls = new ArrayList<String>();
            CompressedSentenceGenerator generator = new CompressedSentenceGenerator(headlines.toArray(new String[headlines.size()]));
            ProcessedArticle processedArticle = new ProcessedArticle();

            for (UnprocessedArticle article : groupedArticles)
            {
                headlines.add(article.getHeadline());
                feedIds.add(article.getFeedId());
                urls.add(article.getUrl());
                urlIds.add(article.getUrlId());
            }


            String aggregateHeadline = generator.createCompressedSentence();
            if (aggregateHeadline != null && !aggregateHeadline.isEmpty())
            {
                processedArticle.setAggregateHeadline(aggregateHeadline);
            } else
            {
                processedArticle.setAggregateHeadline(groupedArticles.get(0).getHeadline());
            }

            processedArticle.setCategory(groupedArticles.get(0).getCategoryId());
            processedArticle.setDescription(groupedArticles.get(0).getDescription());
            processedArticle.setFeedIds(feedIds);
            processedArticle.setUrls(urls);
            processedArticle.setUrlIds(urlIds);
            processedArticle.setPublicationDate(groupedArticles.get(0).getPublicationDate());

            processedArticles.add(processedArticle);
        }

        return processedArticles;
    }


}
