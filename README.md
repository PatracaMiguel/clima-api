# 🌦️ API de Clima y Recomendación de Vestimenta

## 📌 Descripción del proyecto

Esta API REST tiene como objetivo ayudar a los usuarios a tomar mejores decisiones sobre su vestimenta diaria, considerando las condiciones climáticas de una ciudad.

A diferencia de otras aplicaciones meteorológicas, esta API no solo proporciona datos del clima, sino que también genera recomendaciones prácticas de ropa y accesorios adecuados según la temperatura y condiciones climáticas.

Además, permite a los usuarios guardar sus ciudades favoritas para consultas rápidas.

---

## 🏗️ Arquitectura del sistema

El sistema sigue una arquitectura basada en servicios REST:

* Cliente (Postman / Frontend)
* API REST (Spring Boot)
* API externa (OpenWeatherMap)
* Base de datos (MySQL)
* Contenedores Docker

---

## ⚙️ Tecnologías utilizadas

* Java 17
* Spring Boot
* Maven
* MySQL
* Docker
* Docker Compose
* Git & GitHub
* GitHub Actions (CI/CD)
* Postman
* OpenWeatherMap API

---

## 🔐 Variables de entorno

El proyecto utiliza variables de entorno para proteger información sensible.

Ejemplo de archivo `.env.example`:

```env
OPENWEATHER_API_KEY=
DB_URL=
DB_USERNAME=
DB_PASSWORD=
```

⚠️ **Nota:** Nunca subir el archivo `.env` real al repositorio.

---

## 🚀 Endpoints de la API

### 🌦️ Clima

#### Obtener clima actual

```
GET /clima/{ciudad}
```

#### Obtener clima extendido

```
GET /clima/{ciudad}/extendido
```

---

### 👕 Recomendaciones

#### Obtener recomendaciones por ciudad

```
GET /recomendaciones/{ciudad}
```

---

### ⭐ Favoritos

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

### 🔍 Ciudades

#### Buscar ciudades

```
GET /ciudades?nombre={nombre}
```

---

### 👤 Usuarios

#### Crear usuario

```
POST /usuarios
```

#### Obtener usuario

```
GET /usuarios/{id}
```

---

## 🧪 Pruebas

Los endpoints pueden ser probados utilizando Postman.

Se recomienda crear una colección con ejemplos de cada endpoint para facilitar la validación.

---

## 🐳 Ejecución con Docker

### Construir y levantar contenedores

```bash
docker compose up --build
```

---

## 🔄 Pipeline CI/CD

El proyecto utiliza GitHub Actions para automatizar:

* Compilación del proyecto
* Ejecución de pruebas
* Construcción de imagen Docker

Cada push al repositorio ejecuta automáticamente el pipeline.

---

## 📁 Estructura del proyecto

```
mi-api/
├── .github/workflows/
├── src/
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── README.md
└── APRENDIZAJES.md
```

---

## 👥 Equipo

* Líder técnico
* Desarrollador backend
* QA / DevOps
* Documentación

---

## 📌 Estado del proyecto

🚧 En desarrollo

---

## 📚 Aprendizajes esperados

* Desarrollo de APIs REST
* Integración con APIs externas
* Contenedorización con Docker
* Automatización con CI/CD
* Trabajo colaborativo con Git

---
