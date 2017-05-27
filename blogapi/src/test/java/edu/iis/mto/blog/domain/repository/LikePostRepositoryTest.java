package edu.iis.mto.blog.domain.repository;

import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
import edu.iis.mto.blog.domain.model.User;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by Andrzej Borzecki on 28/05/2017.
 */

@RunWith(SpringRunner.class)
@DataJpaTest
public class LikePostRepositoryTest {

    @Autowired
    private LikePostRepository likePostRepository;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        List<User> users = userRepository.findAll();
        BlogPost blogPost = new BlogPost();
        blogPost.setEntry("Test text");
        blogPost.setId(1L);
        blogPost.setUser(users.get(0));
        blogPostRepository.save(blogPost);

        List<BlogPost> blogPosts = blogPostRepository.findAll();

        LikePost likePost = new LikePost();
        likePost.setId(1L);
        likePost.setPost(blogPosts.get(0));
        likePost.setUser(users.get(0));
        likePostRepository.save(likePost);
    }
}
