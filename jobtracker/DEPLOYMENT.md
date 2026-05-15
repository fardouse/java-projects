# Private Deployment Notes

This app can run locally with H2, or online with PostgreSQL.

## Required Production Environment Variables

Set these on your hosting service:

```text
APP_BASE_URL=https://your-deployed-app-url
GOOGLE_CLIENT_ID=your-google-web-client-id
GOOGLE_CLIENT_SECRET=your-google-web-client-secret
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/database
SPRING_DATASOURCE_USERNAME=postgres-user
SPRING_DATASOURCE_PASSWORD=postgres-password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
```

Most hosts also set this automatically:

```text
PORT=8080
```

## Google OAuth Setup

Create a Google OAuth client with application type:

```text
Web application
```

Add these authorized redirect URIs:

```text
http://localhost:8080/oauth2callback
https://your-deployed-app-url/oauth2callback
```

Keep your Gmail address listed as a test user while the app is private.

## Important

Do not deploy:

```text
src/main/resources/credentials.json
tokens/
```

Use `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` in production instead.
