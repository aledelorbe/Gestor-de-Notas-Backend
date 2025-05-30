# Gestor de Notas (Backend)

Este proyecto consiste en el desarrollo de un backend para gestionar notas de texto de usuarios. Utiliza el framework **Spring Boot** y está diseñado para proporcionar una API REST que permita manejar información de los usuarios con sus respectivas notas de texto.

Este proyecto implementa **Spring Security** para proporcionar encriptación de contraseñas, autenticación, autorización y roles de usuario en la aplicación backend. De tal manera, que los usuarios no pueden ver ni modificar la información de otros usuarios.

## Tecnologías utilizadas

- **Java**: Lenguaje de programación principal. Para este proyecto en específico se utilizó el `JDK 17`.
- **Spring Boot**: Framework para construir aplicaciones Java. Particularmente en este proyecto se utiliza la versión `3.4.0`.
  - **Spring Security**: Para proporcionar encriptación de contraseñas, autenticación, autorización y roles de usuario.
  - **Hibernate/JPA**: Para la gestión de la base de datos relacional.
  - **Jakarta Validation**: Para la validación de datos de entrada.
  - **Manejo de Excepciones**
  - **Programación orientada a aspectos (POA)**
- **Maven**: Para la gestión de dependencias y construcción del proyecto.
- **MySQL**: Gestor de base de datos relacional para almacenar la información de los clientes, productos y compras.
- **Postman**: Para simular ser un cliente que hace peticiones al servidor y probar los endpoints.

## Características

- **API REST** con rutas organizadas para interactuar con el backend. Operaciones soportadas:
  - **Rutas públicas**:
    - Crear un nuevo usuario.
    - Hacer login en la aplicación.
  - **User**:
    - **Rol de Usuario**:
      - Obtener la información del propio usuario.
      - Actualizar las credenciales del propio usuario.
      - Eliminar la información del propio usuario.
      - **Notas**:
        - Obtener todas las notas del propio usuario.
        - Agregar una nueva nota al propio usuario.
        - Actualizar una nota del propio usuario.
        - Eliminar una nota del propio usuario.
    - **Rol de Administrador**:
      - Obtener todos los usuarios que solo tienen el rol de usuario.
      - Obtener un usuario en específico de todos los que tienen el rol de usuario con base a su id.
      - Inhabilitar/Habilitar el acceso a la aplicación a un usuario específico que tenga solamente el rol de usuario.
    - **Rol de Super Administrador**:
      - Obtener todos los usuarios que tienen el rol de usuario o el rol de administrador.
      - Agregar/Quitar el rol de administrador a un usuario.
      - Inhabilitar/Habilitar el acceso a la aplicación a un usuario específico que tenga el rol de usuario o el rol de administrador.
  - **Importante**:
    - Un usuario no puede modificar la información de otro usuario.
    - Un usuario super administrador puede hacer las mismas acciones que un usuario administrador y un usuario simple, así como, un usuario administrador puede hacer las mismas acciones que un usuario simple.
    - Un usuario con cualquier rol no puede visualizar las notas de otro usuario para proteger su privacidad.
- Integración con MySQL para la manipulación de datos.
- La base de datos SQL cuenta con tres tablas que gestionan la información de los usuarios, sus notas y los roles de usuario.
- **Restricciones en la base de datos**:
  - No se permite que un mismo usuario se registre dos veces en la base de datos.
- **Manejo de excepciones**:
  - Si se rompe la restricción para la entidad `User` al intentar registrar dos veces a un mismo usuario, se dispara la excepción `DataIntegrityViolationException`. Esta excepción se maneja mediante dos clases que, en conjunto, permiten capturarla y generar un mensaje personalizado indicando la razón por la cual la restricción se rompió.
- **Implementación de programación orientada a aspectos (POA)**:
  - La clase `UserAspect` incluye métodos que interceptan, antes de su ejecución, al método encargado de guardar nuevos usuarios en la base de datos y al método encargado de actualizar usuarios en la base de datos. En ambos casos, su objetivo es eliminar los espacios en blanco al inicio y al final del atributo **username**.
