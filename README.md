# API de Clima y Recomendación de Vestimenta

## Descripción del proyecto

Esta API REST tiene como propósito ayudar a los usuarios a tomar mejores decisiones sobre su vestimenta diaria, considerando las condiciones climáticas de una ciudad.

A diferencia de otras aplicaciones meteorológicas, el sistema no solo muestra datos del clima, sino que también genera sugerencias prácticas, como el tipo de ropa o accesorios que se deberían utilizar.

Además, permite a los usuarios registrarse, iniciar sesión, guardar ciudades favoritas y consultar su historial de búsquedas.

## Ramas principales

- `main`: rama final del proyecto.
- `develop`: rama de integración.
- `feature/*`: ramas de funcionalidades específicas.

Ramas destacadas:

- `feature/estructura-base`
- `feature/auth`
- `feature/clima`
- `feature/recomendaciones`
- `feature/favoritos`
- `feature/historial`
- `feature/usuarios`
- `feature/Imagen-Docker`
- `feature/ci-cd`

---


## Arquitectura del sistema

El sistema utiliza una arquitectura en capas y expone sus funcionalidades mediante una API REST.
* Capa de presentación recibe las peticiones , valida los datos y devuelve una respuesta 
* Capa de negocio: controla todo el comportamiento del sistema  
* Capa de persistencia: acceso a la BD
* OpenWeatherMap: servicio externo integrado en la capa de negocio

<img width="561" height="738" alt="image" src="https://github.com/user-attachments/assets/98ab04f5-65d7-417a-bec2-0dbeb1750a9c" />

---

## Estructura general

```text
clima-api/
+-- src/
|   +-- main/
|   |   +-- java/com/parde4/climaapi/
|   |   |   +-- controller/
|   |   |   +-- dto/
|   |   |   +-- exception/
|   |   |   +-- model/
|   |   |   +-- repository/
|   |   |   +-- service/
|   |   |   +-- ClimaApiApplication.java
|   |   +-- resources/
|   |       +-- application.yaml
|   |       +-- static/
|   +-- test/
|       +-- java/com/parde4/climaapi/
+-- postman/
|   +-- Clima-API.postman_collection.json
+-- docker-compose.yml
+-- Dockerfile
+-- pom.xml
+-- README.md
```

---

## Tecnologías utilizadas

* Java 17
* Spring Boot 4.0.5
* Spring Web
* Spring Data JPA
* Spring Validation
* Maven
* MySQL
* Docker y Docker Compose
* Git & GitHub
* GitHub Actions (CI/CD)
* Postman
* OpenWeatherMap API
---

---
## Endpoints

### Auth

```http
POST /auth/login
POST /auth/logout
```

### Usuarios

```http
POST /usuarios
GET /usuarios/{id}
PUT /usuarios/{id}
PATCH /usuarios/{id}
DELETE /usuarios/{id}
```

### Clima

```http
GET /clima/{ciudad}
GET /clima/{ciudad}/pronostico
```

Los endpoints de clima requieren iniciar sesión previamente.

### Recomendaciones

```http
GET /recomendaciones/{ciudad}
```

Este endpoint requiere iniciar sesion previamente.


El sistema genera recomendaciones con base en rangos de temperatura:

- Frio extremo: menos de 0 C.
- Frio moderado: de 0 C a 12 C.
- Templado/fresco: de 13 C a 20 C.
- Calido: de 21 C a 28 C.
- Calor extremo: mas de 28 C.

Tambien agrega alertas cuando aplica:

- Lluvia: paraguas, chamarra impermeable y calzado impermeable.
- Nieve/hielo: ropa impermeable y botas con suela antideslizante.
- Viento fuerte: chaqueta rompevientos y recomendaciones adicionales.

---

### Favoritos

```http
POST /favoritos
GET /favoritos
DELETE /favoritos/{id}
```

Los endpoints de favoritos requieren iniciar sesion previamente.

### Historial

```http
GET /historial
DELETE /historial
```

Los endpoints de historial requieren iniciar sesion previamente.

---

## Coleccion de endpoints

La documentacion tecnica interactiva se encuentra en la coleccion de Postman:

```text
postman/Clima-API.postman_collection.json
```

Para utilizarla:

1. Abrir Postman.
2. Seleccionar `Import`.
3. Cargar el archivo `postman/Clima-API.postman_collection.json`.
4. Ejecutar primero `Usuarios > Crear usuario`.
5. Ejecutar `Auth > Login`.
6. Probar los endpoints protegidos.

La variable `baseUrl` viene configurada como:

```text
http://localhost:8080
```

---

## Base de datos

El sistema utiliza **MySQL** como base de datos.

### ¿Por qué se utilizó MySQL?

