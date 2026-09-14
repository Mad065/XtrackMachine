# ⚙️ XtrackMachine – Sistema Central

> **Sistema Centralizado de Control para Aspiradoras Autónomas en Fresadoras**

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![TCP/IP](https://img.shields.io/badge/Network-TCP%2FIP-blue?style=for-the-badge)
![FlatLaf](https://img.shields.io/badge/UI-FlatLaf-lightgray?style=for-the-badge)

## 🔗 Ecosistema del Proyecto

Este repositorio contiene el **Software de Control Central** (Desktop App). Para acceder al código fuente que se ejecuta en los microcontroladores de las aspiradoras, visita el repositorio del hardware:
👉 **[XtrackMachine-ESP32 (Firmware)](https://github.com/Mad065/XtrackMachine-ESP32)**

---

## 📖 Descripción General

**XtrackMachine** es una solución de software basada en principios de programación orientada a objetos, diseñada para la **gestión y control en tiempo real de aspiradoras autónomas** utilizadas en entornos de fresado. 

El sistema optimiza la administración de estos dispositivos a través de una interfaz gráfica moderna, permitiendo realizar operaciones CRUD completas sobre usuarios y equipos. Su núcleo de red, basado en **sockets TCP/IP**, garantiza una comunicación bidireccional estable, segura y de baja latencia con el hardware (ESP32).

---

## 📑 Tabla de Contenidos
1. [Características Principales](#-características-principales)
2. [Arquitectura del Sistema](#-arquitectura-del-sistema)
3. [Especificaciones Técnicas](#-especificaciones-técnicas-requisitos-no-funcionales)
4. [Estructura de Base de Datos](#-estructura-de-base-de-datos)
5. [Stack Tecnológico y Herramientas](#-stack-tecnológico-y-herramientas)

---

## 🚀 Características Principales

### 👥 Gestión de Control de Acceso (Usuarios)
* **Autenticación Segura:** Validación robusta de credenciales y prevención de accesos no autorizados con retroalimentación de errores.
* **Administración CRUD:** Creación, lectura, actualización y eliminación (con confirmación de seguridad) de perfiles de usuario.
* **Control de Privilegios:** Diferenciación de accesos según el tipo de usuario.

### 🤖 Gestión de Hardware (Aspiradoras)
* **Registro y Asignación:** Alta de dispositivos mediante IP e ID, y vinculación directa a máquinas fresadoras específicas.
* **Telemetría en Tiempo Real:** Monitorización de estados operativos (*Sucio, Limpio, Limpiando, En uso, Error, Error de conexión, Desconocido*).
* **Administración CRUD:** Modificación dinámica de parámetros de red (IP), estado y asignación de maquinaria.

### 📡 Comunicación Bidireccional (TCP/IP)
* **Control Remoto:** Envío de comandos de ejecución y cambio de estado directamente al hardware.
* **Manejo de Excepciones de Red:** Detección automática de pérdida de paquetes y actualización de estado a *"Error de conexión"* ante timeouts.

---

## 🏗 Arquitectura del Sistema

El proyecto está estructurado bajo el patrón de diseño **MVC (Modelo-Vista-Controlador)**, garantizando código modular, mantenible y con separación de responsabilidades:

* **📦 Modelo (`SQL/`, Entidades):** Gestiona la lógica de negocio, las conexiones a la base de datos MySQL y las operaciones CRUD de usuarios, aspiradoras y estados.
* **🖥 Vista (`UI/`):** Desarrollada en **Java Swing** con el Look and Feel de **FlatLaf**. Incluye paneles responsivos (Login, Principal, Gestión, Búsqueda, Configuración).
* **⚙️ Controlador (`MainFrame`, `XtrackMachine`):** Intermediario que procesa los eventos de la interfaz, orquesta la comunicación con las aspiradoras mediante Sockets y actualiza la base de datos y la UI.

---

## 📊 Especificaciones Técnicas (Requisitos No Funcionales)

| Categoría | Especificación |
| :--- | :--- |
| **Rendimiento** | Operaciones CRUD en < 3 segundos. Soporte para múltiples conexiones TCP/IP concurrentes. |
| **Seguridad** | Accesos restringidos por jerarquía y sanitización de entradas en la interfaz. |
| **Escalabilidad** | Base de datos normalizada para fácil integración de nuevas máquinas o estados. |
| **UX/UI** | Interfaz intuitiva con retroalimentación explícita (éxito, advertencia, error). |

---

## 🗄 Estructura de Base de Datos

Motor relacional: **MySQL**

* **`Usuario`** `(id [PK], nombre, contraseña, tipo_id [FK])`
* **`Tipo`** `(id [PK], nombre)`
* **`Aspiradora`** `(id [PK], ip, maquina_id [FK], encendido, estado_id [FK])`
* **`Estado`** `(id [PK], nombre)`
* **`Maquina`** `(id [PK], nombre)`

---

## 🛠 Stack Tecnológico y Herramientas

* **Lenguaje Core:** Java
* **IDE / Entorno:** IntelliJ IDEA Ultimate (macOS)
* **Framework UI:** Java Swing + [FlatLaf](https://www.formdev.com/flatlaf/)
* **Base de Datos:** MySQL Server (Administrado con Navicat Premium)
* **Protocolo de Red:** `java.net.Socket` / `ServerSocket` (TCP/IP)

---

## 📄 Licencia

Este proyecto está bajo la Licencia **MIT**. Consulta el archivo [`LICENSE`](LICENSE) para obtener más detalles.

> **Aviso de responsabilidad:** El software y firmware se proporcionan "tal cual", sin garantías sobre su desempeño directo con maquinaria física o fresadoras en entornos de producción.