- **Validación de datos de entrada**:
  - `User`:
    - No se permite que los atributos **username** y **password** se reciban vacíos o con solo espacios en blanco.
  - `Note`:
    - No se permite que el atributo **content** se reciba vacío o con solo espacios en blanco.
- **Eventos del ciclo de vida para objetos `Entity`**:
  - **`User`**:
    - Antes de guardar un nuevo usuario en la base de datos, por defecto a este se le asignará el rol llamado usuario y tendrá acceso a la aplicación.
  - **`Note`**:
    - Antes de guardar una nueva nota en la base de datos, se captura la fecha del sistema y se inserta en el atributo llamado `createdAt`.
    - Antes de actualizar una nota en la base de datos, se captura la fecha del sistema y se inserta en el atributo llamado `updatedAt`.
- **Implementación de seguridad**:
  - Encriptación de contraseñas.
  - Creación de tokens de autenticación.
  - Validación de tokens de autenticación antes de permitir el acceso a recursos.
  - Creación de distintos roles: usuario, administrador y super administrador.
- Se emplea el patrón de diseño arquitectónico conocido como **MVC**, para separar en diferentes capas el código del proyecto.
- Implementación del patrón de diseño arquitectónico conocido como **Data Transfer Object (DTO)**:
  - `UserDto`: Por temas de seguridad, cuando se envía la información de los usuarios al cliente (Front-End) no se envía la contraseña aunque esta esté encriptada, así como por motivos de privacidad y confidencialidad no se envían las notas que tiene un usuario (a menos que el mismo usuario dueño de las notas las solicite) ni los roles que este usuario tiene en la aplicación.
  Se utiliza esta clase para crear usuarios con información esencial siendo los atributos siguientes: `id`, `username` y `enabled`.
  - `AdminDto`: Se presenta la misma situación que con la clase `UserDto`, sin embargo, se añade un atributo más, siendo el de `admin`. Este atributo se utiliza para que desde la perspectiva de un usuario super administrador pueda identificar qué usuarios son administradores y cuáles no, y con base en esta información realizar ciertas acciones.

## Estructura del proyecto

- `aop/`: Carpeta donde se almacenan las clases que manejan la lógica relacionada con la programación orientada a aspectos.
- `controllers/`: Carpeta donde se almacenan las clases que manejan las solicitudes HTTP y definen los endpoints de la API.
- `services/`: Carpeta donde se almacenan las clases que contienen el código relacionado con la lógica de negocio.
- `repositories/`: Carpeta donde se almacenan las interfaces que extienden de una interfaz que permite el manejo de datos.
- `entities/`: Carpeta donde se almacenan las clases que se mapean con sus respectivas tablas en la base de datos.
- `dto/`: Carpeta donde se almacenan las clases diseñadas específicamente para la transferencia de datos entre diferentes capas de la aplicación.
- `utils/`: Carpeta donde se almacenan las clases las cuales tienen métodos utilitarios que se pueden usar de manera transversal en la aplicación.
- `security/`: Carpeta donde se almacena los archivos referentes a los temas de seguridad como la generación del token de autenticación, la validación del token de autenticación y las reglas de seguridad para los distintos endpoints.


## Futuras mejoras

Implementacion de pruebas unitarias y de integracion.

Limpiar comentarios innecesarios la clase `UserServiceImp`.

Añadir el apartado demo en el readme.

Mover la carpeta dto

Actualizar getNotes como getPets.

Reorganizar el apartador `caracteristicas` del readme, asi como el de los demas proyectos.

----

# Notes Manager (Backend)

This project involves developing a backend to manage users' text notes. It uses the **Spring Boot** framework and is designed to provide a REST API that allows handling user information and their respective text notes.

This project implements **Spring Security** to provide password encryption, authentication, authorization, and user roles in the backend application. Thus, users cannot view or modify other users' information.

## Technologies Used

