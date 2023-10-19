package com.example.ConnectUs.service;

import com.example.ConnectUs.model.postgres.Image;
import com.example.ConnectUs.repository.postgres.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public Image save(Image image){
        return imageRepository.save(image);
    }
}
