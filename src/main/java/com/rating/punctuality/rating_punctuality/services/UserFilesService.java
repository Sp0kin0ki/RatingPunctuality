package com.rating.punctuality.rating_punctuality.services;

import java.util.List;

import com.rating.punctuality.rating_punctuality.model.entities.UserFiles;

public interface UserFilesService {
    UserFiles saveFile(UserFiles userFiles);

    List<UserFiles> getAllUserFiles(String userId);
}
