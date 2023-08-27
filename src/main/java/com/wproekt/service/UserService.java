package com.wproekt.service;

import com.wproekt.model.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService extends UserDetailsService {
    User reattachUser(User user);

    User register(String username, String password, String repeatPassword, String name, String surname, String email, String host);

    List<Card> getHomePageCards(String username);

    List<Card> getTrashPageCards(String username);

    List<Card> getArchivePageCards(String username);

    List<Card> getLabelFilteredCards(String username, Long labelId);

    Note addNoteCard(String username, String title, String Text);

    TaskCard addTaskCard(String username, String title);

    boolean verifyToken(String username, String token);

    Set<Label> getUserLabels(String username);

    Label addLabelToUser(String username, String label);

    void removeLabelFromUser(String username, Long labelId);

    String getHashedUsername(User user);

    void deleteAllCards(String username, String type);
    void restoreAllCards(String username, String type);

    static String getStaticDir() {
        String STATIC_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static";

        return STATIC_DIRECTORY;
    }

    static String getUploadDir() {
        String UPLOAD_DIRECTORY = getStaticDir() + "/uploads";

        return UPLOAD_DIRECTORY;
    }


}
