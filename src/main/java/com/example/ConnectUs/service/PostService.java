package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.post.PostResponse;
import com.example.ConnectUs.dto.post.PostsResponse;
import com.example.ConnectUs.model.postgres.Post;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.postgres.PostRepository;
import com.example.ConnectUs.util.ImageUtil;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public Post save(Post post){
        return postRepository.save(post);
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

    public String generateUniqueFileName(User user) {
        String email = user.getEmail().replaceAll("[^a-zA-Z0-9]", "_");
        String timestamp = String.valueOf(System.currentTimeMillis());

        return email + "_" + timestamp + ".png";
    }

    /*public PostsResponse getPostsForFeed(Integer id){
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> postResponseList = new ArrayList<>();

        List<Post> posts = postRepository.findAllByUserId(id);
        for(Post post : posts) {
            Resource imgFile = new ClassPathResource("images" + File.separator + post.getImageName());

            try {
                InputStreamResource isr = new InputStreamResource(imgFile.getInputStream());
                byte[] bytes = isr.getContentAsByteArray();
                String image = Base64.getEncoder().encodeToString(bytes);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = post.getDateAndTime().format(formatter);

                PostResponse postResponse = PostResponse.builder()
                        .id(post.getId())
                        .imageInBase64(image)
                        .text(post.getText())
                        .dateAndTime(formattedDateTime)
                        .build();
                postResponseList.add(postResponse);
            } catch (Exception e) {
                return new PostsResponse();
            }
        }
        postsResponse.setPosts(postResponseList);
        return postsResponse;
    }*/
    /*ovo brisi public PostsResponse getPostsForFeed(Integer id){
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> postResponseList = new ArrayList<>();

        List<Post> posts = postRepository.findAllByUserId(id);
        for(Post post : posts) {
            try {
                String image = Base64.getEncoder().encodeToString(ImageUtil.decompressImage(post.getImageData()));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = post.getDateAndTime().format(formatter);

                PostResponse postResponse = PostResponse.builder()
                        .id(post.getId())
                        .imageInBase64(image)
                        .text(post.getText())
                        .dateAndTime(formattedDateTime)
                        .build();
                postResponseList.add(postResponse);
            } catch (Exception e) {
                return new PostsResponse();
            }
        }
        postsResponse.setPosts(postResponseList);
        return postsResponse;
    }*/

    public PostsResponse getPostsForFeed(Integer id){
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> postResponseList = new ArrayList<>();

        List<Post> posts = postRepository.findAllByUserId(id);
        for(Post post : posts) {

            try {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = post.getDateAndTime().format(formatter);

                PostResponse postResponse = PostResponse.builder()
                        .id(post.getId())
                        .imageInBase64(post.getImageData())
                        .text(post.getText())
                        .dateAndTime(formattedDateTime)
                        .build();
                postResponseList.add(postResponse);
            } catch (Exception e) {
                return new PostsResponse();
            }
        }
        postsResponse.setPosts(postResponseList);
        return postsResponse;
    }
}
