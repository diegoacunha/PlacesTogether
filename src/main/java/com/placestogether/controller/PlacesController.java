package com.placestogether.controller;

import com.placestogether.api.PlaceDtos.PhotoRequest;
import com.placestogether.api.PlaceDtos.PhotoResponse;
import com.placestogether.api.PlaceDtos.PlaceRequest;
import com.placestogether.api.PlaceDtos.PlaceResponse;
import com.placestogether.service.PlacesService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlacesController {
    private final PlacesService placesService;

    public PlacesController(PlacesService placesService) {
        this.placesService = placesService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/places")
    public List<PlaceResponse> getPlaces() {
        return placesService.getAllPlaces();
    }

    @GetMapping("/places/{id}")
    public PlaceResponse getPlace(@PathVariable Long id) {
        return placesService.getPlaceById(id);
    }

    @GetMapping("/photos/{id}/content")
    public ResponseEntity<byte[]> getPhotoContent(@PathVariable Long id) {
        byte[] content = placesService.getPhotoContent(id);
        String contentType = placesService.getPhotoContentType(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(content);
    }

    @PostMapping("/places")
    public ResponseEntity<PlaceResponse> createPlace(@Valid @RequestBody PlaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(placesService.createPlace(request));
    }

    @PostMapping(value = "/places/{placeId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoResponse> createPhoto(@PathVariable Long placeId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file) {
        PhotoRequest request = new PhotoRequest(title, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(placesService.createPhoto(placeId, request, file));
    }
}
