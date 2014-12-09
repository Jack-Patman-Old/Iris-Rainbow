package com.iris.rainbow.article;

import java.sql.Date;

public class UnprocessedArticle
{
    private int articleId;
    private int feedId;
    private int urlId;

    private String headline;
    private String description;
    private String url;

    private Date publicationDate;

    public UnprocessedArticle(int articleId, int feedId, int urlId, String headline, String description, String url, Date publicationDate)
    {
        this.articleId = articleId;
        this.feedId = feedId;
        this.urlId = urlId;
        this.url = url;
        this.headline = headline;
        this.description = description;
        this.publicationDate = publicationDate;
    }

    public int getArticleId()
    {
        return articleId;
    }

    public int getFeedId()
    {
        return feedId;
    }

    public String getUrl()
    {
        return url;
    }

    public int getUrlId()
    {
        return urlId;
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
