package edu.iis.mto.blog.domain;

import edu.iis.mto.blog.api.request.PostRequest;
import edu.iis.mto.blog.domain.errors.DomainError;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.repository.BlogPostRepository;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import edu.iis.mto.blog.api.request.UserRequest;
import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.User;
import edu.iis.mto.blog.domain.repository.UserRepository;
import edu.iis.mto.blog.mapper.DataMapper;
import edu.iis.mto.blog.services.BlogService;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogManagerTest {

    @MockBean
    UserRepository userRepository;

    @Autowired
    DataMapper dataMapper;

    @MockBean
    BlogPostRepository blogPostRepository;

    @Autowired
    BlogService blogService;

    @Before
    public void setUp() throws Exception {
        userRepository.deleteAll();
    }

    @Test
    public void creatingNewUserShouldSetAccountStatusToNEW() {
        blogService.createUser(new UserRequest("Lilly", "Misty", "lilly@domain.com"));
        ArgumentCaptor<User> userParam = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userParam.capture());
        User user = userParam.getValue();
        Assert.assertThat(user.getAccountStatus(), Matchers.equalTo(AccountStatus.NEW));
    }

    @Test (expected = DomainError.class)
    public void newUserShouldNotBeAbleToLikePost() throws Exception {

        blogService.createUser(new UserRequest("Lilly", "Misty", "lilly@domain.com"));
        blogService.createUser(new UserRequest("Joe", "Doe", "joe@domain.com"));
        ArgumentCaptor<User> userParam = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository,times(2)).save(userParam.capture());

        List<User> users = userParam.getAllValues();

        blogService.createPost(users.get(0).getId(),new PostRequest());
        ArgumentCaptor<BlogPost> blogPostParam = ArgumentCaptor.forClass(BlogPost.class);
        Mockito.verify(blogPostRepository).save(blogPostParam.capture());

        BlogPost blogPost = blogPostParam.getValue();
        blogPost.setId(1L);
        blogPost.setUser(users.get(0));
        Mockito.verify(blogPostRepository).save(blogPostParam.capture());

        blogPost = blogPostParam.getValue();

        BlogService spyBlogService = Mockito.spy(BlogService.class);
        Mockito.when(spyBlogService.addLikeToPost(users.get(1).getId(),blogPost.getId())).thenThrow(new DomainError("User is unable to like that post"));

        Assert.assertThat(users.get(1).getAccountStatus(), is(not(equalTo(AccountStatus.CONFIRMED))));
        spyBlogService.addLikeToPost(users.get(1).getId(),blogPost.getId());
    }

    @Test
    public void confirmedUserShouldBeAbleToLikePost() throws Exception {

        blogService.createUser(new UserRequest("Lilly", "Misty", "lilly@domain.com"));
        blogService.createUser(new UserRequest("Joe", "Doe", "joe@domain.com"));
        ArgumentCaptor<User> userParam = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository,times(2)).save(userParam.capture());

        List<User> users = userParam.getAllValues();
        users.get(1).setAccountStatus(AccountStatus.CONFIRMED);
        Mockito.verify(userRepository,times(2)).save(userParam.capture());

        users = userParam.getAllValues();


        blogService.createPost(users.get(0).getId(),new PostRequest());
        ArgumentCaptor<BlogPost> blogPostParam = ArgumentCaptor.forClass(BlogPost.class);
        Mockito.verify(blogPostRepository).save(blogPostParam.capture());

        BlogPost blogPost = blogPostParam.getValue();
        blogPost.setId(1L);
        blogPost.setUser(users.get(0));
        Mockito.verify(blogPostRepository).save(blogPostParam.capture());

        blogPost = blogPostParam.getValue();

        BlogService spyBlogService = Mockito.spy(BlogService.class);
        Mockito.when(spyBlogService.addLikeToPost(users.get(1).getId(),blogPost.getId())).thenReturn(true);

        Assert.assertThat(users.get(1).getAccountStatus(), is(equalTo(AccountStatus.CONFIRMED)));
        Assert.assertThat(spyBlogService.addLikeToPost(users.get(1).getId(),blogPost.getId()),is(equalTo(true)));

    }
}
