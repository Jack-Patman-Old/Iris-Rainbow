package com.iris.rainbow.db;

import com.iris.rainbow.article.UnprocessedArticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnprocessedArticleDaoImpl implements UnprocessedArticleDao
{
    private Logger logger = LogManager.getLogger(UnprocessedArticleDaoImpl.class.getName());
    private DAOManager db;
    private PreparedStatement preparedStatement = null;
    private ResultSet results;

    public UnprocessedArticleDaoImpl()
    {
        db = new DAOManager();
    }

    /**
     * Returns a List containing all Unprocessed Articles in the UnprocessedArticles table
     *
     * @return List of UnprocessedArticle()
     */
    @Override
    public List<UnprocessedArticle> GetUnprocessedArticles()
    {
        List<UnprocessedArticle> articles = new ArrayList<UnprocessedArticle>();

        try
        {
            final String statement = "SELECT * FROM \"UnprocessedArticles\" ua RIGHT OUTER JOIN \"UnprocessedArticleUrls\" uau ON ua.\"UrlId\" = uau.\"Id\" WHERE uau.\"DateProcessed\" > LOCALTIMESTAMP - INTERVAL '1 days' ";

            preparedStatement = db.getConnection().prepareStatement(statement);
            results = preparedStatement.executeQuery();

            while (results.next())
            {
                articles.add(ParseUnprocessedArticle(results));
            }

            return articles;

        }
        catch (Exception e)
        {
            logger.error("Fatal exception encountered attempting to load all unprocessed articles, exception was " + e);
            return articles;
        }
        finally
        {
            db.closeConnection();
        }


    }

    /**
     * Returns a single unprocessedArticle object, generated based on resultSet returned
     * from the UnprocessedArticle table.
     *
     * @param results resultSet returned from UnprocessedArticle table
     * @return Single UnprocessedArticle() object
     */
    private UnprocessedArticle ParseUnprocessedArticle(ResultSet results)
    {
        try
        {
            int idIndex = results.findColumn("id");
            int feedIdIndex = results.findColumn("FeedId");
            int headlineIndex = results.findColumn("Headline");
            int descriptionIndex = results.findColumn("Description");
            int publicationDateIndex = results.findColumn("PublicationDate");
            int urlIdIndex = results.findColumn("UrlId");
            int urlIndex = results.findColumn("Url");

            int articleId = results.getInt(idIndex);
            int feedId = results.getInt(feedIdIndex);
            String headline = results.getString(headlineIndex);
            String description = Jsoup.parse(results.getString(descriptionIndex)).text();
            Date publicationDate = results.getDate(publicationDateIndex);
            int urlId = results.getInt(urlIdIndex);
            String url = results.getString(urlIndex);

            return (new UnprocessedArticle(articleId, feedId, urlId, headline, description, url, publicationDate));
        }
        catch (SQLException e)
        {
            logger.error("exception encountered attempting to read from UnprocessedArticle result set, exception was " + e);
            return null;
        }
    }


    @Override
    public void RemoveUnprocessedArticles(List<UnprocessedArticle> unprocessedArticles)
    {
        for (UnprocessedArticle article : unprocessedArticles)
        {
            try
            {
                RemoveUnprocessedArticleDetails(article);
                RemoveUnprocessedArticleUrls(article.getUrlId());
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void RemoveUnprocessedArticleUrls(int urlId) throws SQLException
    {
        final String statement = "DELETE FROM \"UnprocessedArticleUrls\" WHERE \"Id\" = ?";

        preparedStatement = db.getConnection().prepareStatement(statement);
        preparedStatement.setInt(1, urlId);

        preparedStatement.execute();
    }

    private void RemoveUnprocessedArticleDetails(UnprocessedArticle article) throws SQLException
    {
        final String statement = "DELETE FROM \"UnprocessedArticles\" WHERE \"Id\" = ?";

        preparedStatement = db.getConnection().prepareStatement(statement);
        preparedStatement.setInt(1, article.getArticleId());

        preparedStatement.execute();
    }
}
