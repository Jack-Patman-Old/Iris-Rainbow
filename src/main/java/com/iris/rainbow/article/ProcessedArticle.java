package com.iris.rainbow.article;

import java.sql.Date;
import java.util.List;

public class ProcessedArticle
{
    private int category;
    private String headline;
    private String description;
    private Date publicationDate;
    private List<Integer> urlIds;

    public ProcessedArticle(int categoryId, String headline, String description, Date publicationDate, List<Integer> urlIds)
    {
        this.category = categoryId;
        this.headline = headline;
        this.description = description;
        this.publicationDate = publicationDate;
        this.urlIds = urlIds;
    }

    public int getCategory()
    {
        return category;
    }

    public String getHeadline()
    {
        return headline;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getPublicationDate()
    {
        return publicationDate;
    }
}
