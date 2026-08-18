package com.placestogether.service;

import com.placestogether.api.PlaceDtos.PhotoRequest;
import com.placestogether.api.PlaceDtos.PhotoResponse;
import com.placestogether.api.PlaceDtos.PlaceRequest;
import com.placestogether.api.PlaceDtos.PlaceResponse;
import com.placestogether.model.Photo;
import com.placestogether.model.Place;
import com.placestogether.repository.PhotoRepository;
import com.placestogether.repository.PlaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class PlacesService {
    private final PlaceRepository placeRepository;
    private final PhotoRepository photoRepository;

    public PlacesService(PlaceRepository placeRepository, PhotoRepository photoRepository) {
        this.placeRepository = placeRepository;
        this.photoRepository = photoRepository;
    }

    @Transactional(readOnly = true)
    public List<PlaceResponse> getAllPlaces() {
        return placeRepository.findAll().stream()
                .sorted(Comparator.comparing(Place::getVisitDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toPlaceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlaceResponse getPlaceById(Long id) {
        return toPlaceResponse(getPlaceOrThrow(id));
    }

    @Transactional(readOnly = true)
    public byte[] getPhotoContent(Long id) {
        return photoRepository.findById(id)
                .map(Photo::getContent)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto no encontrada"));
    }

    @Transactional(readOnly = true)
    public String getPhotoContentType(Long id) {
        return photoRepository.findById(id)
                .map(Photo::getContentType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto no encontrada"));
    }

    @Transactional
    public PlaceResponse createPlace(PlaceRequest request) {
        Place place = new Place();
        place.setName(request.name());
        place.setCountry(request.country());
        place.setDescription(request.description());
        // coordinates removed from UI; keep DB nullable
        place.setVisitDate(request.visitDate());
        place.setCoverImageUrl(request.coverImageUrl());

        Place saved = placeRepository.save(place);
        return toPlaceResponse(saved);
    }

    @Transactional
    public PhotoResponse createPhoto(Long placeId, PhotoRequest request, MultipartFile file) {
        Place place = getPlaceOrThrow(placeId);

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes adjuntar una imagen");
        }

        try {
            Photo photo = new Photo();
            photo.setTitle(request.title());
            photo.setDescription(request.description());
            photo.setContent(file.getBytes());
            photo.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
            photo.setFileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload");
            photo.setSize(file.getSize());
            place.addPhoto(photo);
            placeRepository.save(place);
            return toPhotoResponse(photo);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo procesar la imagen", exception);
        }
    }

    private Place getPlaceOrThrow(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lugar no encontrado"));
    }

    private PlaceResponse toPlaceResponse(Place place) {
        return new PlaceResponse(
            place.getId(),
            place.getName(),
            place.getCountry(),
            place.getDescription(),
            place.getVisitDate(),
            place.getCoverImageUrl(),
            place.getPhotos().stream().map(this::toPhotoResponse).toList());
    }

    private PhotoResponse toPhotoResponse(Photo photo) {
        return new PhotoResponse(
                photo.getId(),
                photo.getTitle(),
                "/api/photos/" + photo.getId() + "/content",
                photo.getDescription(),
                photo.getContentType(),
                photo.getFileName(),
                photo.getSize());
    }
}
