package com.rating.punctuality.rating_punctuality.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.rating.punctuality.rating_punctuality.model.entities.Flight;

public interface UploadDataService {

    public boolean saveFlights(List<Flight> csvData);

    public List<Flight> parseCsvFileSimple(MultipartFile file) throws IOException;

}
