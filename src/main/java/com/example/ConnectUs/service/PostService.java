package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.authentication.UserResponse;
import com.example.ConnectUs.dto.post.PostRequest;
import com.example.ConnectUs.dto.post.PostResponse;
import com.example.ConnectUs.dto.post.PostsResponse;
import com.example.ConnectUs.dto.searchUsers.SearchUserResponse;
import com.example.ConnectUs.enumerations.NotificationType;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.neo4j.UserNeo4j;
import com.example.ConnectUs.model.postgres.Image;
import com.example.ConnectUs.model.postgres.Notification;
import com.example.ConnectUs.model.postgres.Post;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.neo4j.UserNeo4jRepository;
import com.example.ConnectUs.repository.postgres.CommentRepository;
import com.example.ConnectUs.repository.postgres.PostRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final UserNeo4jRepository userNeo4jRepository;
    private final UserService userService;
    private final ImageService imageService;

    @Transactional
    public PostResponse save(PostRequest request) {
        User user = userService.findByEmail(request.getUserEmail()).orElse(new User());
        List<Image> images = new ArrayList<>();
        List<User> taggedUsers = new ArrayList<>();
        for(Integer id : request.getTaggedUserIds()){
            taggedUsers.add(userService.findById(id));
        }

        Post post = Post.builder()
                .text(request.getPostText())
                .dateAndTime(LocalDateTime.now())
                .user(user)
                .taggedUsers(taggedUsers)
                .build();

        for (String i : request.getImages()) {
            images.add(Image.builder()
                    .image(i)
                    .post(post)
                    .build());
        }

        post.setImages(images);

        Post savedPost = postRepository.save(post);
        for(Image i : images){
            imageService.save(i);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = savedPost.getDateAndTime().format(formatter);

        List<SearchUserResponse> taggedUsersList = new ArrayList<>();
        for(User u : savedPost.getTaggedUsers()){
            taggedUsersList.add(SearchUserResponse.builder()
                    .id(u.getId())
                    .friend(u.getFriends().contains(user))
                    .profileImage(u.getProfileImage())
                    .email(u.getEmail())
                    .firstname(u.getFirstname())
                    .lastname(u.getLastname())
                    .numberOfFriends(userNeo4jRepository.getNumberOfUserFriends(u.getId()))
                    .numberOfMutualFriends(userNeo4jRepository.getNumberOfMutualFriends(u.getId(), user.getId()))
                    .build());
        }

        return PostResponse.builder()
                .isLiked(false)
                .id(savedPost.getId())
                .firstname(post.getUser().getFirstname())
                .lastname(post.getUser().getLastname())
                .profileImage(post.getUser().getProfileImage())
                .images(request.getImages())
                .text(savedPost.getText())
                .dateAndTime(formattedDateTime)
                .numberOfLikes(0)
                .numberOfComments(0)
                .taggedUsers(taggedUsersList)
                .userId(user.getId())
                .build();
    }

    @Transactional
    public PostResponse delete(Integer postId){
        Post post = postRepository.findById(postId).orElseThrow();
        postRepository.delete(post);

        return PostResponse.builder()
                .id(post.getId())
                .firstname(post.getUser().getFirstname())
                .lastname(post.getUser().getLastname())
                .profileImage(post.getUser().getProfileImage())
                .numberOfLikes(0)
                .numberOfComments(0)
                .build();
    }

    /*@Transactional
    public PostResponse getById(Integer id) {
        Post post = postRepository.getById(id);
        Resource imgFile = new ClassPathResource("images" + File.separator + post.getImageName());

        try{
            InputStreamResource isr = new InputStreamResource(imgFile.getInputStream());
            byte[] bytes = isr.getContentAsByteArray();
            String image = Base64.getEncoder().encodeToString(bytes);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = post.getDateAndTime().format(formatter);

            return PostResponse.builder()
                    .id(post.getId())
                    .imageInBase64(image)
                    .text(post.getText())
                    .dateAndTime(formattedDateTime)
                    .build();
        }catch(Exception e){
            return new PostResponse();
        }


    }*/
    @Transactional
    public PostsResponse getPostsForFeed(Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        List<User> userFriends = user.getFriends();

        List<Post> posts = postRepository.findAllByUserId(id);
        posts.addAll(getPostsFromUserFriends(userFriends));

        PostsResponse postsResponse = getPostsResponseFromPostsList(posts, user);

        return postsResponse;
    }

    private List<Post> getPostsFromUserFriends(List<User> friends) {
        List<Post> posts = new ArrayList<>();

        for (User u : friends) {
            List<Post> friendPostList = postRepository.findAllByUserId(u.getId());
            posts.addAll(friendPostList);
        }

        return posts;
    }

    private List<UserResponse> getUserResponseListFromUserList(List<User> userList) {
        List<UserResponse> retList = new ArrayList<>();

        for (User user : userList) {

            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .dateOfBirth(user.getDateOfBirth().toString())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .gender(userService.capitalizeFirstLetter(user.getGender().toString()))
                    .profileImage(user.getProfileImage())
                    .build();

            retList.add(userResponse);
        }

        return retList;
    }

    private PostsResponse getPostsResponseFromPostsList(List<Post> posts, User user) {
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> postResponseList = new ArrayList<>();
        for (Post post : posts) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = post.getDateAndTime().format(formatter);
            boolean isLiked = post.getLikes().contains(user);
            int numberOfComments = commentRepository.countAllCommentsByPostId(post.getId());
            List<SearchUserResponse> taggedUsers = new ArrayList<>();
            for(User u : post.getTaggedUsers()){
                taggedUsers.add(SearchUserResponse.builder()
                                .id(u.getId())
                                .friend(u.getFriends().contains(user))
                                .profileImage(u.getProfileImage())
                                .email(u.getEmail())
                                .firstname(u.getFirstname())
                                .lastname(u.getLastname())
                                .numberOfFriends(userNeo4jRepository.getNumberOfUserFriends(u.getId()))
                                .numberOfMutualFriends(userNeo4jRepository.getNumberOfMutualFriends(u.getId(), user.getId()))
                        .build());
            }

            PostResponse postResponse = PostResponse.builder()
                    .id(post.getId())
                    .userId(post.getUser().getId())
                    .firstname(post.getUser().getFirstname())
                    .lastname(post.getUser().getLastname())
                    .images(post.getImages().stream().map(Image::getImage).collect(Collectors.toList()))
                    .text(post.getText())
                    .dateAndTime(formattedDateTime)
                    .isLiked(isLiked)
                    .numberOfLikes(post.getLikes().size())
                    .numberOfComments(numberOfComments)
                    .profileImage(post.getUser().getProfileImage())
                    .taggedUsers(taggedUsers)
                    .build();
            postResponseList.add(postResponse);
        }
        postsResponse.setPosts(postResponseList);
        Collections.sort(postsResponse.getPosts(), Collections.reverseOrder());
        return postsResponse;
    }

    @Transactional
    public void likePost(Integer userId, Integer postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        Set<User> usersWhoLikes = post.getLikes();
        usersWhoLikes.add(user);
        post.setLikes(usersWhoLikes);
        postRepository.save(post);

        if (post.getUser().getId() != user.getId()) {
            notificationService.save(Notification.builder()
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .user(post.getUser())
                    .avatar(user.getProfileImage())
                    .type(NotificationType.LIKE)
                    .dateAndTime(LocalDateTime.now())
                    .entityId(post.getId())
                    .isRead(false)
                    .text("liked your post. Click on the notification to see the post.")
                    .build());
        }
    }

    @Transactional
    public void unlikePost(Integer userId, Integer postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        Set<User> usersWhoLikes = post.getLikes();
        usersWhoLikes.remove(user);
        post.setLikes(usersWhoLikes);
        postRepository.save(post);
    }

    @Transactional
    public PostsResponse getUserPosts(Integer userId, Integer myId) {
        try {
            List<Post> postList = postRepository.findAllByUserId(userId);
            User user = userRepository.findById(myId).orElseThrow();
            PostsResponse postsResponse = getPostsResponseFromPostsList(postList, user);
            return postsResponse;
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public List<SearchUserResponse> getUsersWhoLikedPost(Integer postId, Integer myId) {
        try {
            Post post = postRepository.findById(postId).orElseThrow();
            User user = userRepository.findById(myId).orElseThrow();

            List<SearchUserResponse> responseList = new ArrayList<>();
            for (User u : post.getLikes()) {
                SearchUserResponse searchUserResponse = SearchUserResponse.builder()
                        .id(u.getId())
                        .profileImage(u.getProfileImage())
                        .friend(user.getFriends().contains(u))
                        .email(u.getEmail())
                        .firstname(u.getFirstname())
                        .lastname(u.getLastname())
                        .country(u.getLocation().getCountry())
                        .city(u.getLocation().getCity())
                        .street(u.getLocation().getStreet())
                        .number(u.getLocation().getNumber())
                        .numberOfFriends(userNeo4jRepository.getNumberOfUserFriends(u.getId().intValue()))
                        .numberOfMutualFriends(userNeo4jRepository.getNumberOfMutualFriends(u.getId().intValue(), myId.intValue()))
                        .build();
                responseList.add(searchUserResponse);
            }
            return responseList;
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public PostResponse getPost(Integer postId, Integer myId) {
        Post post = postRepository.findById(postId).orElseThrow();
        User user = userRepository.findById(myId).orElseThrow();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = post.getDateAndTime().format(formatter);
        boolean isLiked = post.getLikes().contains(user);
        int numberOfComments = commentRepository.countAllCommentsByPostId(post.getId());

        List<SearchUserResponse> taggedUsersList = new ArrayList<>();
        for(User u : post.getTaggedUsers()){
            taggedUsersList.add(SearchUserResponse.builder()
                    .id(u.getId())
                    .friend(u.getFriends().contains(user))
                    .profileImage(u.getProfileImage())
                    .email(u.getEmail())
                    .firstname(u.getFirstname())
                    .lastname(u.getLastname())
                    .country(u.getLocation().getCountry())
                    .city(u.getLocation().getCity())
                    .street(u.getLocation().getStreet())
                    .number(u.getLocation().getNumber())
                    .numberOfFriends(userNeo4jRepository.getNumberOfUserFriends(u.getId().intValue()))
                    .numberOfMutualFriends(userNeo4jRepository.getNumberOfMutualFriends(u.getId().intValue(), myId.intValue()))
                    .build());
        }

        PostResponse postResponse = PostResponse.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .firstname(post.getUser().getFirstname())
                .lastname(post.getUser().getLastname())
                .images(post.getImages().stream().map(Image::getImage).collect(Collectors.toList()))
                .text(post.getText())
                .dateAndTime(formattedDateTime)
                .isLiked(isLiked)
                .numberOfLikes(post.getLikes().size())
                .numberOfComments(numberOfComments)
                .profileImage(post.getUser().getProfileImage())
                .taggedUsers(taggedUsersList)
                .build();

        return postResponse;
    }
}
