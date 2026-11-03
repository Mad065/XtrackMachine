# ⚙️ XtrackMachine – Sistema Central

> **Sistema Centralizado de Control para Aspiradoras Autónomas en Fresadoras**

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![TCP/IP](https://img.shields.io/badge/Network-TCP%2FIP-blue?style=for-the-badge)
![FlatLaf](https://img.shields.io/badge/UI-FlatLaf-lightgray?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)

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
3. [Requisitos Previos](#-requisitos-previos)
4. [Instalación y Configuración](#-instalación-y-configuración)
5. [Compilación y Ejecución](#-compilación-y-ejecución)
6. [Estructura de Base de Datos](#-estructura-de-base-de-datos)
7. [Stack Tecnológico y Herramientas](#-stack-tecnológico-y-herramientas)
8. [Licencia](#-licencia)

---

## 🚀 Características Principales

### 👥 Gestión de Control de Acceso (Usuarios)
* **Autenticación Segura:** Validación robusta de credenciales y prevención de accesos no autorizados con retroalimentación visual inmediata.
* **Administración CRUD:** Creación, lectura, actualización y eliminación de perfiles de usuario.
* **Control de Privilegios:** Diferenciación de accesos según el rol de usuario.

### 🤖 Gestión de Hardware (Aspiradoras)
* **Registro y Asignación:** Alta de dispositivos mediante IP e ID, y vinculación directa a máquinas fresadoras específicas.
* **Telemetría en Tiempo Real:** Monitorización de estados operativos (*En uso, Limpio, Error, Error de conexión, Desconocido*).
* **Administración CRUD:** Modificación dinámica de parámetros de red (IP), estado y asignación de maquinaria.

### 📡 Comunicación Bidireccional (TCP/IP)
* **Control Remoto:** Envío de comandos de ejecución y cambio de estado directamente al microcontrolador.
* **Detección de Red Dinámica:** Identificación automática de Gateway, Broadcast y Máscara de subred en macOS, Linux y Windows.
* **Manejo de Excepciones de Red:** Detección automática de pérdida de paquetes y actualización de estado ante fallos de conexión o timeouts.

---

## 🏗 Arquitectura del Sistema

El proyecto está estructurado bajo el patrón de diseño **MVC (Modelo-Vista-Controlador)**:

* **📦 Modelo (`SQL/`, `Network/`):** Gestiona la lógica de negocio, conexiones a la base de datos MySQL y la capa de transporte TCP/IP mediante Sockets.
* **🖥 Vista (`Main/*.form`):** Interfaz gráfica interactiva desarrollada con **Java Swing** y estilizada mediante **FlatLaf Dark**.
* **⚙️ Controlador (`Main/`):** Clases controladoras (`XtrackMachine`, `MainFrame`, `Principal`, `Manage`, `Edit`, `Register`, `Search`, `Settings`) que procesan eventos y coordinan vistas y modelos.

---

## 📋 Requisitos Previos

* **Java Development Kit (JDK):** Versión 21 o superior.
* **MySQL Server:** Versión 8.0 o superior en ejecución (puerto por defecto `3306`).
* **Apache Maven:** Versión 3.9+ (opcional si se utiliza IntelliJ IDEA).

---

## ⚙️ Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/Mad065/XtrackMachine.git
cd XtrackMachine
```

### 2. Configurar la Base de Datos
Importa el esquema y datos iniciales en tu servidor MySQL:
```bash
mysql -u root -p < src/SQL/XtrackMachine.sql
```

### 3. Configuración de Parámetros (`config.properties`)
El archivo de configuración se encuentra en `src/Main/config.properties`:
```properties
# Red y Polling
interval=3
delay=0
gateway=192.168.1.1
broadcast=192.168.1.255
mask=255.255.255.0

# Base de Datos
db.url=jdbc:mysql://127.0.0.1:3306/xtrackmachine
db.user=root
db.password=tu_password
```
> **Nota:** También puedes sobrescribir las credenciales mediante variables de entorno del sistema: `DB_URL`, `DB_USER` y `DB_PASSWORD`.

---

## 🔨 Compilación y Ejecución

### Opción A: Mediante Apache Maven (Terminal o CI/CD)
Para compilar y empaquetar un JAR ejecutable con todas las dependencias embebidas:
```bash
# Compilar y empaquetar
mvn clean package

# Ejecutar el Fat JAR generado
java -jar target/xtrackmachine-1.0.0-jar-with-dependencies.jar
```

### Opción B: Mediante IntelliJ IDEA
1. Abrir la carpeta raíz en IntelliJ IDEA (`File -> Open`).
2. El IDE detectará automáticamente la configuración del módulo (`XtrackMachine.iml`) o el archivo `pom.xml`.
3. Asegurarse de tener seleccionado el SDK Java 21 en `Project Structure -> Project SDK`.
4. Ejecutar la clase principal [`Main.XtrackMachine`](src/Main/XtrackMachine.java).

---

## 🗄 Estructura de Base de Datos

Motor relacional: **MySQL**

* **`Usuario`** `(id [PK], nombre, contrasena, tipo_id [FK])`
* **`Tipo`** `(id [PK], nombre)`
* **`Aspiradora`** `(id [PK], ip, maquina_id [FK], encendido, estado_id [FK])`
* **`Estado`** `(id [PK], nombre)`
* **`Maquina`** `(id [PK], nombre)`

---

## 🛠 Stack Tecnológico y Herramientas

* **Lenguaje:** Java 21
* **Gestor de Dependencias / Build:** Apache Maven
* **Framework UI:** Java Swing + [FlatLaf 3.5.4](https://www.formdev.com/flatlaf/)
* **Driver JDBC:** [MySQL Connector/J 9.1.0](https://dev.mysql.com/doc/connector-j/en/)
* **Protocolo de Red:** `java.net.Socket` / `ServerSocket` (TCP/IP)
* **CI/CD:** GitHub Actions

---

## 📄 Licencia

Este proyecto está bajo la Licencia **MIT**. Consulta el archivo [`LICENSE`](LICENSE) para obtener más detalles.

> **Aviso de responsabilidad:** El software y firmware se proporcionan "tal cual", sin garantías sobre su desempeño directo con maquinaria física o fresadoras en entornos de producción.
