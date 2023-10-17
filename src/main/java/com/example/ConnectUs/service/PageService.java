package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.authentication.UserResponse;
import com.example.ConnectUs.dto.page.*;
import com.example.ConnectUs.dto.searchUsers.SearchUserResponse;
import com.example.ConnectUs.enumerations.NotificationType;
import com.example.ConnectUs.enumerations.PageCategory;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.neo4j.PageNeo4j;
import com.example.ConnectUs.model.neo4j.UserNeo4j;
import com.example.ConnectUs.model.postgres.Notification;
import com.example.ConnectUs.model.postgres.Page;
import com.example.ConnectUs.model.postgres.Post;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.neo4j.PageNeo4jRepository;
import com.example.ConnectUs.repository.neo4j.UserNeo4jRepository;
import com.example.ConnectUs.repository.postgres.PageRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    private final PageNeo4jRepository pageNeo4jRepository;
    private final UserRepository userRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final NotificationService notificationService;

    @Transactional(value = "chainedTransactionManager")
    public Page save(PageRequest pageRequest) {
        try {
            User administrator = userRepository.findById(pageRequest.getAdministratorId()).orElseThrow();
            PageCategory category;
            if (pageRequest.getCategory().equals("Travel")) {
                category = PageCategory.TRAVEL;
            } else if (pageRequest.getCategory().equals("Food")) {
                category = PageCategory.FOOD;
            } else if (pageRequest.getCategory().equals("Fashion")) {
                category = PageCategory.FASHION;
            } else if (pageRequest.getCategory().equals("Fitness and Wellness")) {
                category = PageCategory.FITNESS_AND_WELLNES;
            } else {
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
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public ViewPageResponse getViewPageResponse(Integer pageId, Integer userId) {
        try {
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
                    .avatar(page.getAvatar())
                    .build();

            return pageResponse;
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public void likePage(Integer pageId, Integer userId) {
        try {
            UserNeo4j user = userNeo4jRepository.findUserById(userId);
            PageNeo4j page = pageNeo4jRepository.findPageById(pageId);
            Page postgresPage = pageRepository.findById(pageId).orElseThrow();
            User postgresUser = userRepository.findById(postgresPage.getAdministrator().getId()).orElseThrow();

            List<UserNeo4j> usersWhoLikedPage = page.getUsersWhoLikedPage();
            usersWhoLikedPage.add(user);
            page.setUsersWhoLikedPage(usersWhoLikedPage);

            pageNeo4jRepository.save(page);

            if (postgresPage.getAdministrator().getId() != user.getId().intValue()) {
                notificationService.save(Notification.builder()
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .user(postgresUser)
                        .avatar(user.getProfileImage())
                        .type(NotificationType.PAGE_LIKE)
                        .dateAndTime(LocalDateTime.now())
                        .entityId(page.getId().intValue())
                        .isRead(false)
                        .text("liked your page. Click on the notification to see the page.")
                        .build());
            }
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public void unlikePage(Integer pageId, Integer userId) {
        try {
            pageNeo4jRepository.unlikePage(pageId.longValue(), userId.longValue());
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
                    .gender(user.getGender())
                    .profileImage(user.getProfileImage())
                    .build();

            retList.add(userResponse);
        }

        return retList;
    }

    public List<SearchUserResponse> getLikers(Integer pageId, Integer myId) {
        try {
            User myUser = userRepository.findById(myId).orElseThrow();
            List<UserNeo4j> likers = pageNeo4jRepository.getLikers(pageId);
            List<Integer> ids = new ArrayList<>();
            for (UserNeo4j u : likers) {
                ids.add(u.getId().intValue());
            }
            List<User> userList = userRepository.findByIdIn(ids);

            List<SearchUserResponse> responseList = new ArrayList<>();
            for (User u : userList) {
                SearchUserResponse searchUserResponse = SearchUserResponse.builder()
                        .id(u.getId())
                        .profileImage(u.getProfileImage())
                        .friend(myUser.getFriends().contains(u))
                        .email(u.getEmail())
                        .firstname(u.getFirstname())
                        .lastname(u.getLastname())
                        .build();
                responseList.add(searchUserResponse);
            }
            return responseList;

        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Transactional
    public List<SearchPageResponse> searchPages(String searchText, Integer userId) {
        try{
            List<Page> pages = pageRepository.findPagesBySearchText(searchText);
            List<SearchPageResponse> searchUserResponses = new ArrayList<>();

            for (Page page : pages) {
                Integer numberOfLikes = pageNeo4jRepository.getNumberOfLikes(page.getId());
                boolean liked = pageNeo4jRepository.isLikedByUser(page.getId().longValue(), userId.longValue());

                SearchPageResponse searchPageResponse = SearchPageResponse.builder()
                        .id(page.getId())
                        .administratorId(page.getAdministrator().getId())
                        .avatar(page.getAvatar())
                        .category(transformCategoryString(page.getCategory().toString()))
                        .description(page.getDescription())
                        .liked(liked)
                        .numberOfLikes(numberOfLikes)
                        .name(page.getName())
                        .build();
                searchUserResponses.add(searchPageResponse);
            }

            return searchUserResponses;
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public String transformCategoryString(String category) {
        String[] parts = category.split("_");

        StringBuilder transformedCategory = new StringBuilder();

        for (String part : parts) {
            String firstLetter = part.substring(0, 1).toUpperCase();
            String restOfWord = part.substring(1).toLowerCase();

            if (transformedCategory.length() > 0) {
                transformedCategory.append(" ");
            }
            transformedCategory.append(firstLetter).append(restOfWord);
        }

        return transformedCategory.toString();
    }

    @Transactional(value = "chainedTransactionManager")
    public PageResponse updatePage(UpdatePageRequest updatePageRequest){
        Page page = pageRepository.findById(updatePageRequest.getId()).orElseThrow();
        PageNeo4j pageNeo4j = pageNeo4jRepository.findPageById(updatePageRequest.getId());
        PageResponse pageResponse = new PageResponse();
        if(updatePageRequest.getCategory() == null){
            page.setAvatar(updatePageRequest.getAvatar());
            pageRepository.save(page);
        }else{
            page.setName(updatePageRequest.getName());
            PageCategory category;
            if (updatePageRequest.getCategory().equals("Travel")) {
                category = PageCategory.TRAVEL;
            } else if (updatePageRequest.getCategory().equals("Food")) {
                category = PageCategory.FOOD;
            } else if (updatePageRequest.getCategory().equals("Fashion")) {
                category = PageCategory.FASHION;
            } else if (updatePageRequest.getCategory().equals("Fitness and Wellness")) {
                category = PageCategory.FITNESS_AND_WELLNES;
            } else {
                category = PageCategory.ENTERTAINMENT;
            }
            page.setCategory(category);
            page.setDescription(updatePageRequest.getDescription());
            pageNeo4j.setCategory(category);
            pageRepository.save(page);
            pageNeo4jRepository.save(pageNeo4j);
        }
        pageResponse.setId(page.getId());
        pageResponse.setCategory(page.getCategory().toString());
        pageResponse.setName(page.getName());
        pageResponse.setDescription(page.getDescription());
        pageResponse.setAdministratorId(page.getAdministrator().getId());
        return pageResponse;
    }

    public PageResponse getPage(Integer pageId){
        Page page = pageRepository.findById(pageId).orElseThrow();
        return PageResponse.builder()
                .name(page.getName())
                .description(page.getDescription())
                .category(transformCategoryString(page.getCategory().toString()))
                .administratorId(page.getAdministrator().getId())
                .build();
    }
}
