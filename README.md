# CrediYa · Microservicio de Autenticación

Microservicio encargado del registro y la gestión de usuarios de la plataforma **CrediYa**, construido con **Clean Architecture** a partir del [Scaffold de Bancolombia](https://bancolombia.github.io/scaffold-clean-architecture/).

## Historias de usuario implementadas
| HU | Descripción | Endpoint |
|----|-------------|----------|
| HU-01 | Registrar usuarios en el sistema | `POST /api/v1/usuarios` |

## Arquitectura
```
applications/app-service          → Arranque de la app y configuración de beans
domain/model                      → Entidades (User, Role), puertos (gateways) y excepciones de negocio
domain/usecase                    → Reglas de negocio (RegisterUserUseCase)
infrastructure/driven-adapters
  ├── jpa-repository              → Persistencia con JPA + MySQL, migraciones con Flyway
  └── password-encoder            → Cifrado de contraseñas con BCrypt
infrastructure/entry-points
  └── api-rest                    → API REST (Spring MVC), Swagger y manejo global de errores
```
El dominio no depende de ningún framework; la infraestructura implementa los puertos que este define. Las reglas de dependencia se validan en cada build con `validateStructure` y ArchUnit.

## Stack
Java 25 · Spring Boot 4.2 · Gradle 9 · MySQL 8.4 · Flyway · Lombok · springdoc-openapi · JUnit 5 · Mockito · Pitest

## Requisitos
- Java 25
- Docker

## Cómo ejecutarlo
1. Levantar MySQL:
   ```bash
   docker run -d --name crediya-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=crediya_auth -e MYSQL_USER=crediya -e MYSQL_PASSWORD=crediya -p 3307:3306 -v crediya-mysql-data:/var/lib/mysql mysql:8.4
   ```
2. Ejecutar la aplicación (Flyway crea las tablas y precarga los roles):
   ```bash
   ./gradlew bootRun
   ```
3. Abrir Swagger: http://localhost:8080/v3/swagger-ui.html

### Variables de entorno
| Variable | Valor por defecto |
|----------|-------------------|
| `DB_URL` | `jdbc:mysql://localhost:3307/crediya_auth` |
| `DB_USERNAME` | `crediya` |
| `DB_PASSWORD` | `crediya` |

## Ejemplo de uso
```bash
curl -X POST http://localhost:8080/api/v1/usuarios \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ana","lastName":"Gómez","email":"ana@mail.com","identityDocument":"1020304050","baseSalary":2500000,"password":"Secreta123*"}'
```
Si no se envía `roleId`, el usuario queda con el rol `CLIENT`.

| Código | Caso |
|--------|------|
| 201 | Usuario registrado |
| 400 | Campo obligatorio ausente, formato inválido o salario fuera de rango (0 – 15.000.000) |
| 409 | Correo o documento ya registrado |

## Pruebas
```bash
./gradlew build                # compila, ejecuta tests, Pitest y validación de arquitectura
./gradlew jacocoMergedReport   # reporte de cobertura unificado
```

## Flujo de trabajo (GitFlow)
- `main` → versiones liberadas (con tag)
- `develop` → integración
- `feature/HU-XX-descripcion` → una rama por historia de usuario
- `release/X.Y.Z` → preparación de cada versión