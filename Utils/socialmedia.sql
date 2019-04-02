DROP DATABASE IF EXISTS socialmedia;
CREATE DATABASE IF NOT EXISTS socialmedia;
USE socialmedia;

CREATE TABLE usuario
(
	id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(60) NOT NULL,
    direccion VARCHAR(60) NOT NULL,
    correo VARCHAR(50) NOT NULL,
    registro DATETIME DEFAULT now(),
    activo BOOLEAN DEFAULT TRUE
);

INSERT INTO usuario(nombre, direccion, correo) VALUES ('Javier Serrano Lule', 'Ocampo 2', 'jserranolule@gmail.com');

CREATE TABLE credencial
(
	id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    nick VARCHAR(20) NOT NULL,
    pass VARCHAR(60) NOT NULL,
    usuario_id INT UNSIGNED NOT NULL,
    FOREIGN KEY(usuario_id) REFERENCES usuario(id)
);

INSERT INTO credencial(nick, pass, usuario_id) VALUES ('javiersl', 'oQ1dMA+fV3tw4+AQr8Bnipf6NWQJ47hHE3ZZJNB4Tb8=', 1);

CREATE TABLE metodo
(
	id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(20) NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

INSERT INTO metodo(nombre) VALUES ('Google'), ('Facebook');

CREATE TABLE cuenta
(
	id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    correo VARCHAR(50) NOT NULL,
    usuario_id INT UNSIGNED NOT NULL,
    metodo_id INT UNSIGNED NOT NULL,
    FOREIGN KEY(usuario_id) REFERENCES usuario(id),
    FOREIGN KEY(metodo_id) REFERENCES metodo(id)
);

INSERT INTO cuenta(correo, usuario_id, metodo_id) VALUES ('legend87jea@gmail.com', 1, 1), ('legend87jea@gmail.com', 1, 2);