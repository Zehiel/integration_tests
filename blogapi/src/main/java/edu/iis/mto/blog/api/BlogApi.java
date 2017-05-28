package edu.iis.mto.blog.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.iis.mto.blog.api.request.PostRequest;
import edu.iis.mto.blog.api.request.UserRequest;
import edu.iis.mto.blog.dto.Id;
import edu.iis.mto.blog.dto.PostData;
import edu.iis.mto.blog.dto.UserData;
import edu.iis.mto.blog.services.BlogService;
import edu.iis.mto.blog.services.DataFinder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/blog", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(tags = "blog api")
public class BlogApi {

    private final static Logger logger = LoggerFactory.getLogger(BlogApi.class);

    @Autowired
    private BlogService blogService;

    @Autowired
    private DataFinder finder;

    @ApiOperation(value = "Creates new user")
    @RequestMapping(method = RequestMethod.POST, path = "/user")
    @ResponseStatus(HttpStatus.CREATED)
    public Id createUser(@RequestBody UserRequest userRequest) {
        logger.debug("create user endpoint called for data '{}'", userRequest);
        Long userId = blogService.createUser(userRequest);
        return id(userId);
    }

    @ApiOperation(value = "get user info based on user id")
    @RequestMapping(method = RequestMethod.GET, path = "/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserData getUser(@PathVariable("id") Long userId) {
        logger.debug("get user endpoint called for user id '{}'", userId);
        UserData user = finder.getUserData(userId);
        return user;
    }

    @ApiOperation(value = "find users based on email or first name or last name")
    @RequestMapping(method = RequestMethod.GET, path = "/user/find")
    public List<UserData> findUser(@RequestParam String searchString) {
        logger.debug("find users endpoint called for searchString '{}'", searchString);
        List<UserData> users = finder.findUsers(searchString);
        return users;
    }

    @ApiOperation(value = "Creates new blog post")
    @RequestMapping(method = RequestMethod.POST, path = "/user/{id}/post")
    @ResponseStatus(HttpStatus.CREATED)
    public Id createPost(@PathVariable("id") Long userId, @RequestBody PostRequest postRequest) {
        logger.debug("create post endpoint called for data '{}'", postRequest);

        Long postId = blogService.createPost(userId, postRequest);
        return id(postId);
    }

    @ApiOperation(value = "Add like to blog post")
    @RequestMapping(method = RequestMethod.POST, path = "user/{userId}/like/{postId}")
    public boolean addLikeToPost(@PathVariable("userId") Long userId, @PathVariable("postId") Long postId) {
        logger.debug("add like to post endpoint called for userId '{}' and postId '{}'", userId, postId);

        return blogService.addLikeToPost(userId, postId);
    }

    @ApiOperation(value = "get user posts based on user id")
    @RequestMapping(method = RequestMethod.GET, path = "/user/{id}/post")
    public List<PostData> getUserPosts(@PathVariable("id") Long userId) {
        logger.debug("get user posts endpoint called for user id '{}'", userId);
        List<PostData> posts = finder.getUserPosts(userId);
        return posts;
    }

    @ApiOperation(value = "Get post based on post id")
    @RequestMapping(method = RequestMethod.GET, path = "/post/{postId}")
    public PostData getPost(@PathVariable("postId") Long postId) {

        logger.debug("get posts endpoint called for post id '{}'", postId);
        return finder.getPost(postId);
    }

    private Id id(Long userId) {
        return new Id(userId);
    }

}
