# GameTracker - Aplicación para tracking de videojuegos
Aplicación móvil desarrollada para Android que permite a los usuarios llevar un seguimiento de los videojuegos que juegan, desean jugar o han abandonado.

---

## Descripción
Aplicación Android inspirada en letterboxd para que los usuarios puedan hacer un seguimiento de su biblioteca de videojuegos.

En este proyecto se implementa: 
- Programación orientada a objetos 
- Gestión de usuarios con Firebase Auth
- CRUD en base de datos con Firebase
- Conexión con API externa
- Personalización de pantallas con Jetpack Compose
- Arquitectura MVVM
- Buenas prácticas (modularidad separada, encapsulación, responsabiliad única..)

---

## Características principales
- Roles de usuario
  - Rol de administrador: Posibilidad de generar avisos o suspender cuentas de forma indefinida o temporal.
  - Rol de usuario corriente
  
- Registro y autenticación.
  - Posibilidad de registrarse con email y contraseña o con cuenta de Google.

 - Pantalla principal con timeline
   - Visualización de actividad reciente de usuarios a los que sigues

- Información concreta de cada videojuego
  - Sipnopsis, portada, notas, género, año de lanzamiento e imágenes.

- Hacer review y añadir juegos a tu lista
  - Añadir estado (completado, deseado, en curso o abandonado)
  - Añadir nota sobre 100
  - Añadir cantidad de horas jugadas
  - Añadir opinión más extendida

- Personalización de cuenta
  - Posibilidad de cambiar nombre de usuario, biografia y stats

- Ordenar lista y personalizarla
  - Filtrar por diferentes métodos (nota, momento en el que se añadió)

- Pantalla para explorar videojuegos
  - Mostrar títulos por género, año o recomendaciones personales en base a los juegos añadidos a la lista.
  - Barra de búsqueda
 
  ---

## Tecnologías utilizadas
- Kotlin
- Jetpack Compose
- Firebase Auth
- Firebase Crashlytics
- Cloud Firestore
- API IGDB

 ---

## Instalación y ejecución

### Requisitos
- Sistema operativo: Android 8.0 (API 26) o superior
- Conexión a internet: necesaria para iniciar sesión, sincronización de datos y acceder a contenido remoto
- Cuenta de usuario: con Cuenta de Google o email y contraseña

### Clonar el repositorio
```bash
git clone https://github.com/RubenAC1999/proyectoFP.git
cd proyectoFP
```



