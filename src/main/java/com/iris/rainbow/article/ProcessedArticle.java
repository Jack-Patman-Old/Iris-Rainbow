package com.iris.rainbow.article;

import com.iris.rainbow.headline.generator.sentencecompression.CompressedSentenceGenerator;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

public class ProcessedArticle
{
    private int category;
    private List<Integer> feedIds;
    private String aggregateHeadline;
    private String description;
    private Date publicationDate;
    private List<Integer> urlIds;
    private List<String> urls;

    /* A Processed Article represents a grouped set of headlines ready to
       be entered back into the Db  */
    public ProcessedArticle(int categoryId, List<Integer>feedIds, List<String> headlines, String description, Date publicationDate, List<Integer> urlIds, List<String> urls)
    {
        CompressedSentenceGenerator generator = null;
        try
        {
            // Attempt to generate an aggregate headline from the headlines of all grouped articles.
            String[] headlinesArr = headlines.toArray(new String[headlines.size()]);
            generator = new CompressedSentenceGenerator(headlinesArr);
            String aggregateSentence = generator.createCompressedSentence();
            if (aggregateSentence == null || aggregateSentence.isEmpty())
            {
                this.aggregateHeadline = headlines.get(0);
            }
            else
            {
                this.aggregateHeadline = generator.createCompressedSentence();
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        this.feedIds = feedIds;
        this.category = categoryId;
        this.description = description;
        this.publicationDate = publicationDate;
        this.urlIds = urlIds;
        this.urls = urls;
    }

    public int getCategory()
    {
        return category;
    }

    public List<Integer> getFeedIds() {
        return feedIds;
    }

    public String getHeadline()
    {
        return aggregateHeadline;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getPublicationDate()
    {
        return publicationDate;
    }

    public List<Integer> getUrlIds() { return urlIds; }

    public List<String> getUrls() { return urls; }

}
