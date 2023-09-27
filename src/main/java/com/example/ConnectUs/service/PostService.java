package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.authentication.UserResponse;
import com.example.ConnectUs.dto.post.PostResponse;
import com.example.ConnectUs.dto.post.PostsResponse;
import com.example.ConnectUs.model.postgres.Post;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.postgres.PostRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse save(Post post){
        Post savedPost = postRepository.save(post);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = savedPost.getDateAndTime().format(formatter);
        return PostResponse.builder()
                .isLiked(false)
                .id(savedPost.getId())
                .imageInBase64(savedPost.getImageData())
                .text(savedPost.getText())
                .dateAndTime(formattedDateTime)
                .likes(new ArrayList<UserResponse>())
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
    public PostsResponse getPostsForFeed(Integer id){
        Optional<User> user = userRepository.findById(id);

        List<Post> posts = postRepository.findAllByUserId(id);

        PostsResponse postsResponse = getPostsResponseFromPostsList(posts, user.get());

        return postsResponse;
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

    private PostsResponse getPostsResponseFromPostsList(List<Post> posts, User user){
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> postResponseList = new ArrayList<>();
        for(Post post : posts){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = post.getDateAndTime().format(formatter);
            boolean isLiked = post.getLikes().contains(user);

            PostResponse postResponse = PostResponse.builder()
                    .id(post.getId())
                    .imageInBase64(post.getImageData())
                    .text(post.getText())
                    .dateAndTime(formattedDateTime)
                    .isLiked(isLiked)
                    .likes(getUserResponseListFromUserList(post.getLikes()))
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

        List<Post> likedPosts = user.getLikedPosts();
        likedPosts.add(post);
        user.setLikedPosts(likedPosts);
        userRepository.save(user);
    }

    @Transactional
    public void unlikePost(Integer userId, Integer postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        List<Post> likedPosts = user.getLikedPosts();
        likedPosts.remove(post);
        user.setLikedPosts(likedPosts);
        userRepository.save(user);
    }
}
