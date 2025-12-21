package com.rating.punctuality.rating_punctuality.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rating.punctuality.rating_punctuality.model.entities.UserFiles;
import com.rating.punctuality.rating_punctuality.repository.UserFilesRepository;

@Service
public class UserFilesServiceImpl implements UserFilesService {

    private final UserFilesRepository userFilesRepository;

    public UserFilesServiceImpl(UserFilesRepository userFilesRepository) {
        this.userFilesRepository = userFilesRepository;
    }

    @Override
    public UserFiles saveFile(UserFiles userFiles) {
        return userFilesRepository.save(userFiles);
    }

    @Override
    public List<UserFiles> getAllUserFiles(String userId) {
        return userFilesRepository.findByUserId(userId);
    }
    
}
