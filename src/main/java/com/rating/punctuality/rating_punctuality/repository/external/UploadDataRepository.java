package com.rating.punctuality.rating_punctuality.repository.external;

import org.springframework.stereotype.Repository;

import com.rating.punctuality.rating_punctuality.model.external.UploadData;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UploadDataRepository extends JpaRepository<UploadData, Long>{}
