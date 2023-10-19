package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.image.ImageResponse;
import com.example.ConnectUs.model.postgres.PagePostImage;
import com.example.ConnectUs.repository.postgres.PagePostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagePostImageService {
    private final PagePostImageRepository imageRepository;

    public PagePostImage save(PagePostImage pagePostImage) {
        return imageRepository.save(pagePostImage);
    }

    public List<ImageResponse> getPagePhotos(Integer pageId) {
        List<PagePostImage> images = imageRepository.findImagesByPageId(pageId);
        List<ImageResponse> imageResponses = new ArrayList<>();

        for (PagePostImage i : images) {
            imageResponses.add(ImageResponse.builder()
                    .id(i.getId())
                    .pagePostId(i.getPagePost().getId())
                    .postId(-1)
                    .image(i.getImage())
                    .build());
        }

        return imageResponses;
    }
}
