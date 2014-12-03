package com.iris.rainbow.reader;

import com.iris.rainbow.article.ProcessedArticle;
import com.iris.rainbow.article.UnprocessedArticle;
import com.iris.rainbow.db.UnprocessedArticleDao;
import com.iris.rainbow.db.UnprocessedArticleDaoImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArticleReader
{

    // 21 Representing 21 Days - Three weeks search limit on article publication date

    public static void main(String[] args)
    {
        ArticleReader reader = new ArticleReader();

        UnprocessedArticleDao unprocessedArticleDao = new UnprocessedArticleDaoImpl();

        List<UnprocessedArticle> unprocessedArticles = unprocessedArticleDao.GetUnprocessedArticles();
        List<ProcessedArticle> processedArticles = reader.ProcessArticles(unprocessedArticles);
        System.out.println("");

        /* ProcessedArticleDao processedArticleDao = new ProcessedArticleDaoImpl();
        * boolean successfullyProcessedArticles = processedArticleDao.WriteProcessedArticles(processedArticle);
        * if (successfullyProcessedArticles)
        * {
        *   unprocessedArticleDao.RemoveUnprocessedArticles(processedArticle);
        * }
        */
    }

    private static int minimum(int a, int b, int c)
    {
        return Math.min(Math.min(a, b), c);
    }

    /**
     * Finds any articles that cover the same story in UnprocessedArticles, creates a processedArticle object containing this information
     *
     * @param unprocessedArticles All unprocessedArticles
     * @return All unprocessedArticles within the period of ARTICLE_SEARCH_LIMIT to Current Date
     */
    private List<ProcessedArticle> ProcessArticles(List<UnprocessedArticle> unprocessedArticles)
    {

        List<ProcessedArticle> processedArticles = new ArrayList<>();
        List<UnprocessedArticle> matches = new ArrayList<>();


        for (Iterator<UnprocessedArticle> iteratorOne = unprocessedArticles.iterator(); iteratorOne.hasNext();)
        {
            List<String> matchingHeadlines = new ArrayList<>();
            List<String> matchingDescriptions = new ArrayList<>();

            UnprocessedArticle originalArticle = iteratorOne.next();
            // Ensure that original article is no longer part of the search candidates

            for (Iterator<UnprocessedArticle> iteratorTwo = unprocessedArticles.iterator(); iteratorTwo.hasNext();)
            {
                UnprocessedArticle comparisonArticle = iteratorTwo.next();

                if (comparisonArticle.getFeedId() != originalArticle.getFeedId())
                {
                    if (!comparisonArticle.getDescription().equals(originalArticle.getDescription()) && !comparisonArticle.getHeadline().equals(originalArticle.getHeadline()))
                    {
                        if (!matchingDescriptions.contains(comparisonArticle.getDescription()) && !matchingHeadlines.contains(comparisonArticle.getHeadline()))
                        {
                            BigDecimal headlineDifference = CompareTextDistance(originalArticle.getHeadline(), comparisonArticle.getHeadline());
                            BigDecimal contentDifference = CompareTextDistance(originalArticle.getDescription(), comparisonArticle.getDescription());

                            if (headlineDifference.doubleValue() < 0.20 || contentDifference.doubleValue() < 0.20)
                            {
                                matchingHeadlines.add(comparisonArticle.getHeadline());
                                matchingDescriptions.add(comparisonArticle.getDescription());
                                matches.add(comparisonArticle);
                            }
                        }
                    }
                }
            }

            if (matches.size() > 0)
            {
                List<Integer> urlIds = new ArrayList<>();
                urlIds.add(originalArticle.getUrlId())
                ;
                UnprocessedArticle firstMatch = matches.get(0);

                for (Iterator<UnprocessedArticle> matchesIterator = matches.iterator(); matchesIterator.hasNext();)
                {
                    UnprocessedArticle article = matchesIterator.next();

                    urlIds.add(article.getUrlId());
                    matchesIterator.remove();
                }

                processedArticles.add(new ProcessedArticle(0, firstMatch.getHeadline(),firstMatch.getDescription(), firstMatch.getPublicationDate(), urlIds));
            }

        }

        return processedArticles;
    }

    /**
     * Works out percentage difference between two texts based on Levenshtein distance algorithm
     *
     * @param originalText Original text for comparison
     * @param comparedText Secondary text provided for comparison
     * @return Percentage difference between the two strings of text
     */
    private BigDecimal CompareTextDistance(String originalText, String comparedText)
    {
        originalText = originalText.toLowerCase();
        comparedText = comparedText.toLowerCase();

        int[] costs = new int[comparedText.length() + 1];

        for (int j = 0; j < costs.length; j++)
        {
            costs[j] = j;
        }

        for (int i = 1; i <= originalText.length(); i++)
        {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= comparedText.length(); j++)
            {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), originalText.charAt(i - 1) == comparedText.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }

        BigDecimal difference = new BigDecimal(costs[comparedText.length()]);
        BigDecimal totalCharacters = new BigDecimal(originalText.length() + comparedText.length());

        BigDecimal similarity = difference.divide(totalCharacters, 100, RoundingMode.CEILING);

        return similarity;
    }
}
