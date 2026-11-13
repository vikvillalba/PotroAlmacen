# Potro Almacén

Sistema para digitalizar el control de los almacenes de Servicios Generales y Mantenimiento de ITSON
(Obregón Náinari, Obregón Centro, Navojoa Sur, Guaymas y Empalme).

Monorepo con dos proyectos:

| Carpeta     | Contenido                                                                 |
|-------------|---------------------------------------------------------------------------|
| `backend/`  | API REST en Spring Boot: monolito modular con arquitectura hexagonal.     |
| `frontend/` | Cliente web (lo agrega el equipo de frontend).                            |

## Backend

### Tecnologías

Java 21, Spring Boot 4.1 (Web MVC, Validation, Data JPA, Actuator), MySQL 8.4, Flyway (con `flyway-mysql`),
Spring Modulith, springdoc-openapi, MapStruct, Testcontainers y Spotless. Build con Maven (wrapper incluido).

### Estructura de módulos

Es un solo proyecto Maven: cada hexágono es un paquete dentro de `mx.itson.potroalmacen`, no un módulo de Maven.
Spring Modulith verifica en el CI que ningún módulo use clases internas de otro.

| Paquete      | Módulo           | Tipo              |
|--------------|------------------|-------------------|
| `materiales` | Materiales       | Hexágono completo |
| `salidas`    | Salidas          | Hexágono completo |
| `areas`      | Áreas y espacios | Hexágono ligero   |
| `personal`   | Personal         | Hexágono ligero   |
| `usuarios`   | Usuarios         | Hexágono ligero   |
| `auditoria`  | Auditoría        | Hexágono mínimo   |
| `reportes`   | Reportes         | Hexágono mínimo   |
| `seguridad`  | Seguridad        | Sin hexágono      |
| `compartido` | Infraestructura web compartida (errores, Swagger) | Módulo abierto |

Estructura interna de cada hexágono:

```
<modulo>/
├── domain/                        Modelo y reglas de negocio (sin Spring ni JPA)
├── application/
│   ├── port/in/                   Casos de uso y comandos (API pública del módulo)
│   ├── port/out/                  Repositorios y servicios externos
│   └── service/                   Implementación de los casos de uso
└── adapter/
    ├── in/web/                    Controladores y DTOs (/api/<modulo>)
    └── out/persistence/           Entidades JPA, repositorios y mappers
```

Los módulos se comunican solo por `application/port/in` o por eventos de dominio.

### Requisitos

- JDK 21 (no hace falta instalar Maven: `mvnw` descarga la versión correcta)
- Docker (Docker Desktop en Windows/macOS) con Docker Compose

### Levantar el entorno local

Todos los comandos se ejecutan dentro de `backend/`.

1. **Variables de entorno.** Copia el ejemplo y cambia las contraseñas:

   ```bash
   cp .env.example .env
   ```

   `.env` está en el `.gitignore`; nunca se sube al repositorio.

2. **Base de datos.** Levanta MySQL 8.4 (utf8mb4, base `potro_almacen` y usuario de la aplicación):

   ```bash
   docker compose up -d
   ```

   Desde el IDE puedes conectarte con `localhost:3306`, base `potro_almacen` y el usuario y contraseña de tu `.env`.
   Para detenerla: `docker compose down` (agrega `-v` solo si quieres borrar los datos).

3. **Backend.** Arranca la aplicación con el perfil `dev`:

   ```bash
   ./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
   ```

   Al arrancar, Flyway aplica las migraciones y crea la tabla `flyway_schema_history`.
   Si lo ejecutas desde el IDE, activa el perfil `dev` y usa `backend/` como directorio de trabajo
   (ahí se lee el archivo `.env`).

4. **Comprobar.**
   - Salud: <http://localhost:8080/actuator/health>
   - Swagger (solo perfil `dev`): <http://localhost:8080/swagger-ui/index.html>

### Perfiles

| Perfil | Uso | Base de datos |
|--------|-----|---------------|
| `dev`  | Desarrollo local. Swagger encendido y actor fijo (almacenista de Náinari) en lugar de autenticación. | MySQL de `docker compose`, credenciales de `.env` |
| `test` | Pruebas automatizadas. | MySQL en Testcontainers |

Sin perfil, la aplicación no tiene datasource y no arranca: así no se usa por accidente una configuración de
desarrollo.

### Pruebas y formato

```bash
./mvnw test             # todas las pruebas (requiere Docker para Testcontainers)
./mvnw spotless:apply    # aplica el formato de código
./mvnw verify            # compila, ejecuta las pruebas y verifica formato (lo mismo que el CI)
```

### Migraciones de esquema (Flyway)

- Los scripts van en `backend/src/main/resources/db/migration/`.
- Nombre con fecha y hora: `V202611040900__catalogos_materiales.sql`.
- Una persona por migración. **Nunca se edita una migración ya aplicada**: los errores se corrigen con una nueva.
- Hibernate solo valida el esquema (`ddl-auto: validate`); nunca lo crea.

### Formato de errores de la API

Todos los módulos responden los errores con el mismo JSON:

```json
{
  "codigo": "VALIDACION",
  "mensaje": "La solicitud contiene campos inválidos.",
  "campos": [{ "campo": "nombre", "mensaje": "no debe estar vacío" }]
}
```

| HTTP | Cuándo                                                                  |
|------|-------------------------------------------------------------------------|
| 400  | Campos inválidos (`VALIDACION`) o petición mal formada                  |
| 403  | El dominio lanza `PermisoDenegadoException`                             |
| 404  | El dominio lanza `NoEncontradoException` o la ruta no existe            |
| 409  | El dominio lanza `DuplicadoException`                                   |
| 500  | Error no previsto (`ERROR_INTERNO`), sin detalles internos              |

Cada módulo extiende las excepciones de `compartido.excepcion` con su propio código, por ejemplo
`new DuplicadoException("MATERIAL_DUPLICADO", "Ya existe un material con el código PIN-001")`.

### Integración continua

GitHub Actions (`.github/workflows/ci.yml`) ejecuta `./mvnw verify` en cada pull request a `main`.

## Proxy de desarrollo (nginx)

Para que el frontend llame a la API con rutas relativas (`fetch('/api/materiales')`) sin configurar CORS,
nginx sirve todo desde un mismo origen, <http://localhost:8000>:

| Ruta     | Destino                                         |
|----------|-------------------------------------------------|
| `/api/*` | Backend Spring Boot en `localhost:8080`         |
| `/*`     | Vite (`npm run dev`) en `localhost:5173`, con recarga en caliente |

1. Levanta el backend (sección [Levantar el entorno local](#levantar-el-entorno-local)).
2. En `frontend/`, arranca Vite: `npm run dev`.
3. En `frontend/`, levanta el proxy: `docker compose up -d`.
4. Abre <http://localhost:8000>.

La configuración está en `frontend/nginx/dev.conf`. Para usar otro puerto: `PROXY_PORT=9000 docker compose up -d`.
Para detenerlo: `docker compose down`.
