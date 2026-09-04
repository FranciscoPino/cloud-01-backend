# cloud-01-backend

API Spring Boot minima, lista para validar tokens JWT emitidos por Amazon Cognito.

## Instalar y ejecutar en otro equipo

### Requisitos

- Java 21 o superior.
- Apache Maven 3.9 o superior.
- Git, para clonar el repositorio.

Comprueba las herramientas instaladas:

```powershell
java -version
mvn -version
git --version
```

### Inicio rapido en localhost

1. Clona el repositorio y entra a la carpeta del proyecto:

  ```powershell
  git clone https://github.com/FranciscoPino/cloud-01-backend.git
  Set-Location cloud-01-backend
  ```

2. Compila y ejecuta las pruebas:

  ```powershell
  mvn clean test
  ```

3. Para ejecutar la API sin autenticacion durante el desarrollo, inicia el perfil `local`:

  ```powershell
  mvn spring-boot:run "-Dspring-boot.run.profiles=local"
  ```

4. Prueba el endpoint desde otra terminal:

  ```powershell
  Invoke-RestMethod "http://localhost:8080/api/demo"
  ```

  Respuesta esperada:

  ```json
  {
    "message": "API Spring Boot operativa",
    "status": "ok"
  }
  ```

5. Deten la aplicacion con `Ctrl + C`.

### Inicio protegido con Cognito

El perfil predeterminado es `cognito`. Antes de iniciarlo, crea un archivo `.env` en la raiz del proyecto con valores de tu User Pool y App Client:

```properties
COGNITO_ISSUER_URI=https://cognito-idp.<region>.amazonaws.com/<user-pool-id>
COGNITO_CLIENT_ID=<cognito-public-app-client-id>
```

Luego ejecuta:

```powershell
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`. Sin un token Cognito valido, `/api/demo` responde `401 Unauthorized`.

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

Completa el archivo `.env` con el emisor de tu User Pool y el App client que usara el flujo federado:

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

Una solicitud sin token, con un token expirado o con otro `client_id` responde `401 Unauthorized` e incluye el motivo del rechazo en el campo `reason`:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Autenticacion rechazada",
  "reason": "The token client_id is not authorized"
}
```

## Flujo de la aplicacion

1. Al ejecutar `mvn spring-boot:run`, Spring Boot carga las propiedades desde `.env` mediante `application.yml`.
2. El perfil predeterminado es `cognito`, por lo que la API inicia protegida. El perfil `local` debe activarse explicitamente y permite probar el endpoint sin autenticacion.
3. El cliente inicia Authorization Code con PKCE en Cognito Hosted UI. Cognito delega el login en Entra ID y emite un access token propio.
4. El cliente envia el token a la API mediante el encabezado `Authorization`:

  ```http
  Authorization: Bearer <access-token-de-cognito>
  ```

5. Spring Security valida la firma, el emisor (`issuer`), la vigencia del JWT y los claims requeridos:
  - `token_use` debe ser `access`.
  - `client_id` debe coincidir con `COGNITO_CLIENT_ID`.
6. Si el token es valido, la solicitud llega a `GET /api/demo` y la API responde `200 OK` con el JSON dummy.
7. Si el token falta o no es valido, la solicitud se rechaza antes de llegar al controlador con `401 Unauthorized` y un mensaje JSON.

```mermaid
sequenceDiagram
  participant Cliente
  participant Cognito as Amazon Cognito
  participant API as Spring Boot API

  Cliente->>Cognito: Solicita token con client_credentials
  Cognito-->>Cliente: Devuelve access token JWT
  Cliente->>API: GET /api/demo con Bearer token
  API->>API: Valida firma, issuer y expiracion
  API->>API: Valida token_use=access y client_id

  alt Token valido
    API-->>Cliente: 200 OK + JSON dummy
  else Token ausente o invalido
    API-->>Cliente: 401 Unauthorized + JSON de error
  end
```

El backend valida el access token de Cognito, pero no necesita conocer el `client_secret`. Para una SPA local, el App Client debe ser publico y usar PKCE; nunca se debe incluir un secreto en el codigo Angular.