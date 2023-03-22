# DbUCA Project

Este proyecto contiene la [API](./dbuca) y la [aplicación Android](./MyUCA).

Configure el servidor de Apache según sea necesario. Mueva o enlace el proyecto de la API a su directorio de páginas web (por lo general `htdocs`).

Configure el archivo `local.properties` del proyecto de Android Studio, de tal manera que se añada una propiedad que contenga la URL de la API como en el siguiente ejemplo:

```toml
connection_url=http://192.168.1.17:8080/dbuca
```

> NOTA: Asegúrese que la URL use la IP o nombre del servidor (no localhost), además de incluir el puerto de conexión HTTP.
