# Gestor de Notas (Backend)

Este proyecto consiste en el desarrollo de un backend para gestionar notas de texto de usuarios. Utiliza el framework Spring Boot y está diseñado para proporcionar una API REST que permita manejar informacion de los usuarios con sus respectivas notas de texto.

Este proyecto implementa spring security para proporcionar autenticación, autorización y roles de usaurio en la aplicacion backend. De tal manera, que los usuarios no puede ver ni modificar la informacion de otros usuarios.

## Tecnologías utilizadas

- **Java**: Lenguaje de programación principal. Para este proyecto en específico se utilizó el `JDK 17`.
- **Spring Boot**: Framework para construir aplicaciones Java. Particularmente en este proyecto se utiliza la versión `3.4.0`.
  - **Spring Security**: Para proporcionar autenticación, autorización y roles de usaurio.
  - **Hibernate/JPA**: Para la gestión de la base de datos relacional.
  - **Jakarta Validation**: Para la validación de datos de entrada.
  - **Manejo de Excepciones**
  - **Programación orientada a aspectos (POA)**
- **Maven**: Para la gestión de dependencias y construcción del proyecto.
- **MySQL**: Gestor de base de datos relacional para almacenar la información de los clientes, productos y compras.
- **Postman**: Para simular ser un cliente que hace peticiones al servidor y probar los endpoints.

## Características

- **API REST** con rutas organizadas para interactuar con el backend. Operaciones soportadas:
  - **Rutas publicas**:
    - Crear un nuevo usuario.
    - Hacer login en la aplicacion.
  - **User**:
    - **Rol de Usuario**:
      - Obtener la información del propio usuario.
      - Actualizar las credenciales del propio usuario.
      - Eliminar la informacion del propio usuario.
      - **Notas**:
        - Obtener todas las notas del propio usuario.
        - Agregar una nueva nota al propio usuario.
        - Actualizar una nota del propio usuario.
        - Eliminar una nota del propio usuario.
    - **Rol de Administrador**:
      - Obtener todos los usuarios que solo tienen el rol de usuario.
      - Obtener un usuario en especifico de todos los que tienen el rol de usuario con base a su id.
      - Inhabilitar/Habilitar el acceso a la aplicacion a un usuario especifico que tenga solamente el rol de usuario.
    - **Rol de Super Administrador**:
      - Obtener todos los usuarios que tienen el rol de usuario o el rol de administrador.
      - Agregar/Quitar el rol de administrador a un usuario.
      - Inhabilitar/Habilitar el acceso a la aplicacion a un usuario especifico que tenga el rol de usuario o el rol de administrador.
  - **Importante**:
    - Un usuario no puede modificar la infromacion de otro usuario.
    - Un usuario super administrador puede hacer las mismas acciones que un usuario administrador y un usuario simple, asi como, un usuario administrador puede hacer las mismas acciones que un usuario simple.
    - Un usuario con cualquier rol no puede visualizar las notas de otro usuario para proteger su privacidad.
- Integración con MySQL para la manipulación de datos.
- La base de datos SQL cuenta con tres tablas que gestionan la información de los usuarios, sus notas y los roles de usuario.
- **Restricciones en la base de datos**:
  - No se permite que un mismo usuario se registre dos veces en la base de datos.
- **Manejo de excepciones**:
  - Si se rompe la restricción para la entidad `User` al intentar registrar dos veces a un mismo usuario, se dispara la excepción `DataIntegrityViolationException`. Esta excepción se maneja mediante dos clases que, en conjunto, permiten capturarla y generar un mensaje personalizado indicando la razón por la cual la restricción se rompió.
- **Implementación de programación orientada a aspectos (POA)**:
  - La clase `UserAspect` incluye métodos que interceptan, antes de su ejecución, al método encargado de guardar nuevos usuarios en la base de datos y al metodo encargado de actualizar usuarios en la base de datos. En ambos casos, su objetivo es eliminar los espacios en blanco al inicio y al final del atributo **username**.
- **Validación de datos de entrada**:
  - `User`:
    - No se permite que los atributos **username** y **password** se reciban vacíos o con solo espacios en blanco.
  - `Note`:
    - No se permite que el atributo **content** se reciba vacío o con solo espacios en blanco.
- **Eventos del ciclo de vida para objetos `Entity`**:
  - **`User`**:
    - Antes de guardar un nuevo usuario en la base de datos, por defecto a este se le asignara el rol llamado usuario y tendra acceso a la aplicacion.
  - **`Note`**:
    - Antes de guardar una nueva nota en la base de datos, se captura la fecha del sistema y se inserta en el atributo llamado `createdAt`.
    - Antes de actualizar una nota en la base de datos, se captura la fecha del sistema y se inserta en el atributo llamado `updatedAt`.
- **Implementacion de seguridad**:
  - Creacion de tokens de autenticacion.
  - Validacion de tokens de autenticacion antes de permitir el acceso a recursos.
  - Creacion de distintos roles: usuario, administrador y super administrador.
- Se emplea el patrón de diseño arquitectónico conocido como **MVC**, para separar en diferentes capas el código del proyecto.

## Estructura del proyecto

- `aop/`: Carpeta donde se almacenan las clases que manejan la lógica relacionada con la programación orientada a aspectos.
- `controllers/`: Carpeta donde se almacenan las clases que manejan las solicitudes HTTP y definen los endpoints de la API.
- `services/`: Carpeta donde se almacenan las clases que contienen el código relacionado con la lógica de negocio.
- `repositories/`: Carpeta donde se almacenan las interfaces que extienden de una interfaz que permite el manejo de datos.
- `entities/`: Carpeta donde se almacenan las clases que se mapean con sus respectivas tablas en la base de datos.
- `dto/`: Carpeta donde se almacenan las clases diseñadas específicamente para la transferencia de datos entre diferentes capas de la aplicación.
- `utils/`: Carpeta donde se almacenan las clases las cuales tienen métodos utilitarios que se pueden usar de manera transversal en la aplicación.
- `security/`: Carpeta donde se alamcena los archivos referentes a los temas de seguridad como la generacion del token de autenticacion, la validacion del token de autenticacion y las reglas de seguridad para los distintos endpoints.