- **Java**: Main programming language. Specifically, `JDK 17` was used for this project.
- **Spring Boot**: Framework for building Java applications. In this project, version `3.4.0` is used.
  - **Spring Security**: To provide password encryption, authentication, authorization, and user roles.
  - **Hibernate/JPA**: For relational database management.
  - **Jakarta Validation**: For input data validation.
  - **Exception Handling**
  - **Aspect-Oriented Programming (AOP)**
- **Maven**: For dependency management and project building.
- **MySQL**: Relational database management system for storing information about users, notes, and roles.
- **Postman**: Used to simulate a client making requests to the server and test the endpoints.

## Features

- **REST API** with organized routes to interact with the backend. Supported operations:
  - **Public routes**:
    - Create a new user.
    - Log in to the application.
  - **User**:
    - **User Role**:
      - Retrieve the logged-in user's information.
      - Update the logged-in user's credentials.
      - Delete the logged-in user's information.
      - **Notes**:
        - Retrieve all notes of the logged-in user.
        - Add a new note for the logged-in user.
        - Update a note of the logged-in user.
        - Delete a note of the logged-in user.
    - **Admin Role**:
      - Retrieve all users with the user role only.
      - Retrieve a specific user by ID from those with the user role.
      - Enable/disable access to the application for a specific user with the user role only.
    - **Super Admin Role**:
      - Retrieve all users with the user or admin role.
      - Add/remove the admin role from a user.
      - Enable/disable access to the application for a specific user with the user or admin role.
  - **Important**:
    - A user cannot modify another user's information.
    - A super admin user can perform the same actions as an admin and a regular user, and an admin can perform the same actions as a regular user.
    - A user with any role cannot view another user's notes to protect their privacy.
- Integration with MySQL for data manipulation.
- The SQL database has three tables to manage information about users, their notes, and roles.
- **Database Constraints**:
  - A user cannot register twice with the same information.
- **Exception Handling**:
  - If a user tries to register twice, the `DataIntegrityViolationException` is thrown. This exception is handled by two classes that capture it and generate a custom message explaining the reason for the violation.
- **Aspect-Oriented Programming (AOP) Implementation**:
  - The `UserAspect` class includes methods that intercept the save and update methods for users in the database, ensuring that leading and trailing spaces in the **username** attribute are removed.
- **Input Data Validation**:
  - `User`:
    - The **username** and **password** attributes cannot be empty or contain only whitespace.
  - `Note`:
    - The **content** attribute cannot be empty or contain only whitespace.
- **Entity Lifecycle Events**:
  - **`User`**:
    - Before saving a new user, they are assigned the default role of "user" and granted access to the application.
  - **`Note`**:
    - Before saving a new note, the current system date is captured and stored in the `createdAt` attribute.
    - Before updating a note, the current system date is captured and stored in the `updatedAt` attribute.
- **Security Implementation**:
  - Password encryption.
  - Token-based authentication.
  - Token validation before allowing access to resources.
  - Creation of different roles: user, admin, and super admin.
- The project uses the **MVC architectural pattern** to separate the code into different layers.
- **Data Transfer Object (DTO) Pattern**:
  - `UserDto`: For security reasons, when sending user information to the client (Front-End), the password is not included, and for privacy reasons, the user's notes and roles are not sent unless requested by the user themselves. This class is used to create users with essential information, including the following attributes: `id`, `username`, and `enabled`.
  - `AdminDto`: Similar to `UserDto`, but with an additional attribute `admin` to allow the super admin to identify which users are admins and perform specific actions based on that information.

## Project Structure

- `aop/`: Folder containing classes that handle logic related to aspect-oriented programming.
- `controllers/`: Folder containing classes that handle HTTP requests and define the API endpoints.
- `services/`: Folder containing classes with business logic.
- `repositories/`: Folder containing interfaces extending repository interfaces for data management.
- `entities/`: Folder containing classes mapped to their respective database tables.
- `dto/`: Folder containing classes specifically designed for data transfer between different application layers.
- `utils/`: Folder containing utility classes with methods that can be used throughout the application.
- `security/`: Folder containing files related to security, such as token generation, token validation, and endpoint security rules.
