package com.placestogether.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class PlaceDtos {
    public record PlaceRequest(
            @NotBlank String name,
            @NotBlank String country,
            String description,
            LocalDate visitDate,
            String coverImageUrl) {
    }

    public record PhotoRequest(
            @NotBlank String title,
            String description) {
    }

    public record PlaceResponse(
            Long id,
            String name,
            String country,
            String description,
            LocalDate visitDate,
            String coverImageUrl,
            List<PhotoResponse> photos) {
    }

    public record PhotoResponse(
            Long id,
            String title,
            String imageUrl,
            String description,
            String contentType,
            String fileName,
            long size) {
    }
}
