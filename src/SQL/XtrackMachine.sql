-- BEGIN DataBase XtrackMachine
CREATE DATABASE IF NOT EXISTS XtrackMachine;
USE XtrackMachine;
-- END DataBase XtrackMachine

-- BEGIN TABLE Tipo_usuario
DROP TABLE IF EXISTS Tipo_usuario;
CREATE TABLE Tipo_usuario (
                              id INT NOT NULL AUTO_INCREMENT,
                              nombre VARCHAR(100) NOT NULL,
                              PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO Tipo_usuario (id, nombre) VALUES
    (1, 'Dios');
-- END TABLE Tipo_usuario

-- BEGIN TABLE Estado
DROP TABLE IF EXISTS Estado;
CREATE TABLE Estado (
                        id INT NOT NULL AUTO_INCREMENT,
                        nombre VARCHAR(100) NOT NULL,
                        PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
CREATE TABLE Maquina (
                         id INT NOT NULL AUTO_INCREMENT,
                         nombre VARCHAR(100) NOT NULL,
                         PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO Maquina (id, nombre) VALUES
                                     (1, 'Fresadora tipo A'),
                                     (2, 'Fresadora tipo B'),
                                     (3, 'Fresadora tipo C');
-- END TABLE Maquina

-- BEGIN TABLE Usuario
DROP TABLE IF EXISTS Usuario;
CREATE TABLE Usuario (
                         nombre VARCHAR(100) NOT NULL,
                         contrasena VARCHAR(100) NOT NULL,
                         tipo INT NOT NULL,
                         PRIMARY KEY (nombre),
                         KEY tipo (tipo),
                         CONSTRAINT usuario_ibfk_1 FOREIGN KEY (tipo) REFERENCES Tipo_usuario (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO Usuario (nombre, contrasena, tipo) VALUES
    ('Mad0', '1234', 1);
-- END TABLE Usuario

-- BEGIN TABLE Aspiradora
DROP TABLE IF EXISTS Aspiradora;
CREATE TABLE Aspiradora (
                            id INT NOT NULL AUTO_INCREMENT,
                            ip VARCHAR(50) NOT NULL,
                            maquina INT NOT NULL,
                            encendido TINYINT(1) NOT NULL,
                            estado INT NOT NULL,
                            PRIMARY KEY (id),
                            KEY maquina (maquina),
                            KEY estado (estado),
                            CONSTRAINT aspiradora_ibfk_1 FOREIGN KEY (maquina) REFERENCES Maquina (id),
                            CONSTRAINT aspiradora_ibfk_2 FOREIGN KEY (estado) REFERENCES Estado (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO Aspiradora (id, ip, maquina, encendido, estado) VALUES
    (1, '192.168.3.31', 1, 0, 1);
-- END TABLE Aspiradora
