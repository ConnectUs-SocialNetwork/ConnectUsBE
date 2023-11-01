package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.authentication.UserResponse;
import com.example.ConnectUs.dto.page.PageResponse;
import com.example.ConnectUs.dto.pagePost.PagePostRequest;
import com.example.ConnectUs.dto.pagePost.PagePostResponse;
import com.example.ConnectUs.dto.pagePost.PagePostsResponse;
import com.example.ConnectUs.dto.post.PostResponse;
import com.example.ConnectUs.dto.post.PostsResponse;
import com.example.ConnectUs.dto.searchUsers.SearchUserResponse;
import com.example.ConnectUs.enumerations.NotificationType;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.*;
import com.example.ConnectUs.repository.neo4j.PageNeo4jRepository;
import com.example.ConnectUs.repository.neo4j.UserNeo4jRepository;
import com.example.ConnectUs.repository.postgres.PagePostCommentRepository;
import com.example.ConnectUs.repository.postgres.PagePostRepository;
import com.example.ConnectUs.repository.postgres.PageRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagePostService {
    private final PagePostRepository postRepository;
    private final PageRepository pageRepository;
    private final PageNeo4jRepository pageNeo4jRepository;
    private final UserRepository userRepository;
    private final PagePostCommentRepository pagePostCommentRepository;
    private final NotificationService notificationService;
    private final UserNeo4jRepository userNeo4jRepository;
    private final UserService userService;
    private final PagePostImageService imageService;

    public PagePostResponse save(PagePostRequest pagePostRequest) {
        try {
            Page page = pageRepository.findById(pagePostRequest.getPageId()).orElseThrow();
            List<PagePostImage> images = new ArrayList<>();
            PagePost post = PagePost.builder()
                    .text(pagePostRequest.getPostText())
                    .dateAndTime(LocalDateTime.now())
                    .page(page)
                    .build();

            for (String i : pagePostRequest.getImages()) {
                images.add(PagePostImage.builder()
                        .image(i)
                        .pagePost(post)
                        .build());
            }

            post.setImages(images);

            PagePost savedPost = postRepository.save(post);
            for(PagePostImage i : images){
                imageService.save(i);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = savedPost.getDateAndTime().format(formatter);

            return PagePostResponse.builder()
                    .isLiked(false)
                    .postId(savedPost.getId())
                    .pageId(savedPost.getId())
                    .name(savedPost.getPage().getName())
                    .profileImage(savedPost.getPage().getAvatar())
                    .images(pagePostRequest.getImages())
                    .text(savedPost.getText())
                    .dateAndTime(formattedDateTime)
                    .numberOfLikes(0)
                    .numberOfComments(0)
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
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

    private PagePostsResponse getPagePostsResponseFromPostsList(List<PagePost> posts, User user) {
        PagePostsResponse postsResponse = new PagePostsResponse();
        List<PagePostResponse> postResponseList = new ArrayList<>();
        for (PagePost post : posts) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = post.getDateAndTime().format(formatter);
            boolean isLiked = post.getLikes().contains(user);
            int numberOfComments = pagePostCommentRepository.countAllCommentsByPostId(post.getId());

            PagePostResponse postResponse = PagePostResponse.builder()
                    .postId(post.getId())
                    .pageId(post.getPage().getId())
                    .name(post.getPage().getName())
                    .images(post.getImages().stream().map(PagePostImage::getImage).collect(Collectors.toList()))
                    .text(post.getText())
                    .dateAndTime(formattedDateTime)
                    .isLiked(isLiked)
                    .numberOfLikes(post.getLikes().size())
                    .numberOfComments(numberOfComments)
                    .profileImage(post.getPage().getAvatar())
                    .build();
            postResponseList.add(postResponse);
        }
        postsResponse.setPosts(postResponseList);
        Collections.sort(postsResponse.getPosts(), Collections.reverseOrder());
        return postsResponse;
    }

    @Transactional
    public PagePostsResponse getPagePosts(Integer pageId, Integer myId) {
        try {
            Page page = pageRepository.findById(pageId).orElseThrow();
            List<PagePost> postList = postRepository.findAllByPageId(pageId);
            User user = userRepository.findById(myId).orElseThrow();
            PagePostsResponse postsResponse = getPagePostsResponseFromPostsList(postList, user);
            postsResponse.setAvatar(page.getAvatar());
            return postsResponse;
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Transactional
    public void likePost(Integer userId, Integer postId) {
        try {
            User user = userRepository.findById(userId).orElseThrow();

            PagePost post = postRepository.findById(postId).orElseThrow();

            List<User> userWhoLikes = post.getLikes();
            userWhoLikes.add(user);
            post.setLikes(userWhoLikes);
            postRepository.save(post);

            if(post.getPage().getAdministrator().getId() != user.getId()){
                notificationService.save(Notification.builder()
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .user(post.getPage().getAdministrator())
                        .avatar(user.getProfileImage())
                        .type(NotificationType.PAGE_POST_LIKE)
                        .dateAndTime(LocalDateTime.now())
                        .entityId(post.getId())
                        .isRead(false)
                        .text("liked the post on page " + post.getPage().getName() + ". Click on the notification to see the post.")
                        .build());
            }
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Transactional
    public void unlikePost(Integer userId, Integer postId) {
        try {
            User user = userRepository.findById(userId).orElseThrow();

            PagePost post = postRepository.findById(postId).orElseThrow();

            List<User> userWhoLikes = post.getLikes();
            userWhoLikes.remove(user);
            post.setLikes(userWhoLikes);
            postRepository.save(post);
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public PagePostsResponse getPagePostsForFeed(Integer userId) {
        List<Integer> integerPageIds = pageNeo4jRepository.getLikedPagesIds(userId).stream()
                .map(Long::intValue)
                .collect(Collectors.toList());

        List<PagePostResponse> postResponseList = new ArrayList<>();

        for (Integer pageId : integerPageIds) {
            PagePostsResponse postResponse = getPagePosts(pageId, userId);
            postResponseList.addAll(postResponse.getPosts());
        }

        PagePostsResponse postResponse = new PagePostsResponse();
        postResponse.setPosts(postResponseList);

        return postResponse;
    }

    public List<SearchUserResponse> getUsersWhoLikedPost(Integer postId, Integer myId) {
        try {
            PagePost post = postRepository.findById(postId).orElseThrow();
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

    public PagePostResponse getPost(Integer postId, Integer myId) {
        PagePost post = postRepository.findById(postId).orElseThrow();
        User user = userRepository.findById(myId).orElseThrow();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = post.getDateAndTime().format(formatter);
        boolean isLiked = post.getLikes().contains(user);
        int numberOfComments = pagePostCommentRepository.countAllCommentsByPostId(post.getId());

        PagePostResponse postResponse = PagePostResponse.builder()
                .postId(post.getId())
                .pageId(post.getPage().getId())
                .name(post.getPage().getName())
                .images(post.getImages().stream().map(PagePostImage::getImage).collect(Collectors.toList()))
                .text(post.getText())
                .dateAndTime(formattedDateTime)
                .isLiked(isLiked)
                .numberOfLikes(post.getLikes().size())
                .numberOfComments(numberOfComments)
                .profileImage(post.getPage().getAvatar())
                .build();

        return postResponse;
    }
}
