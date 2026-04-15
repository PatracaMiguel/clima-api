# API de Clima y Recomendación de Vestimenta

## Descripción del proyecto

Esta API REST tiene como propósito ayudar a los usuarios a tomar mejores decisiones sobre su vestimenta diaria, considerando las condiciones climáticas de una ciudad.

A diferencia de otras aplicaciones meteorológicas, el sistema no solo muestra datos del clima, sino que también genera sugerencias prácticas, como el tipo de ropa o accesorios que se deberían utilizar.

Además, permite a los usuarios registrarse, iniciar sesión, guardar ciudades favoritas y consultar su historial de búsquedas.

---

## Arquitectura del sistema

El sistema está diseñado bajo una arquitectura basada en servicios REST:

* Cliente (Postman o frontend)
* API REST: desarrollada con Spring Boot, procesa las solicitudes y genera respuestas
* API externa (OpenWeatherMap): utilizada para obtener datos climáticos en tiempo real  
* Contenedores Docker

---

## Tecnologías utilizadas

* Java 17
* Spring Boot
* Maven
* MySQL
* Docker y Docker Compose
* Git & GitHub
* GitHub Actions (CI/CD)
* Postman
* OpenWeatherMap API

---

## Variables de entorno

El proyecto utiliza variables de entorno para proteger información sensible.

Ejemplo de archivo `.env.example`:

```
OPENWEATHER_API_KEY=
DB_URL=
DB_USERNAME=
DB_PASSWORD=
```

---

## Endpoints de la API

### Clima

#### Obtener clima actual

```
GET /clima/{ciudad}
```

#### Obtener clima extendido

```
GET /clima/{ciudad}/extendido
```

---

### Recomendaciones

#### Obtener recomendaciones por ciudad

```
GET /recomendaciones/{ciudad}
```

---

### Favoritos

#### Agregar ciudad a favoritos

```
POST /usuarios/{id}/favoritos
```

#### Eliminar ciudad de favoritos

```
DELETE /usuarios/{id}/favoritos/{favoritoId}
```

#### Listar favoritos

```
GET /usuarios/{id}/favoritos
```

---

### Ciudades

#### Buscar ciudades

```
GET /ciudades?nombre={nombre}
```

---

### Usuarios

#### Crear usuario

```
POST /usuarios
```

#### Obtener usuario

```
GET /usuarios/{id}
```

---

## Base de datos

El sistema utiliza MySQL para almacenar la información de usuarios, ciudades favoritas y un historial de consultas.

### Tablas principales

#### Usuario

Almacena la información de los usuarios del sistema.

* idusuario
* nombre
* correo
* contraseña
* fecha_creado

---

#### Favorito

Guarda las ciudades favoritas de cada usuario.

* idfavorito
* ciudad
* pais
* fecha_agregado
* usuario_idusuario

---

#### Historial

Registra las consultas de clima realizadas por los usuarios.

* idhistorial
* ciudad
* fecha_consulta
* usuario_idusuario

---

### Relaciones

* Un usuario puede tener múltiples ciudades favoritas
* Un usuario puede tener múltiples registros en el historial

Estas relaciones se implementan mediante llaves foráneas.

---

## Ejecución con Docker

### Construir y levantar contenedores

```bash
docker compose up --build
```

---

## Pipeline CI/CD

El proyecto utiliza GitHub Actions para automatizar:

* Compilación del proyecto
* Ejecución de pruebas
* Construcción de la imagen Docker

Cada push al repositorio ejecuta automáticamente el pipeline.

---

## Estructura del proyecto

```
mi-api/
├── .github/workflows/
├── src/
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── README.md
```