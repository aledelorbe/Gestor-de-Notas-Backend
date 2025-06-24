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
  - **Eventos de ciclo de vida**: Manejo de acciones antes o después de realizar alguna de las operaciones CRUD sobre objetos de las clases `entity`.
- **Maven**: Para la gestión de dependencias y construcción del proyecto.
- **MySQL**: Gestor de base de datos relacional para almacenar la información de los clientes, productos y compras.
- **Postman**: Para simular ser un cliente que hace peticiones al servidor y probar los endpoints.

## Características

### EndPoint's

Rutas organizadas para interactuar con el backend. Operaciones soportadas:

- **Rutas públicas**:
  - Crear un nuevo usuario con el rol de usuario.
  - Hacer login en la aplicación.
- **Rol de Super Administrador**:
  - Obtener todos los usuarios que tienen el rol de usuario o el rol de administrador.
  - Agregar/Quitar el rol de administrador a un usuario.
  - Inhabilitar/Habilitar el acceso a la aplicación a un usuario específico que tenga el rol de usuario o el rol de administrador.
- **Rol de Administrador**:
  - Obtener todos los usuarios que tienen el rol de usuario.
  - Obtener un usuario en específico que tiene el rol de usuario con base en su id.
  - Inhabilitar/Habilitar el acceso a la aplicación a un usuario específico que tenga el rol de usuario.
- **Rol de Usuario**:
  - Obtener la información del propio usuario.
  - Actualizar la información del propio usuario.
  - Eliminar la información del propio usuario.
  - **Notas**:
    - Obtener todas las notas del propio usuario.
    - Agregar una nueva nota al propio usuario.
    - Actualizar una nota del propio usuario.
    - Eliminar una nota del propio usuario.
- **Importante**:
  - Un usuario no puede modificar la información de otro usuario.
  - Un usuario superadministrador puede hacer las mismas acciones que un usuario administrador y un usuario simple, así como un usuario administrador puede hacer las mismas acciones que un usuario simple.
  - Un usuario con cualquier rol no puede visualizar las notas de otro usuario para proteger su privacidad.

### Programación Orientada a Aspectos (POA)

- La clase `UserAspect` incluye métodos que interceptan, antes de su ejecución, al método encargado de guardar nuevos usuarios en la base de datos y al método encargado de actualizar usuarios en la base de datos. En ambos casos, su objetivo es eliminar los espacios en blanco al inicio y al final del atributo **username**.

### Gestor de base de datos

- Integración con MySQL para la manipulación de datos.
- La base de datos SQL cuenta con tres tablas que gestionan la información de los usuarios, sus notas y los roles de usuario.
- **Restricciones en la base de datos**:
  - No se permite que un mismo usuario se registre dos veces en la base de datos.

### Validaciones y Excepciones

- **Manejo de excepciones**:
  - Si se rompe la restricción para la entidad `User` al intentar registrar dos veces a un mismo usuario, se dispara la excepción `DataIntegrityViolationException`. Esta excepción se maneja mediante dos clases que, en conjunto, permiten capturarla y generar un mensaje personalizado indicando la razón por la cual la restricción se rompió.
- **Validación de datos de entrada**:
  - `User`:
    - No se permite que los atributos **username** y **password** se reciban vacíos o con solo espacios en blanco.
  - `Note`:
    - No se permite que el atributo **content** se reciba vacío o con solo espacios en blanco.

### Patrones de diseño

- Se emplea el patrón de diseño arquitectónico conocido como **MVC**, para separar en diferentes capas el código del proyecto.
- Implementación del patrón de diseño arquitectónico conocido como **Data Transfer Object (DTO)**:
  - `UserDto`: Por temas de seguridad, cuando se envía la información de los usuarios al cliente (Front-End) no se envía la contraseña aunque esta esté encriptada, así como por motivos de privacidad y confidencialidad no se envían las notas que tiene un usuario (a menos que el mismo usuario dueño de las notas las solicite) ni los roles que este usuario tiene en la aplicación.
  Se utiliza esta clase para crear usuarios con información esencial siendo los atributos siguientes: `id` y `username`.
  - `AdminDto`: Se presenta la misma situación que con la clase `UserDto`, sin embargo, se añade un atributo más, siendo el de `enabled`. Este atributo se utiliza para que desde la perspectiva de un usuario administrador pueda identificar qué usuarios tienen acceso a la aplicación, y con base en esta información habilitarles o deshabilitarles el acceso.
  - `SuperAdminDto`: Se añade un atributo más en comparación con la clase `AdminDto`, siendo el de `admin`. Este atributo se utiliza para que desde la perspectiva de un usuario superadministrador pueda identificar qué usuarios tienen el rol de administrador, y con base en esta información agregarles o quitarles este rol.

