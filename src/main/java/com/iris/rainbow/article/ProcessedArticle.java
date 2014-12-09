package com.iris.rainbow.article;

import java.sql.Date;
import java.util.List;

public class ProcessedArticle
{
    private int category;
    private String aggregateHeadline;
    private String description;
    private Date publicationDate;
    private List<Integer> urlIds;
    private List<String> urls;

    public ProcessedArticle(int categoryId, List<String> headlines, String description, Date publicationDate, List<Integer> urlIds, List<String> urls)
    {
        HeadlineGenerator generator = new HeadlineGenerator();
        this.aggregateHeadline = generator.GenerateAggregateHeadline(headlines);

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
