package com.iris.rainbow.db;

import com.iris.rainbow.article.ProcessedArticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProcessedArticleDaoImpl implements ProcessedArticleDao
{
    private Logger logger = LogManager.getLogger(ProcessedArticleDaoImpl.class.getName());
    private DAOManager db;
    private PreparedStatement preparedStatement;
    private ResultSet results;

    public ProcessedArticleDaoImpl()
    {
        db = new DAOManager();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean WriteArticlesToDb(List<ProcessedArticle> processedArticles)
    {
        for (ProcessedArticle article: processedArticles)
        {
            try
            {
                Integer articleId = WriteArticleDetailsToDb(article);
                List<Integer> urlIds = WriteArticleUrlsToDb(article.getUrls(), article.getFeedIds());
                JoinArticleAndUrls(articleId, urlIds);
            }
            catch (Exception e)
            {
                return false;
            }
            finally
            {
                db.closeConnection();
            }
        }

        return true;
    }

    /**
     * Writes the details of a processedArticle to the database (The category, Generated Headline, Description and
     * publication date). Currently does not support writing non SQL formatted publication dates.
     *
     * @param  article A single ProcessedArticle to be entered into the database.
     *
     * @return Article index generated for the row entered into the table.
     */
    private Integer WriteArticleDetailsToDb(ProcessedArticle article) throws SQLException
    {

        final String statement = "INSERT INTO \"ProcessedArticles\" (\"CategoryId\", \"Headline\",\"Description\",\"PublicationDate\") "
                + "VALUES (?,?,?,?)"
                + "RETURNING *";

        preparedStatement = db.getConnection().prepareStatement(statement);

        preparedStatement.setInt(1, article.getCategory());
        preparedStatement.setString(2, article.getHeadline());
        preparedStatement.setString(3, article.getDescription());
        preparedStatement.setDate(4, article.getPublicationDate());

        results = preparedStatement.executeQuery();
        results.next();

        int idIndex   = results.findColumn("id");

        return results.getInt(idIndex);
    }

    /**
     * Takes a list of Urls for related articles and writes them into their own respective adatabase.
     *
     * @param  urls A List of Urls for a single ProcessedArticle
     *
     * @return List of Url indice generated for the rows entered into the table.
     */
    private List<Integer> WriteArticleUrlsToDb(List<String> urls, List<Integer> feedIds) throws SQLException
    {
        List<Integer> urlIndexes = new ArrayList<Integer>();

        for (int i = 0; i < urls.size(); i++)
        {
            final String statement = "INSERT INTO \"ProcessedArticleUrls\" (\"Url\",\"FeedId\") "
                    + "VALUES (?,?)"
                    + "RETURNING *";

            preparedStatement = db.getConnection().prepareStatement(statement);
            preparedStatement.setString(1, urls.get(i));
            preparedStatement.setInt(2, feedIds.get(i));

            results = preparedStatement.executeQuery();
            results.next();

            int idIndex   = results.findColumn("Id");
            urlIndexes.add(results.getInt(idIndex));
        }

        return urlIndexes;
    }

    /**
     * Makes a number of entries into a joining table to facilitate the many to many relationship between
     * ProcessedArticles and their respectively urls.
     *
     * @param  articleId A single articleId for the ProcessedArticle associated with the Urls.
     * @param  urlIds A List of UrlIds for all urls associated with the ProcessedArticle.
     */
    private void JoinArticleAndUrls(Integer articleId, List<Integer> urlIds) throws SQLException
    {
        for (Integer urlId: urlIds)
        {
            final String statement = "INSERT INTO \"ProcessedArticle_Urls\" (\"ProcessedArticleId\", \"ProcessedArticleUrlId\") "
                    + "VALUES (?, ?)";

            preparedStatement = db.getConnection().prepareStatement(statement);

            preparedStatement.setInt(1, articleId);
            preparedStatement.setInt(2, urlId);

            preparedStatement.execute();
        }
    }


}