### Eventos del ciclo de vida para objetos `Entity`

- **`User`**:
  - Antes de guardar un nuevo usuario en la base de datos se modificará el atributo llamado `enabled` permitiéndole el acceso a la aplicación.
- **`Note`**:
  - Antes de guardar una nueva nota en la base de datos, se captura la fecha del sistema y se inserta en el atributo llamado `createdAt`.
  - Antes de actualizar una nota en la base de datos, se captura la fecha del sistema y se inserta en el atributo llamado `updatedAt`.

### Seguridad

- Integración de **Spring Security** para la gestión de autenticación y autorización.
- Uso de **JWT (JSON Web Tokens)** para la creación y validación de tokens de autenticación.
- Encriptación de contraseñas utilizando **BCrypt**.
- Validación automática de JWT en cada petición para proteger los endpoints.
- Definición y restricción de acceso según distintos roles: **usuario**, **administrador** y **superadministrador**.

## Estructura del proyecto

### Código fuente de la aplicación

- `aop/`: Carpeta donde se almacenan las clases que manejan la lógica relacionada con la programación orientada a aspectos.
- `controllers/`: Carpeta donde se almacenan las clases que manejan las solicitudes HTTP y definen los endpoints de la API.
- `services/`: Carpeta donde se almacenan las clases que contienen el código relacionado con la lógica de negocio.
- `repositories/`: Carpeta donde se almacenan las interfaces que extienden de una interfaz que permite el manejo de datos.
- `entities/`: Carpeta donde se almacenan las clases que se mapean con sus respectivas tablas en la base de datos.
- `dto/`: Carpeta donde se almacenan las clases diseñadas específicamente para la transferencia de datos entre diferentes capas de la aplicación.
- `utils/`: Carpeta donde se almacenan las clases las cuales tienen métodos utilitarios que se pueden usar de manera transversal en la aplicación.
- `security/`: Carpeta donde se almacenan los archivos referentes a los temas de seguridad como la generación del token de autenticación, la validación del token de autenticación y las reglas de seguridad para los distintos endpoints.

### Código de pruebas

- `controllers/`: Contiene las clases de prueba que validan el comportamiento de los métodos en los controladores del código fuente.
- `services/`: Incluye las clases de prueba dedicadas a verificar el correcto funcionamiento de los métodos dentro de los servicios de la aplicación.
- `repositories/`: Incluye las clases de prueba dedicadas a verificar el correcto funcionamiento de las consultas personalizadas dentro de los repositorios de la aplicación.
- `data/`: Almacena clases con datos simulados (mock data) utilizados durante la ejecución de las pruebas.
- `integrations/`: Contiene las clases de prueba que validan el comportamiento completo de los controladores (tests de integración).
- `resources/`: Almacena los datos en formato SQL utilizados como insumos para las pruebas de integración. Además, contiene las propiedades de configuración de una base de datos en memoria H2 para que la aplicación la use durante las pruebas de integración.

## Futuras mejoras

Actualizar todo lo relacionado con AOP.  
Dockerizar.  
Despliegue en AWS.

## Demo

Puedes ver una demo del proyecto en el siguiente enlace: [Gestor de notas](Gestor_de_notas).

----

# Notes Manager (Backend)

This project consists of the development of a backend to manage user text notes. It uses the **Spring Boot** framework and is designed to provide a REST API that allows handling user information along with their corresponding text notes.

This project implements **Spring Security** to provide password encryption, authentication, authorization, and user roles in the backend application. In this way, users cannot view or modify information belonging to other users.

## Technologies Used

- **Java**: Main programming language. Specifically, `JDK 17` was used for this project.
- **Spring Boot**: Framework for building Java applications. In this project, version `3.4.0` is used.
  - **Spring Security**: Provides password encryption, authentication, authorization, and user roles.
  - **Hibernate/JPA**: For relational database management.
  - **Jakarta Validation**: For input data validation.
  - **Exception Handling**
  - **Aspect-Oriented Programming (AOP)**
  - **Lifecycle Events**: Handles actions before or after performing CRUD operations on `entity` class objects.
- **Maven**: For dependency management and project building.
- **MySQL**: Relational database management system used to store client, product, and purchase information.
- **Postman**: Used to simulate a client making requests to the server and to test the API endpoints.

## Features

### Endpoints

Organized routes to interact with the backend. Supported operations:

- **Public Routes**:
  - Create a new user with the user role.
  - Log in to the application.
