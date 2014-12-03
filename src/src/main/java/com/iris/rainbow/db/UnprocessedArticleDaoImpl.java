package com.iris.rainbow.db;

import com.iris.rainbow.article.ProcessedArticle;
import com.iris.rainbow.article.UnprocessedArticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    /** Returns a List containing all Unprocessed Articles in the UnprocessedArticles table
     *
     * @return List of UnprocessedArticle()
     **/
    @Override
    public List<UnprocessedArticle> GetUnprocessedArticles()
    {
        List<UnprocessedArticle> article = new ArrayList<>();

        try
        {
            String statement = "SELECT * FROM \"UnprocessedArticles\"";

            preparedStatement = db.getConnection().prepareStatement(statement);
            results = preparedStatement.executeQuery();

            while (results.next())
            {
                article.add(ParseUnprocessedArticle(results));
            }

            return article;

        }
        catch (Exception e)
        {
            logger.error("Fatal exception encountered attempting to load all unprocessed articles, exception was " + e);
            return article;
        }
        finally
        {
            db.closeConnection();
        }


    }

    /** Returns a single unprocessedArticle object, generated based on resultSet returned
     * from the UnprocessedArticle table.
     *
     * @param results resultSet returned from UnprocessedArticle table
     * @return Single UnprocessedArticle() object
     **/
    private UnprocessedArticle ParseUnprocessedArticle(ResultSet results)
    {
        try
        {
            int idIndex   = results.findColumn("id");
            int feedIdIndex     = results.findColumn("FeedId");
            int headlineIndex   = results.findColumn("Headline");
            int descriptionIndex   = results.findColumn("Description");
            int publicationDateIndex   = results.findColumn("PublicationDate");
            int urlIdIndex   = results.findColumn("UrlId");

            int articleId = results.getInt(idIndex);
            int feedId = results.getInt(feedIdIndex);
            String headline = results.getString(headlineIndex);
            String description = results.getString(descriptionIndex);
            Date publicationDate = results.getDate(publicationDateIndex);
            int urlId = results.getInt(urlIdIndex);

            return (new UnprocessedArticle(articleId, feedId, urlId, headline, description, publicationDate));
        }
        catch (SQLException e)
        {
            logger.error("exception encountered attempting to read from UnprocessedArticle result set, exception was " + e);
            return null;
        }
    }



    /** Returns a single unprocessedArticle object, generated based on resultSet returned
     * from the UnprocessedArticle table.
     *
     * @param results resultSet returned from UnprocessedArticle table
     * @return Single UnprocessedArticle() object
     **/
    @Override
    public void RemoveUnprocessedArticles(List<ProcessedArticle> processedArticles)
    {

    }
}
