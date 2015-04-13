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
    public ProcessedArticle()
    {

    }

    public ProcessedArticle(int categoryId, List<Integer> feedIds, String aggregateHeadline, String description, Date publicationDate, List<Integer> urlIds, List<String> urls)
    {
        this.aggregateHeadline = aggregateHeadline;
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

    public List<Integer> getFeedIds()
    {
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

    public List<Integer> getUrlIds()
    {
        return urlIds;
    }

    public List<String> getUrls()
    {
        return urls;
    }

    public void setUrls(List<String> urls)
    {
        this.urls = urls;
    }

    public void setCategory(int category)
    {
        this.category = category;
    }

    public void setFeedIds(List<Integer> feedIds)
    {
        this.feedIds = feedIds;
    }

    public void setAggregateHeadline(String aggregateHeadline)
    {
        this.aggregateHeadline = aggregateHeadline;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setPublicationDate(Date publicationDate)
    {
        this.publicationDate = publicationDate;
    }

    public void setUrlIds(List<Integer> urlIds)
    {
        this.urlIds = urlIds;
    }
}
