INSERT INTO places (name, country, description, visit_date, cover_image_url, created_at) VALUES
('Santorini', 'Grecia', 'Atardeceres inolvidables y callejones blancos junto al mar.', '2024-07-14', 'https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?auto=format&fit=crop&w=900&q=80', '2026-01-01T10:00:00Z'),
('Kyoto', 'Japón', 'Templos, jardines y una de las mejores escapadas de otoño.', '2023-11-21', 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=900&q=80', '2026-01-01T10:00:00Z'),
('Marrakech', 'Marruecos', 'Zocos, aromas y un viaje lleno de color.', '2022-03-10', 'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=900&q=80', '2026-01-01T10:00:00Z');

INSERT INTO photos (place_id, title, image_url, description, created_at) VALUES
(1, 'Cielo naranja', 'https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?auto=format&fit=crop&w=800&q=80', 'El atardecer desde la terraza del hotel.', '2026-01-01T10:00:00Z'),
(1, 'Caldera azul', 'https://images.unsplash.com/photo-1573790387438-4da905039392?auto=format&fit=crop&w=800&q=80', 'Un paseo con vistas al mar Egeo.', '2026-01-01T10:00:00Z'),
(2, 'Templo Fushimi Inari', 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=800&q=80', 'La escalera de miles de puertas rojas.', '2026-01-01T10:00:00Z'),
(3, 'Jardín secreto', 'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=800&q=80', 'Una tarde entre palmeras y patios llenos de luz.', '2026-01-01T10:00:00Z');
