package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.page.PageRequest;
import com.example.ConnectUs.dto.page.ViewPageResponse;
import com.example.ConnectUs.enumerations.PageCategory;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.neo4j.PageNeo4j;
import com.example.ConnectUs.model.postgres.Page;
import com.example.ConnectUs.model.postgres.Post;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.neo4j.PageNeo4jRepository;
import com.example.ConnectUs.repository.postgres.PageRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    private final PageNeo4jRepository pageNeo4jRepository;
    private final UserRepository userRepository;

    @Transactional(value = "chainedTransactionManager")
    public Page save(PageRequest pageRequest){
        try{
            User administrator = userRepository.findById(pageRequest.getAdministratorId()).orElseThrow();
            PageCategory category;
            if(pageRequest.getCategory().equals("Travel")){
                category = PageCategory.TRAVEL;
            }else if(pageRequest.getCategory().equals("Food")){
                category = PageCategory.FOOD;
            }else if(pageRequest.getCategory().equals("Fashion")){
                category = PageCategory.FASHION;
            }else if(pageRequest.getCategory().equals("Fitness and Wellness")){
                category = PageCategory.FITNESS_AND_WELLNES;
            }else{
                category = PageCategory.ENTERTAINMENT;
            }

            Page page = Page.builder()
                    .administrator(administrator)
                    .avatar("")
                    .category(category)
                    .description(pageRequest.getDescription())
                    .name(pageRequest.getName())
                    .build();

            pageRepository.save(page);

            PageNeo4j pageNeo4j = PageNeo4j.builder()
                    .id(page.getId().longValue())
                    .category(category)
                    .build();

            pageNeo4jRepository.save(pageNeo4j);

            return page;
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public ViewPageResponse getViewPageResponse(Integer pageId, Integer userId){
        try{
            Page page = pageRepository.findById(pageId).orElseThrow();
            int numberOfLikes = pageNeo4jRepository.getNumberOfLikes(pageId.longValue());
            boolean isLikedByUser = pageNeo4jRepository.isLikedByUser(pageId.longValue(), userId.longValue());

            ViewPageResponse pageResponse = ViewPageResponse.builder()
                    .pageId(page.getId())
                    .category(page.getCategory().toString())
                    .description(page.getDescription())
                    .name(page.getName())
                    .numberOfLikes(numberOfLikes)
                    .liked(isLikedByUser)
                    .build();

            return pageResponse;
        }catch(DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }}
