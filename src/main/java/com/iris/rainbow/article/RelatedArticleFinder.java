package com.iris.rainbow.article;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RelatedArticleFinder
{
    private List<String> ignoredTerms;


    /**
     * Takes a list of Unprocessed Articles, then for each article searches for articles that are related
     * (In this context - Articles that cover the same story), will then generate a ProcessedArticle containing
     * a list of all Urls from related articles.
     *
     * @param unprocessedArticles A list of unprocessedArticles taken fresh from RssFeeds
     * @return A list of processedArticles containing urls linking them to related articles.
     */
    public List<ProcessedArticle> processArticles(List<UnprocessedArticle> unprocessedArticles)
    {
        ignoredTerms = loadIgnoredTerms();

        List<ProcessedArticle> processedArticles = new ArrayList<ProcessedArticle>();
        List<UnprocessedArticle> matches = new ArrayList<UnprocessedArticle>();

        List<String> matchingHeadlines = new ArrayList<String>();
        List<String> matchingDescriptions = new ArrayList<String>();

        for (UnprocessedArticle originalArticle : unprocessedArticles)
        {

            if (matchingHeadlines.contains(originalArticle.getHeadline()))
            {
                continue;
            }

            // Ensure that original article is no longer part of the search candidates

            for (UnprocessedArticle comparisonArticle : unprocessedArticles)
            {
                if (matchingHeadlines.contains(comparisonArticle.getHeadline()))
                {
                    continue;
                }

                if (articlesAreRelated(originalArticle, comparisonArticle, matchingDescriptions, matchingHeadlines))
                {
                    matchingHeadlines.add(comparisonArticle.getHeadline());
                    matchingDescriptions.add(comparisonArticle.getDescription());
                    matches.add(comparisonArticle);
                }
            }


            if (matches.size() > 0)
            {
                List<Integer> urlIds = new ArrayList<Integer>();
                List<String> urls = new ArrayList<String>();
                List<String> headlines = new ArrayList<String>();

                urlIds.add(originalArticle.getUrlId());
                urls.add(originalArticle.getUrl());
                headlines.add(originalArticle.getHeadline());

                UnprocessedArticle firstMatch = matches.get(0);

                for (Iterator<UnprocessedArticle> matchesIterator = matches.iterator(); matchesIterator.hasNext(); )
                {
                    UnprocessedArticle article = matchesIterator.next();
                    headlines.add(article.getHeadline());
                    urls.add(article.getUrl());
                    urlIds.add(article.getUrlId());
                    matchesIterator.remove();
                }

                processedArticles.add(new ProcessedArticle(0, headlines, firstMatch.getDescription(), firstMatch.getPublicationDate(), urlIds, urls));
            }

        }

        return processedArticles;
    }

    /**
     * Loads a text file containing a list  of terms to ignore when searching for headline matches from the
     * classpath, will then return this as a List of words that can be used during the matching process.
     *
     * @return A List of words that can be ignored during the matching process.
     */
    private List<String> loadIgnoredTerms()
    {
        List<String> result = new ArrayList<String>();

        BufferedReader reader;

        try
        {

            InputStream in = getClass().getResourceAsStream("/ignoredMatchTerms.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            String currentLine;

            while ((currentLine = reader.readLine()) != null)
            {
                result.add(currentLine.toLowerCase());
            }

            reader.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;

    }


    /**
     * Takes the original article, the comparison article and a list of existing matches and then searches for new matches
     * by comparing the word volume between the original articles headline and the comparison article headline, and
     * then checking to make sure the article/headline have not already been matched to another article. Currently
     * the articles require 75% similarity, however 100% matches will be ignored under the assumption that it is a
     * repeat article.
     *
     * @param originalArticle      The original article that you wish to compare against.
     * @param comparisonArticle    The article that you wish to compare your original article against.
     * @param matchingDescriptions A list of Article descriptions that you have already managed to match up to other articles.
     * @param matchingHeadlines    A list of Article Headlines that you have already managed to match up to other articles.
     * @return A boolean value specifying whether the articles are related or not.
     */
    private boolean articlesAreRelated(UnprocessedArticle originalArticle, UnprocessedArticle comparisonArticle, List<String> matchingDescriptions, List<String> matchingHeadlines)
    {
        // App will only support one news article per outlet for a select topic.
        if (comparisonArticle.getFeedId() == originalArticle.getFeedId())
        {
            return false;
        }

        // Avoid headlines that link back to the same url.
        if (comparisonArticle.getUrl().contains(originalArticle.getUrl()))
        {
            return false;
        }

        // Avoid articles that contain no description for comparison.
        if (comparisonArticle.getDescription() == null || originalArticle.getDescription() == null)
        {
            return false;
        }

        // Avoid articles with EXACTLY the same text.
        if (comparisonArticle.getDescription().equals(originalArticle.getDescription()) || comparisonArticle.getHeadline().equals(originalArticle.getHeadline()))
        {
            return false;
        }

        // Avoid articles that have already been procesed
        if (matchingDescriptions.contains(comparisonArticle.getDescription()) || matchingHeadlines.contains(comparisonArticle.getHeadline()))
        {
            return false;
        }

        double headlineSimilarity = CompareWordVolumes(originalArticle.getHeadline(), comparisonArticle.getHeadline());

        if (headlineSimilarity > 0.25)
        {
            return true;
        }
        
        return false;
    }


    /**
     * Works out percentage difference between two texts based on word Volume
     *
     * @param originalText Original text for comparison
     * @param comparedText Secondary text provided for comparison
     * @return Percentage difference between the two strings of text
     */
    private double CompareWordVolumes(String originalText, String comparedText)
    {
        String originalHeadline = originalText.toLowerCase();
        String comparisonHeadline = comparedText.toLowerCase();

        for (String text : ignoredTerms)
        {
            String filteredTerm = text.toLowerCase();
            comparisonHeadline = comparisonHeadline.replaceAll(filteredTerm, " ");
            comparisonHeadline = comparisonHeadline.replace(filteredTerm, " ");
            originalHeadline = originalHeadline.replaceAll(filteredTerm, " ");
            originalHeadline = originalHeadline.replace(filteredTerm, " ");
        }

        String[] originalWords = originalHeadline.split("\\s+");
        String[] comparativeWords = comparisonHeadline.split("\\s+");

        int wordMatches = 0;

        for (String originalWord : originalWords)
        {
            for (String comparativeWord : comparativeWords)
            {
                if (originalWord.toLowerCase().equals(comparativeWord.toLowerCase()))
                {
                    wordMatches++;
                }
            }
        }


        BigDecimal matches = new BigDecimal(wordMatches);
        BigDecimal totalCharacters = new BigDecimal(originalWords.length);

        BigDecimal similarity = matches.divide(totalCharacters, 100, RoundingMode.CEILING);

        double percentageSimilarity = similarity.doubleValue();
        return percentageSimilarity;
    }
}
