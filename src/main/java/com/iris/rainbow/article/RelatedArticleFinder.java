package com.iris.rainbow.article;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class RelatedArticleFinder
{
    private List<String> ignoredTerms;


    public List<ProcessedArticle> processArticles(List<UnprocessedArticle> unprocessedArticles)
    {
        ignoredTerms = loadIgnoredTerms();

        List<ProcessedArticle> processedArticles = new ArrayList<>();
        List<UnprocessedArticle> matches = new ArrayList<>();

        for (UnprocessedArticle originalArticle : unprocessedArticles)
        {
            List<String> matchingHeadlines = new ArrayList<>();
            List<String> matchingDescriptions = new ArrayList<>();

            // Ensure that original article is no longer part of the search candidates

            for (UnprocessedArticle comparisonArticle : unprocessedArticles)
            {
                if (articlesAreRelated(originalArticle, comparisonArticle, matchingDescriptions, matchingHeadlines))
                {
                    matchingHeadlines.add(comparisonArticle.getHeadline());
                    matchingDescriptions.add(comparisonArticle.getDescription());
                    matches.add(comparisonArticle);
                }
            }


            if (matches.size() > 0)
            {
                List<Integer> urlIds = new ArrayList<>();
                List<String> headlines = new ArrayList<>();

                urlIds.add(originalArticle.getUrlId());
                headlines.add(originalArticle.getHeadline());

                UnprocessedArticle firstMatch = matches.get(0);

                for (Iterator<UnprocessedArticle> matchesIterator = matches.iterator(); matchesIterator.hasNext(); )
                {
                    UnprocessedArticle article = matchesIterator.next();
                    headlines.add(article.getHeadline());
                    urlIds.add(article.getUrlId());
                    matchesIterator.remove();
                }

                processedArticles.add(new ProcessedArticle(0, headlines, firstMatch.getDescription(), firstMatch.getPublicationDate(), urlIds));
            }

        }

        return processedArticles;
    }

    private List<String> loadIgnoredTerms()
    {
        List<java.lang.String> result = new ArrayList<>();

        final java.lang.String workingDir = System.getProperty("user.dir");

        File file = new File(workingDir + "//src//resources//ignoredMatchTerms.txt");

        try (Scanner scanner = new Scanner(file))
        {

            while (scanner.hasNextLine())
            {
                java.lang.String line = scanner.nextLine();
                result.add(line.toLowerCase());
            }

            scanner.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;

    }


    private boolean articlesAreRelated(UnprocessedArticle originalArticle, UnprocessedArticle comparisonArticle, List<String> matchingDescriptions, List<String> matchingHeadlines)
    {
        if (comparisonArticle.getFeedId() != originalArticle.getFeedId())
        {
            if (!comparisonArticle.getDescription().equals(originalArticle.getDescription()) && !comparisonArticle.getHeadline().equals(originalArticle.getHeadline()))
            {
                if (!matchingDescriptions.contains(comparisonArticle.getDescription()) && !matchingHeadlines.contains(comparisonArticle.getHeadline()))
                {
                    double headlineDifference = CompareWordVolumes(originalArticle.getHeadline(), comparisonArticle.getHeadline());

                    if (headlineDifference > 0.65)
                    {
                        return true;
                    }
                }
            }
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
        originalText = originalText.toLowerCase();
        comparedText = comparedText.toLowerCase();

        String[] originalWords = originalText.split("\\s+");
        String[] comparitiveWords = comparedText.split("\\s+");

        int wordMatches = 0;

        for (String originalWord : originalWords)
        {
            if (!ignoredTerms.contains(originalWord))
            {
                for (String comparativeWord : comparitiveWords)
                {
                    if (originalWord.toLowerCase().equals(comparativeWord.toLowerCase()))
                    {
                        wordMatches++;
                    }
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
