package edu.iis.mto.blog.domain;

import edu.iis.mto.blog.api.request.UserRequest;
import edu.iis.mto.blog.domain.errors.DomainError;
import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.User;
import edu.iis.mto.blog.domain.repository.BlogPostRepository;
import edu.iis.mto.blog.domain.repository.LikePostRepository;
import edu.iis.mto.blog.domain.repository.UserRepository;
import edu.iis.mto.blog.mapper.DataMapper;
import edu.iis.mto.blog.services.BlogService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogManagerTest {

    @MockBean
    UserRepository userRepository;

    @Autowired
    DataMapper dataMapper;

    @MockBean
    BlogPostRepository blogPostRepository;

    @MockBean
    LikePostRepository likePostRepository;

    @Autowired
    BlogService blogService;

    private List<User> testUsers;
    private BlogPost testPost;

    @Before
    public void setUp() throws Exception {
        testUsers = new ArrayList<User>();

        testUsers.add(new User());
        testUsers.get(0).setFirstName("Andrzej");
        testUsers.get(0).setLastName("Borzecki");
        testUsers.get(0).setEmail("andrzej@pl.com");
        testUsers.get(0).setId((long) 1);

        testUsers.add(new User());
        testUsers.get(1).setFirstName("Rafal");
        testUsers.get(1).setLastName("Franiewski");
        testUsers.get(1).setEmail("rafal@pl.com");
        testUsers.get(1).setId((long) 2);

        testPost = new BlogPost();
        testPost.setEntry("lorem ipsum");
        testPost.setId((long) 10);
    }

    @Test
    public void creatingNewUserShouldSetAccountStatusToNEW() {
        blogService.createUser(new UserRequest("Lilly", "Misty", "lilly@domain.com"));
        ArgumentCaptor<User> userParam = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userParam.capture());
        User user = userParam.getValue();
        Assert.assertThat(user.getAccountStatus(), Matchers.equalTo(AccountStatus.NEW));
    }

    @Test(expected = DomainError.class)
    public void newUserShouldNotBeAbleToLikePost() throws Exception {

        testUsers.get(0).setAccountStatus(AccountStatus.NEW);
        testUsers.get(1).setAccountStatus(AccountStatus.CONFIRMED);
        Mockito.when(userRepository.findOne(testUsers.get(0).getId())).thenReturn(testUsers.get(0));
        Mockito.when(userRepository.findOne(testUsers.get(1).getId())).thenReturn(testUsers.get(1));

        testPost.setUser(testUsers.get(1));
        Mockito.when(blogPostRepository.findOne(testPost.getId())).thenReturn(testPost);
        Mockito.when(likePostRepository.findByUserAndPost(testUsers.get(0), testPost)).thenReturn(Optional.empty());
        blogService.addLikeToPost(testUsers.get(1).getId(), testPost.getId());
    }

    @Test
    public void confirmedUserShouldBeAbleToLikePost() throws Exception {

        testUsers.get(0).setAccountStatus(AccountStatus.CONFIRMED);
        testUsers.get(1).setAccountStatus(AccountStatus.CONFIRMED);
        Mockito.when(userRepository.findOne(testUsers.get(0).getId())).thenReturn(testUsers.get(0));
        Mockito.when(userRepository.findOne(testUsers.get(1).getId())).thenReturn(testUsers.get(1));

        testPost.setUser(testUsers.get(0));
        Mockito.when(blogPostRepository.findOne(testPost.getId())).thenReturn(testPost);
        Mockito.when(likePostRepository.findByUserAndPost(testUsers.get(1), testPost)).thenReturn(Optional.empty());
        blogService.addLikeToPost(testUsers.get(1).getId(), testPost.getId());
    }
}
