package test.db;
import com.iris.rainbow.article.UnprocessedArticle;
import com.iris.rainbow.db.UnprocessedArticleDaoImpl;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UnprocessedArticleDaoImplTest
{
    @Test
    public void GetUnprocessedArticles_valid()
    {
        UnprocessedArticleDaoImpl db = new UnprocessedArticleDaoImpl();
        List<UnprocessedArticle> articles = db.GetUnprocessedArticles();

        assertTrue(articles.size() > 0);
    }
}
