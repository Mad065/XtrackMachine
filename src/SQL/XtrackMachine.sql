-- BEGIN DataBase XtrackMachine
CREATE DATABASE XtrackMachine;
USE XtrackMachine;
-- END DataBase XtrackMachine

-- BEGIN TABLE Tipo_usuario
DROP TABLE IF EXISTS Tipo_usuario;
CREATE TABLE `Tipo_usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Inserting 1 row into Tipo_usuario
-- Insert batch #1
INSERT INTO Tipo_usuario (id, nombre) VALUES
(1, 'Dios');

-- END TABLE Tipo_usuario

-- BEGIN TABLE Estado
DROP TABLE IF EXISTS Estado;
CREATE TABLE `Estado` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Inserting 7 rows into Estado
-- Insert batch #1
INSERT INTO Estado (id, nombre) VALUES
(1, 'Sucio'),
(2, 'Limpio'),
(3, 'Limpiando'),
(4, 'En uso'),
(5, 'Error'),
(6, 'Error de conexion'),
(7, 'Desconocido');

-- END TABLE Estado

-- BEGIN TABLE Maquina
DROP TABLE IF EXISTS Maquina;
CREATE TABLE `Maquina` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Inserting 3 rows into Maquina
-- Insert batch #1
INSERT INTO Maquina (id, nombre) VALUES
(1, 'Fresadora tipo A'),
(2, 'Fresadora tipo B'),
(3, 'Fresadora tipo C');

-- END TABLE Maquina

-- BEGIN TABLE Usuario
DROP TABLE IF EXISTS Usuario;
CREATE TABLE `Usuario` (
  `nombre` varchar(100) NOT NULL,
  `contraseña` varchar(100) NOT NULL,
  `tipo` int NOT NULL,
  PRIMARY KEY (`nombre`),
  KEY `tipo` (`tipo`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`tipo`) REFERENCES `Tipo_usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Inserting 1 row into Usuario
-- Insert batch #1
INSERT INTO Usuario (nombre, `contraseña`, tipo) VALUES
('Mad0', '1234', 1);

-- END TABLE Usuario

-- BEGIN TABLE Aspiradora
DROP TABLE IF EXISTS Aspiradora;
CREATE TABLE `Aspiradora` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ip` varchar(50) NOT NULL,
  `maquina` int NOT NULL,
  `encendido` tinyint(1) NOT NULL,
  `estado` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `maquina` (`maquina`),
  KEY `estado` (`estado`),
  CONSTRAINT `aspiradora_ibfk_1` FOREIGN KEY (`maquina`) REFERENCES `Maquina` (`id`),
  CONSTRAINT `aspiradora_ibfk_2` FOREIGN KEY (`estado`) REFERENCES `Estado` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Inserting 1 row into Aspiradora
-- Insert batch #1
INSERT INTO Aspiradora (id, ip, maquina, encendido, estado) VALUES
(1, '192.168.3.31', 1, 0, 1);

-- END TABLE Aspiradora