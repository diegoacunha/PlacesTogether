package com.placestogether.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlacesControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void shouldUploadPhotoAndPersistIt() throws Exception {
                String placePayload = """
                                {
                                        "name": "Lisboa",
                                        "country": "Portugal",
                                        "description": "Una escapada de primavera",
                                        "visitDate": "2024-05-01",
                                        "coverImageUrl": ""
                                }
                                """;

                MvcResult placeResult = mockMvc.perform(post("/api/places")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(placePayload))
                                .andExpect(status().isCreated())
                                .andReturn();

                JsonNode placeNode = objectMapper.readTree(placeResult.getResponse().getContentAsString());
                Long placeId = placeNode.get("id").asLong();

                MockMultipartFile photoFile = new MockMultipartFile(
                                "file",
                                "trip.png",
                                "image/png",
                                new byte[] { 1, 2, 3, 4 });

                mockMvc.perform(multipart("/api/places/{placeId}/photos", placeId)
                                .file(photoFile)
                                .param("title", "Miradouro")
                                .param("description", "Foto de prueba"))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.title").value("Miradouro"));

                mockMvc.perform(get("/api/places/{id}", placeId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.photos[0].title").value("Miradouro"));
        }

        @Test
        void shouldUploadCoverImageForPlace() throws Exception {
                String placePayload = """
                                {
                                        "name": "Sevilla",
                                        "country": "España",
                                        "description": "Ciudad de la luz",
                                        "visitDate": "2024-06-01",
                                        "coverImageUrl": ""
                                }
                                """;

                MvcResult placeResult = mockMvc.perform(post("/api/places")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(placePayload))
                                .andExpect(status().isCreated())
                                .andReturn();

                JsonNode placeNode = objectMapper.readTree(placeResult.getResponse().getContentAsString());
                Long placeId = placeNode.get("id").asLong();

                MockMultipartFile coverFile = new MockMultipartFile(
                                "file",
                                "cover.png",
                                "image/png",
                                new byte[] { 9, 8, 7, 6 });

                mockMvc.perform(multipart("/api/places/{placeId}/cover-image", placeId)
                                .file(coverFile)
                                .param("title", "Portada")
                                .param("description", "Imagen de portada"))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.coverImageUrl").value(
                                                org.hamcrest.Matchers.matchesPattern("/api/photos/[0-9]+/content")));
        }
}
