# PlacesTogether

Aplicación web para guardar y explorar los lugares de viaje compartidos con tu pareja.

## Stack
- Backend: Java 17 + Spring Boot 3
- Frontend: HTML, CSS y JavaScript vanilla
- Base de datos: PostgreSQL para desarrollo y producción

## Ejecutar localmente
1. Crear una base de datos PostgreSQL llamada `placestogether`
2. Configurar las variables de entorno:
   - `DB_URL=jdbc:postgresql://localhost:5432/placestogether`
   - `DB_USERNAME=postgres`
   - `DB_PASSWORD=postgres`
3. Ejecutar `mvn spring-boot:run`
4. Abrir `http://localhost:8080`

## Pruebas
- Para ejecutar las pruebas con una base de datos temporal, usa `mvn test -Dspring.profiles.active=test`

## Endpoints principales
- `GET /api/places`
- `GET /api/places/{id}`
- `POST /api/places`
- `POST /api/places/{placeId}/photos`
- `GET /api/health`
