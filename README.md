# XtrackMachine – Sistema de Control de Aspiradoras para Fresadoras

## Introducción

**XtrackMachine** es un sistema diseñado para la **gestión y control de aspiradoras autónomas** utilizadas en fresadoras.  
Su objetivo principal es **facilitar la administración eficiente de estos dispositivos** mediante una interfaz que permite realizar operaciones CRUD (crear, leer, actualizar, eliminar) sobre usuarios y aspiradoras.

El sistema está desarrollado en **Java** con **MySQL** como base de datos y se comunica con las aspiradoras a través de **sockets TCP/IP**, permitiendo **monitoreo en tiempo real** y control seguro de los dispositivos.

---

## Requisitos del Sistema

### 1. Requisitos Funcionales

#### 1.1 Gestión de usuarios
- Crear nuevos usuarios con nombre de usuario y contraseña.
- Consultar lista de usuarios registrados.
- Modificar información de usuarios existentes.
- Eliminar usuarios de manera segura con confirmación.
- Validar credenciales al iniciar sesión.
- Controlar intentos de acceso fallidos con mensajes de error.

#### 1.2 Gestión de aspiradoras
- Registrar nuevas aspiradoras: IP, ID, estado y máquina asignada.
- Asociar aspiradoras a máquinas fresadoras.
- Consultar información de todas las aspiradoras con indicadores visuales.
- Modificar datos de aspiradoras (estado, IP, máquina).
- Eliminar aspiradoras.
- Mostrar el estado según valores: Sucio, Limpio, Limpiando, En uso, Error, Error de conexión, Desconocido.

---

### 2. Requisitos de la Base de Datos

#### 2.1 Estructura
- Motor: **MySQL**
- Tablas principales:
  - `Usuario (id, nombre, contraseña, tipo_id)`
  - `Tipo (id, nombre)`
  - `Aspiradora (id, ip, maquina_id, encendido, estado_id)`
  - `Estado (id, nombre)`
  - `Maquina (id, nombre)`

#### 2.2 Integridad
- Claves primarias bien definidas.
- Claves foráneas entre tablas (`Usuario` → `Tipo`, `Aspiradora` → `Estado` y `Maquina`).
- Base de datos normalizada para evitar redundancia y mejorar consistencia.

---

### 3. Requisitos de Comunicación con Dispositivos

#### 3.1 Comunicación bidireccional
- Conexión TCP/IP con cada aspiradora mediante su IP.
- Envío de comandos para cambiar estado o iniciar funciones.
- Recepción de datos actualizados de cada aspiradora.

#### 3.2 Gestión de errores
- Actualizar estado a “Error de conexión” si no hay respuesta.
- Mostrar notificaciones visuales ante errores de conexión o comandos inválidos.

---

### 4. Requisitos de Interfaz Gráfica

- Intuitiva, fácil de navegar y adaptada a distintos tamaños de pantalla.
- Paneles para gestión de usuarios y aspiradoras con funcionalidades completas de CRUD.
- Mostrar estado de aspiradoras con colores, íconos o etiquetas.
- Uso de **Java Swing** + **FlatLaf** para un diseño moderno.
- Retroalimentación clara: mensajes de éxito, advertencia o error.

---

### 5. Requisitos No Funcionales

- **Rendimiento:** Operaciones CRUD < 3 segundos, múltiples conexiones simultáneas.
- **Seguridad:** Acceso restringido a usuarios con privilegios; validación de entradas.
- **Escalabilidad:** Posibilidad de agregar nuevos tipos de máquinas o usuarios.
- **Mantenibilidad:** Código modular, documentado y estructurado por responsabilidad.

---

## Herramientas Utilizadas

- **IntelliJ IDEA Ultimate** – Desarrollo Java y conexión con MySQL.
- **Navicat Premium** – Administración y diseño de base de datos MySQL.
- **Visual Studio Code** – Desarrollo de software para ESP32.
- **Tower Git / GitHub** – Control de versiones y repositorio remoto.
- **FlatLaf** – Librería para interfaces gráficas modernas en Swing.
- **MySQL Server** – Base de datos relacional.
- **Socket / ServerSocket (Java.net)** – Comunicación TCP/IP con aspiradoras.
- **macOS** – Plataforma de desarrollo.

---

## Arquitectura del Programa

**Modelo MVC** (Modelo-Vista-Controlador)  

### 1. Modelo
- Clases de conexión y operaciones sobre base de datos (`SQL/`).
- Gestión de usuarios, aspiradoras y estados.
- Lógica de negocio y CRUD.

### 2. Vista
- Interfaz gráfica en **Java Swing** + **FlatLaf**.
- Paneles: Inicio de sesión, Principal, Manage, Register, Edit, Search, Settings.
- Visualización de estados y acciones de las aspiradoras.

### 3. Controlador
- Clases: `XtrackMachine`, `MainFrame`, `Principal`, `Manage`, `Settings`.
- Gestionan flujo de información entre modelo y vista.
- Procesan acciones del usuario y actualizan datos y UI.
