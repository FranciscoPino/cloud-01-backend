# cloud-01-backend

API Spring Boot minima, lista para validar tokens JWT emitidos por Amazon Cognito.

## Ejecutar en modo local

El perfil `cognito` es el predeterminado. Para ejecutar sin autenticacion durante el desarrollo, activa explicitamente el perfil `local`:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Prueba el endpoint en `http://localhost:8080/api/demo`.

```json
{
  "message": "API Spring Boot operativa",
  "status": "ok"
}
```

## Activar Amazon Cognito

Completa el archivo `.env` con el emisor de tu User Pool y el App client que usara el flujo `client_credentials`:

```properties
COGNITO_ISSUER_URI=https://cognito-idp.<region>.amazonaws.com/<user-pool-id>
COGNITO_CLIENT_ID=<app-client-id>
```

Luego inicia la aplicacion sin cargar variables manualmente en la terminal:

```powershell
mvn spring-boot:run
```

Con ese perfil, `/api/demo` exige un access token JWT valido emitido por Cognito. El token debe incluir `token_use=access` y un `client_id` igual a `COGNITO_CLIENT_ID`:

```http
Authorization: Bearer <access-token-de-cognito>
```

Spring Security obtiene las claves publicas del User Pool desde el `issuer-uri` y valida la firma y las fechas del token.

Una solicitud sin token, con un token expirado o con otro `client_id` responde `401 Unauthorized`.