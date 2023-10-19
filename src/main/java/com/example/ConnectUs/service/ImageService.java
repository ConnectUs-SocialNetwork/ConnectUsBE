package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.image.ImageResponse;
import com.example.ConnectUs.model.postgres.Image;
import com.example.ConnectUs.repository.postgres.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public Image save(Image image) {
        return imageRepository.save(image);
    }

    public List<ImageResponse> getUserImages(Integer userId) {
        List<Image> images = imageRepository.findImagesByUserId(userId);
        List<ImageResponse> imageResponses = new ArrayList<>();
        for (Image i : images) {
            ImageResponse imageResponse = ImageResponse.builder()
                    .image(i.getImage())
                    .postId(i.getPost().getId())
                    .id(i.getId())
                    .build();
            imageResponses.add(imageResponse);
        }
        return imageResponses;
    }
}
