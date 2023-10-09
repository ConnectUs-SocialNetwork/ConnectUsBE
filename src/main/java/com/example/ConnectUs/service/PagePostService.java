package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.authentication.UserResponse;
import com.example.ConnectUs.dto.page.PageResponse;
import com.example.ConnectUs.dto.pagePost.PagePostRequest;
import com.example.ConnectUs.dto.pagePost.PagePostResponse;
import com.example.ConnectUs.dto.pagePost.PagePostsResponse;
import com.example.ConnectUs.dto.post.PostResponse;
import com.example.ConnectUs.dto.post.PostsResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.Page;
import com.example.ConnectUs.model.postgres.PagePost;
import com.example.ConnectUs.model.postgres.Post;
import com.example.ConnectUs.model.postgres.User;
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

@Service
@RequiredArgsConstructor
public class PagePostService {
    private final PagePostRepository postRepository;
    private final PageRepository pageRepository;
    private final UserRepository userRepository;
    private final PagePostCommentRepository pagePostCommentRepository;

    public PagePostResponse save(PagePostRequest pagePostRequest){
        try{
            Page page = pageRepository.findById(pagePostRequest.getPageId()).orElseThrow();
            PagePost post = PagePost.builder()
                    .text(pagePostRequest.getPostText())
                    .imageData(pagePostRequest.getImageInBase64())
                    .dateAndTime(LocalDateTime.now())
                    .page(page)
                    .build();

            PagePost savedPagePost = postRepository.save(post);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = savedPagePost.getDateAndTime().format(formatter);

            return PagePostResponse.builder()
                    .isLiked(false)
                    .pageId(savedPagePost.getId())
                    .name(savedPagePost.getPage().getName())
                    .profileImage(savedPagePost.getPage().getAvatar())
                    .imageInBase64(savedPagePost.getImageData())
                    .text(savedPagePost.getText())
                    .dateAndTime(formattedDateTime)
                    .numberOfLikes(0)
                    .numberOfComments(0)
                    .build();
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    private List<UserResponse> getUserResponseListFromUserList(List<User> userList){
        List<UserResponse> retList = new ArrayList<>();

        for (User user : userList){

            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .dateOfBirth(user.getDateOfBirth().toString())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .gender(user.getGender())
                    .profileImage(user.getProfileImage())
                    .build();

            retList.add(userResponse);
        }

        return retList;
    }
    private PagePostsResponse getPagePostsResponseFromPostsList(List<PagePost> posts, User user){
        PagePostsResponse postsResponse = new PagePostsResponse();
        List<PagePostResponse> postResponseList = new ArrayList<>();
        for(PagePost post : posts){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = post.getDateAndTime().format(formatter);
            boolean isLiked = post.getLikes().contains(user);
            int numberOfComments = pagePostCommentRepository.countAllCommentsByPostId(post.getId());

            PagePostResponse postResponse = PagePostResponse.builder()
                    .postId(post.getId())
                    .pageId(post.getPage().getId())
                    .name(post.getPage().getName())
                    .imageInBase64(post.getImageData())
                    .text(post.getText())
                    .dateAndTime(formattedDateTime)
                    .isLiked(isLiked)
                    .numberOfLikes(post.getLikes().size())
                    .numberOfComments(numberOfComments)
                    .build();
            postResponseList.add(postResponse);
        }
        postsResponse.setPosts(postResponseList);
        Collections.sort(postsResponse.getPosts(), Collections.reverseOrder());
        return postsResponse;
    }

    @Transactional
    public PagePostsResponse getPagePosts(Integer pageId, Integer myId){
        try{
            List<PagePost> postList = postRepository.findAllByPageId(pageId);
            User user = userRepository.findById(myId).orElseThrow();
            PagePostsResponse postsResponse = getPagePostsResponseFromPostsList(postList, user);
            return postsResponse;
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Transactional
    public void likePost(Integer userId, Integer postId) {
        try{
            User user = userRepository.findById(userId).orElseThrow();

            PagePost post = postRepository.findById(postId).orElseThrow();

            List<PagePost> likedPosts = user.getLikedPagePosts();
            likedPosts.add(post);
            user.setLikedPagePosts(likedPosts);
            userRepository.save(user);
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Transactional
    public void unlikePost(Integer userId, Integer postId) {
        try{
            User user = userRepository.findById(userId).orElseThrow();

            PagePost post = postRepository.findById(postId).orElseThrow();

            List<PagePost> likedPosts = user.getLikedPagePosts();
            likedPosts.remove(post);
            user.setLikedPagePosts(likedPosts);
            userRepository.save(user);
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }
}
