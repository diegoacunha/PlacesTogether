package com.placestogether.config;

import com.placestogether.model.Photo;
import com.placestogether.model.Place;
import com.placestogether.repository.PlaceRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class DataSeeder implements ApplicationRunner {
    private final PlaceRepository placeRepository;

    public DataSeeder(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (placeRepository.count() > 0) {
            return;
        }

        Place santorini = new Place();
        santorini.setName("Santorini");
        santorini.setCountry("Grecia");
        santorini.setDescription("Atardeceres inolvidables y callejones blancos junto al mar.");
        santorini.setVisitDate(java.time.LocalDate.of(2024, 7, 14));
        santorini.addPhoto(createPhoto("Atardecer", "Una foto de ejemplo para recordar la caldera.", "image/png",
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAACklEQVR4nGMAAQwAAgAA5DUw0A0wAAAAAElFTkSuQmCC"));

        Place kyoto = new Place();
        kyoto.setName("Kyoto");
        kyoto.setCountry("Japón");
        kyoto.setDescription("Templos, jardines y una escapada de otoño llena de calma.");
        kyoto.setVisitDate(java.time.LocalDate.of(2023, 11, 21));
        kyoto.addPhoto(createPhoto("Templo", "Un momento mágico en los senderos del templo.", "image/png",
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAACklEQVR4nGMAAQwAAgAA2Y5S4g0QAAAAAElFTkSuQmCC"));

        placeRepository.save(santorini);
        placeRepository.save(kyoto);
    }

    private Photo createPhoto(String title, String description, String contentType, String base64Content) {
        Photo photo = new Photo();
        photo.setTitle(title);
        photo.setDescription(description);
        photo.setContentType(contentType);
        photo.setContent(Base64.getDecoder().decode(base64Content));
        photo.setFileName(title.toLowerCase().replace(" ", "-") + ".png");
        photo.setSize(photo.getContent().length);
        return photo;
    }
}
