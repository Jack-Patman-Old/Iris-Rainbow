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

    @Override
    public boolean WriteArticlesToDb(List<ProcessedArticle> processedArticles)
    {
        for (ProcessedArticle article: processedArticles)
        {
            try
            {
                Integer articleId = WriteArticleDetailsToDb(article);
                List<Integer> urlIds = WriteArticleUrlsToDb(article.getUrls());
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

    private List<Integer> WriteArticleUrlsToDb(List<String> urls) throws SQLException
    {
        List<Integer> urlIndexes = new ArrayList<Integer>();

        for (String url: urls)
        {
            final String statement = "INSERT INTO \"ProcessedArticleUrls\" (\"Url\") "
                    + "VALUES (?)"
                    + "RETURNING *";

            preparedStatement = db.getConnection().prepareStatement(statement);
            preparedStatement.setString(1, url);

            results = preparedStatement.executeQuery();
            results.next();

            int idIndex   = results.findColumn("Id");
            urlIndexes.add(results.getInt(idIndex));
        }

        return urlIndexes;
    }

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
