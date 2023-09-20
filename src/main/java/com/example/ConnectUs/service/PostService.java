package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.post.PostResponse;
import com.example.ConnectUs.model.postgres.Post;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.postgres.PostRepository;
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
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public Post save(Post post){
        return postRepository.save(post);
    }
    @Transactional
    public PostResponse getById(Integer id) {
        Post post = postRepository.getById(id);
        Resource imgFile = new ClassPathResource("images" + File.separator + post.getImageName());

        try{
            InputStreamResource isr = new InputStreamResource(imgFile.getInputStream());
            byte[] bytes = isr.getContentAsByteArray();
            String image = Base64.getEncoder().encodeToString(bytes);
            return PostResponse.builder()
                    .id(post.getId())
                    .imageInBase64(image)
                    .text(post.getText())
                    .dateAndTime(post.getDateAndTime())
                    .build();
        }catch(Exception e){
            return new PostResponse();
        }


    }

    public String generateUniqueFileName(User user) {
        String email = user.getEmail().replaceAll("[^a-zA-Z0-9]", "_");
        String timestamp = String.valueOf(System.currentTimeMillis());

        return email + "_" + timestamp + ".png";
    }
}