Se eligió MySQL porque el proyecto maneja información estructurada y relaciones claras entre entidades. En este caso, los usuarios se relacionan con sus favoritos y con su historial de consultas.

* La información se organiza en tablas
* Cada tabla contiene filas y columnas
* Las tablas pueden relacionarse entre sí mediante claves primarias y foráneas

Este tipo de base de datos fue adecuado para el proyecto porque permite controlar relaciones como:

* un usuario puede tener muchos favoritos
* un usuario puede tener muchos registros en su historial

---

## Modelo entidad-relación

<img width="706" height="245" alt="Captura de pantalla 2026-04-19 a la(s) 21 32 59" src="https://github.com/user-attachments/assets/7d39a75e-abb8-409b-bb4d-860857c9e711" />

El modelo está compuesto por tres tablas principales:

### 1. Usuario

La tabla `usuario` representa a cada usuario registrado en el sistema.

Campos principales:

* `idusuario`
* `nombre`
* `correo`
* `contraseña`
* `fecha_creado`

Esta es la entidad principal del sistema.

---

### 2. Favorito

La tabla `favorito` almacena las ciudades favoritas guardadas por los usuarios.

Campos principales:

* `idfavorito`
* `ciudad`
* `pais`
* `fecha_agregado`
* `usuario_idusuario`

La columna `usuario_idusuario` funciona como clave foránea y conecta cada favorito con un usuario.

---

### 3. Historial

La tabla `historial` registra las consultas de clima realizadas por los usuarios.

Campos principales:

* `idhistorial`
* `ciudad`
* `fecha_consulta`
* `usuario_idusuario`

Al igual que en `favorito`, la columna `usuario_idusuario` relaciona cada registro del historial con un usuario.

---

## Ejecucion local con Maven

Primero se debe tener MySQL disponible en `localhost:3307`.

Con Docker se puede levantar solo la base de datos:

```bash
docker compose up -d mysql
```

Despues ejecutar la aplicación:

```bash
mvn spring-boot:run
```

La aplicación quedara disponible en: http://localhost:8080

## Ejecucion con Docker Compose 

Para levantar base de datos y aplicación:

```bash
docker compose up
```

Para descargar la versión más reciente ejecuta 
docker compose pull
docker compose up
```

La aplicación queda disponible en:

```text
http://localhost:8080
```

## Ejecucion sin código fuente

Una persona que no tenga el código puede ejecutar el sistema usando las imagenes publicadas.

Crear la red:

```bash
docker network create clima-net
```

Levantar la base de datos:

```bash
docker run -d --name clima_db --network clima-net -p 3307:3306 elisasc/clima-db
```

Bajar la imagen de la API:

```bash
docker pull patracamiguel/clima-api:4.0
```

Levantar la API:

```bash
docker run -d --name clima_app --network clima-net -p 8080:8080 patracamiguel/clima-api:4.0
```

Abrir en el navegador:

```text
http://localhost:8080
```

Si ya existen contenedores con esos nombres, se pueden eliminar antes:

```bash
docker rm -f clima_db clima_app
```

## Imagen de la base de datos

Se creó una imagen personalizada basada en MySQL que incluye:

* Creación automática de la base de datos `clima_app`
* Configuración de credenciales
* Ejecución del script inicial `ClimaApp.sql`
* Estructura de tablas (`usuario`, `favorito`, `historial`)

## Pipeline CI/CD

El proyecto utiliza GitHub Actions para automatizar:

* Compilación del proyecto
* Ejecución de pruebas
* Construcción de la imagen Docker

Cada push al repositorio ejecuta automáticamente el pipeline.


## Imagen Docker

Imagen publicada:

```text
patracamiguel/clima-api:4.0
patracamiguel/clima-api:latest
```

La version `4.0` es la version fija recomendada para el equipo. La etiqueta `latest` apunta actualmente a la misma version.

La imagen fue publicada como multi-arquitectura:

```text
linux/amd64
linux/arm64
```

Esto permite ejecutarla en Linux y Mac

## Imagen en DockerHub
https://hub.docker.com/repositories/patracamiguel

## Variables de entorno

La aplicación usa las siguientes variables:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
WEATHER_API_KEY
```

En Docker Compose se usan valores por defecto para desarrollo local. Si se desea personalizar la configuracion, se puede crear un archivo `.env` tomando como referencia:

```text
.env.example
```

Ejemplo:

```text
DB_PASSWORD=parde4
WEATHER_API_KEY=tu_api_key_de_openweathermap
```

---

## Pruebas

Para ejecutar todas las pruebas:

```bash
mvn test
```

Ejecutar pruebas de recomendaciones:

```bash
mvn -Dtest=RecomendacionesControllerTest test
```

En la ultima verificacion se ejecutaron 68 pruebas correctamente.

---


