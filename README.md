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
* Base de datos MYSQL
* Contenedores Docker para la ejecución del entorno

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

## Organización del trabajo por ramas

Para el desarrollo del proyecto se utilizó una estrategia de trabajo por ramas en GitHub. Esto permitió que cada integrante implementara una funcionalidad específica sin afectar directamente el trabajo de los demás.

### Ramas creadas

* `develop`
  Rama de integración del proyecto. Aquí se concentran los avances validados antes de pasar a una rama principal.

* `feature/estructura-base`
  Se utilizó para construir la estructura inicial del proyecto, incluyendo la base del backend y la organización principal del repositorio.

* `feature/clima`
  Creada para desarrollar la funcionalidad relacionada con la consulta de clima.

* `feature/auth`
  Destinada a la autenticación de usuarios.

* `feature/recomendaciones`
  Usada para implementar la generación de recomendaciones de vestimenta con base en el clima.

* `feature/ci-cd`
  Utilizada para configurar el pipeline de integración y despliegue continuo con GitHub Actions.

* `feature/usuarios`
  Creada para el desarrollo del módulo de usuarios, incluyendo endpoints para crear y consultar usuarios.

* `feature/favoritos`
  Utilizada para implementar la funcionalidad de favoritos, permitiendo registrar ciudades favoritas asociadas a un usuario.

* `feature/conexion-openweathermap`
  Enfocada en la integración con la API externa OpenWeatherMap.

---
## Endpoints de la API

### Clima

#### Obtener clima actual

```http
GET /clima/{ciudad}
```

#### Obtener clima extendido

```http
GET /clima/{ciudad}/extendido
```

---

### Recomendaciones

#### Obtener recomendaciones por ciudad

```http
GET /recomendaciones/{ciudad}
```

---

### Favoritos

#### Crear favorito

```http
POST /favoritos
```

#### Listar favoritos

```http
GET /favoritos
```

---

### Usuarios

#### Crear usuario

```http
POST /usuarios
```

#### Obtener usuario

```http
GET /usuarios/{id}
```

---

## Base de datos

El sistema utiliza **MySQL** como base de datos.

### ¿Por qué se utilizó MySQL?

Se eligió MySQL porque el proyecto maneja información estructurada y relaciones claras entre entidades. En este caso, los usuarios se relacionan con sus favoritos y con su historial de consultas, lo cual encaja mejor en un modelo relacional.

Esto significa que:

* La información se organiza en tablas
* Cada tabla contiene filas y columnas
* Las tablas pueden relacionarse entre sí mediante claves primarias y foráneas

Este tipo de base de datos fue adecuado para el proyecto porque permite controlar relaciones como:

* un usuario puede tener muchos favoritos
* un usuario puede tener muchos registros en su historial

---

## Modelo entidad-relación

<img width="1205" height="443" alt="image" src="https://github.com/user-attachments/assets/d8c2b2a7-0ef9-4755-82ba-6381a0e4fc05" />


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

## 🐳 Ejecución con Docker

### Levantar la base de datos con imagen personalizada

Para ejecutar la base de datos se utilizó una imagen personalizada subida a Docker Hub.

### Comandos

```
docker pull elisasc/clima-db
docker rm -f clima_db
docker run -d -p 3306:3306 --name clima_db elisasc/clima-db
```

---

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

## Estructura del proyecto

El proyecto sigue una arquitectura por capas de Spring Boot, permitiendo separar responsabilidades y mantener el código organizado.

```
clima-api/
├── src/
│   ├── main/java/com/parde4/climaapi/
│   │   ├── controller/  
│   │   ├── dto/            
│   │   ├── exception/     
│   │   ├── model/          
│   │   ├── repository/   
│   │   ├── service/        
│   │   └── ClimaApiApplication.java 
│   │
│   ├── resources/
│   │   └── application.yaml 
│
│   ├── test/              
│
├── docker-compose.yml     
├── pom.xml          
├── README.md
```