- **Super Administrator Role**:
  - Retrieve all users who have the user or administrator role.
  - Add/Remove the administrator role to/from a user.
  - Enable/Disable access to the application for a specific user with the user or administrator role.
- **Administrator Role**:
  - Retrieve all users with the user role.
  - Retrieve a specific user with the user role by ID.
  - Enable/Disable access to the application for a specific user with the user role.
- **User Role**:
  - Retrieve the user's own information.
  - Update the user's own information.
  - Delete the user's own information.
  - **Notes**:
    - Retrieve all notes belonging to the user.
    - Add a new note for the user.
    - Update a note belonging to the user.
    - Delete a note belonging to the user.
- **Important**:
  - A user cannot modify another user's information.
  - A super administrator can perform the same actions as an administrator and a regular user. Similarly, an administrator can perform the same actions as a regular user.
  - A user with any role cannot view another user's notes to protect their privacy.

### Aspect-Oriented Programming (AOP)

- The `UserAspect` class includes methods that intercept, before execution, the method responsible for saving new users to the database and the method responsible for updating users in the database. In both cases, its goal is to trim whitespace at the beginning and end of the **username** attribute.

### Database Management

- MySQL integration for data manipulation.
- The SQL database contains three tables to manage user information, notes, and user roles.
- **Database Constraints**:
  - A user cannot register more than once in the database.

### Validations and Exceptions

- **Exception Handling**:
  - If the constraint for the `User` entity is violated by attempting to register the same user twice, the `DataIntegrityViolationException` is thrown. This exception is handled by two classes working together to capture it and return a custom message indicating the reason for the violation.
- **Input Data Validation**:
  - `User`:
    - The **username** and **password** attributes cannot be empty or contain only whitespace.
  - `Note`:
    - The **content** attribute cannot be empty or contain only whitespace.

### Design Patterns

- The architectural design pattern **MVC** is used to separate project code into different layers.
- Implementation of the architectural design pattern known as **Data Transfer Object (DTO)**:
  - `UserDto`: For security reasons, when user information is sent to the client (Frontend), the password is not included even if encrypted. For privacy and confidentiality, notes and roles are also not included (unless the user explicitly requests their notes).
    This class is used to create users with essential information: `id` and `username`.
  - `AdminDto`: Similar to `UserDto`, but adds an extra attribute: `enabled`. This is used by an administrator to identify which users have access to the application and enable/disable access accordingly.
  - `SuperAdminDto`: Adds one more attribute compared to `AdminDto`: `admin`. This is used by a super administrator to identify which users have the administrator role and add or remove this role accordingly.

### Entity Lifecycle Events

- **`User`**:
  - Before saving a new user to the database, the `enabled` attribute is modified to grant access to the application.
- **`Note`**:
  - Before saving a new note to the database, the system date is captured and inserted into the `createdAt` attribute.
  - Before updating a note in the database, the system date is captured and inserted into the `updatedAt` attribute.

### Security

- Integration of **Spring Security** for authentication and authorization management.
- Use of **JWT (JSON Web Tokens)** for the creation and validation of authentication tokens.
- Password encryption using **BCrypt**.
- Automatic JWT validation in every request to protect endpoints.
- Access control based on different roles: **user**, **administrator**, and **super administrator**.

## Project Structure

### Application Source Code

- `aop/`: Contains classes related to aspect-oriented programming logic.
- `controllers/`: Contains classes that handle HTTP requests and define the API endpoints.
- `services/`: Contains classes with the business logic.
- `repositories/`: Contains interfaces that extend repository interfaces for data access.
- `entities/`: Contains classes mapped to the corresponding database tables.
- `dto/`: Contains classes specifically designed for data transfer between application layers.
- `utils/`: Contains utility classes with reusable helper methods across the application.
- `security/`: Contains security-related files such as token generation, token validation, and endpoint security rules.

### Test Code

- `controllers/`: Contains test classes that validate the behavior of controller methods.
- `services/`: Includes test classes that verify the correct functioning of service methods.
- `repositories/`: Includes test classes that verify the correct execution of custom queries in the repositories.
- `data/`: Stores mock data classes used during test execution.
- `integrations/`: Contains integration test classes that validate complete controller behavior.
- `resources/`: Stores SQL data used as input for integration tests. Also contains configuration properties for an in-memory H2 database used during integration testing.

## Future Improvements

Update everything related to AOP.  
Dockerize the application.  
Deploy on AWS.

## Demo

You can see a demo of the project at the following link: [Notes Manager](Gestor_de_notas).
