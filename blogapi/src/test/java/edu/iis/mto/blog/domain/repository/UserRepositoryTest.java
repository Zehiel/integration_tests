package edu.iis.mto.blog.domain.repository;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.User;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    private User user;

    @Before
    public void setUp() {
        repository.deleteAll();
        user = new User();
        user.setFirstName("Andrew");
        user.setLastName("Borzecki");
        user.setEmail("andrew.borzecki@gmail.com");
        user.setAccountStatus(AccountStatus.NEW);
    }


    @Test
    public void shouldFindNoUsersIfRepositoryIsEmpty() {

        List<User> users = repository.findAll();

        Assert.assertThat(users, Matchers.hasSize(0));
    }


    @Test
    public void shouldFindOneUsersIfRepositoryContainsOneUserEntity() {
        User persistedUser = entityManager.persist(user);
        List<User> users = repository.findAll();

        Assert.assertThat(users, Matchers.hasSize(1));
        Assert.assertThat(users.get(0).getEmail(), Matchers.equalTo(persistedUser.getEmail()));
    }


    @Test
    public void shouldStoreANewUser() {

        User persistedUser = repository.save(user);

        Assert.assertThat(persistedUser.getId(), Matchers.notNullValue());
    }

    @Test
    public void shouldNotFindUser() throws Exception {

        User persistedUser = entityManager.persist(user);
        List<User> foundUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("test","test","test@test.pl");
        Assert.assertThat(foundUsers.isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldFindOneUserByExactFirstName() throws Exception {
        User persistedUser = entityManager.persist(user);
        List<User> foundUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("Andrew","#","#");
        Assert.assertThat(foundUsers.get(0),Matchers.equalTo(persistedUser));
    }

    @Test
    public void shouldFindOneUserBySimilarFirstName() throws Exception {
        User persistedUser = entityManager.persist(user);
        List<User> foundUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("And","#","#");
        Assert.assertThat(foundUsers.get(0),Matchers.equalTo(persistedUser));
    }


    @Test
    public void shouldFindOneUserByLastName() throws Exception {
        User persistedUser = entityManager.persist(user);
        List<User> foundUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("#","Borzecki","#");
        Assert.assertThat(foundUsers.get(0),Matchers.equalTo(persistedUser));
    }

    @Test
    public void shouldFindOneUserBySimilarLastName() throws Exception {
        User persistedUser = entityManager.persist(user);
        List<User> foundUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("#","orz","#");
        Assert.assertThat(foundUsers.get(0),Matchers.equalTo(persistedUser));
    }

    @Test
    public void shouldFindOneUserByEmail() throws Exception {
        User persistedUser = entityManager.persist(user);
        List<User> foundUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("#","#","andrew.borzecki@gmail.com");
        Assert.assertThat(foundUsers.get(0),Matchers.equalTo(persistedUser));
    }

    @Test
    public void shouldFindOneUserBySimilarEmail() throws Exception {
        User persistedUser = entityManager.persist(user);
        List<User> foundUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("#","#","andrew.bor");
        Assert.assertThat(foundUsers.get(0),Matchers.equalTo(persistedUser));
    }

    @Test
    public void shouldFindOneUserByFirstAndLastNameCombination() throws Exception {
        User persistedUser = entityManager.persist(user);
        List<User> foundUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("Andrew","Borzecki","#");
        Assert.assertThat(foundUsers.get(0),Matchers.equalTo(persistedUser));
    }

    @Test
    public void shouldFindOneUserByFirstNameAndEmailCombination() throws Exception {
        User persistedUser = entityManager.persist(user);
        List<User> foundUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("Andrew","#","andrew.borzecki@gmail.com");
        Assert.assertThat(foundUsers.get(0),Matchers.equalTo(persistedUser));
    }

    @Test
    public void shouldFindOneUserByLastNameAndEmailCombination() throws Exception {
        User persistedUser = entityManager.persist(user);
        List<User> foundUsers = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("#","Borzecki","andrew.borzecki@gmail.com");
        Assert.assertThat(foundUsers.get(0),Matchers.equalTo(persistedUser));
    }
}
