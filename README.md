# MiniMarket Plus

Backend de microservicios para la gestión de un minimarket: productos, categorías, inventario, carrito de compras, ventas y usuarios con autenticación JWT y control de acceso por roles (`GERENTE`, `EMPLEADO`, `CLIENTE`).

## Requisitos

- **Java 17** (el proyecto no corre con versiones más nuevas, como Java 24).
- No necesitas instalar Maven: el proyecto incluye el wrapper (`mvnw.cmd` / `mvnw`).

## Cómo levantar el proyecto

1. Clona el repositorio y entra a la carpeta del proyecto.
2. Si tienes más de una versión de Java instalada, apunta `JAVA_HOME` a la 17 antes de compilar. En PowerShell (VS Code):

   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.19"
   ```

3. Ejecuta la aplicación:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. El servidor queda disponible en `http://localhost:8080`. Usa una base de datos H2 en memoria, por lo que no requiere configuración adicional.

## Swagger UI

Con la aplicación corriendo, abre:

```
http://localhost:8080/swagger-ui.html
```

Ahí vas a encontrar todos los endpoints agrupados por entidad (Productos, Categorías, Inventario, Carrito, Detalle de Ventas, Ventas, Usuarios, Autenticación), cada uno con su descripción, parámetros de entrada y ejemplos de respuesta.

La mayoría de los endpoints requieren autenticación. Para probarlos desde Swagger:

1. Crea un usuario con `POST /api/auth/register`.
2. Inicia sesión con `POST /api/auth/login` y copia el `token` que devuelve.
3. Haz clic en **Authorize** (arriba a la derecha) y pega el token como `Bearer {token}`.

## Exportar la especificación OpenAPI

El JSON completo de la API (útil para importar en Postman u otras herramientas) está disponible en:

```
http://localhost:8080/v3/api-docs
```

Al importarlo en Postman como **OpenAPI 3.1 Specification**, se genera automáticamente una colección con todos los endpoints organizados por tags.

## Roles del sistema

| Rol | Descripción |
|---|---|
| `GERENTE` | Administra productos, categorías e inventario. |
| `EMPLEADO` | Gestiona ventas e inventario. |
| `CLIENTE` | Gestiona su propio carrito de compras. |
