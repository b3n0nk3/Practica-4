-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 08-11-2023 a las 11:34:43
-- Versión del servidor: 10.4.28-MariaDB
-- Versión de PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `empresa`
--

CREATE DATABASE empresa;
USE empresa;

DELIMITER $$
--
-- Procedimientos
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `insertar_programador` (IN `e_nombre` VARCHAR(100), IN `e_apellidos` VARCHAR(100), IN `e_fecha_nacimiento` VARCHAR(20), IN `e_email` VARCHAR(100), IN `e_lenguajes` VARCHAR(255), OUT `id_empleado` MEDIUMINT)   BEGIN

    DECLARE i INT DEFAULT 1;
    DECLARE leng VARCHAR(40) DEFAULT '';
    DECLARE leng_id MEDIUMINT DEFAULT 0;
    DECLARE num_lenguajes INT;


    -- Insertamos el nuevo empleado

    INSERT INTO empleado (nombre, apellidos, fecha_nacimiento, puesto, email)
    VALUES (e_nombre, e_apellidos, STR_TO_DATE(e_fecha_nacimiento,'%d/%m/%Y'), 'programador', e_email);

    SET id_empleado = last_insert_id();
    
    WHILE i <= (SELECT NUM_ELEMENTS(e_lenguajes, ',')) DO
    	SET leng = (SELECT SPLIT_STR_POS(e_lenguajes, ',', i));
 		-- call debug_msg(TRUE, (SELECT concat_ws('','leng:', leng)));
        -- call debug_msg(TRUE, (SELECT GROUP_CONCAT(nombre) FROM lenguaje));
        SET leng_id = (SELECT id_lenguaje FROM lenguaje WHERE nombre = leng);
	
        INSERT INTO empleado_lenguaje (id_empleado, id_lenguaje, nivel)
        VALUES (id_empleado, leng_id, 'básico');
        
        SET i = i+1;
    
    END WHILE;
END$$

--
-- Funciones
--
CREATE DEFINER=`root`@`localhost` FUNCTION `NUM_ELEMENTS` (`x` VARCHAR(255), `delim` VARCHAR(12)) RETURNS INT(11) DETERMINISTIC BEGIN
    RETURN OCURRENCES(x, delim) + 1;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `OCURRENCES` (`x` VARCHAR(255), `delim` VARCHAR(12)) RETURNS INT(11) DETERMINISTIC BEGIN
    RETURN LENGTH(x) - LENGTH(REPLACE(x, delim, ''));
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `SPLIT_STR_POS` (`x` VARCHAR(255), `delim` VARCHAR(12), `pos` INT) RETURNS VARCHAR(255) CHARSET utf8mb4 COLLATE utf8mb4_general_ci DETERMINISTIC BEGIN 
    RETURN TRIM(REPLACE(SUBSTRING(SUBSTRING_INDEX(x, delim, pos),
       LENGTH(SUBSTRING_INDEX(x, delim, pos -1)) + 1),
       delim, ''));
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleado`
--

CREATE TABLE `empleado` (
  `id_empleado` mediumint(9) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `puesto` varchar(100) NOT NULL,
  `email` varchar(320) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `empleado`
--

INSERT INTO `empleado` (`id_empleado`, `nombre`, `apellidos`, `fecha_nacimiento`, `puesto`, `email`) VALUES
(1, 'José', 'García Martínez', '2000-01-01', 'programador', 'jose@openwebinars.net'),
(2, 'María', 'Almagro Muñoz', '1995-03-02', 'programador', 'maria@openwebinars.net'),
(3, 'Luis Miguel', 'López Magaña', '1982-09-18', 'profesor', 'luismi@openwebinars.net'),
(9, 'María', 'González Ferrán', '2001-02-23', 'programador', 'maria@openwebinars.net');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleado_lenguaje`
--

CREATE TABLE `empleado_lenguaje` (
  `id_empleado` mediumint(9) NOT NULL,
  `id_lenguaje` mediumint(9) NOT NULL,
  `nivel` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `empleado_lenguaje`
--

INSERT INTO `empleado_lenguaje` (`id_empleado`, `id_lenguaje`, `nivel`) VALUES
(9, 1, 'básico'),
(9, 2, 'básico');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `lenguaje`
--

CREATE TABLE `lenguaje` (
  `id_lenguaje` mediumint(9) NOT NULL,
  `nombre` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `lenguaje`
--

INSERT INTO `lenguaje` (`id_lenguaje`, `nombre`) VALUES
(1, 'Java'),
(2, 'Python'),
(3, 'Javascript'),
(4, 'C'),
(5, 'C++'),
(6, 'Typescript'),
(7, 'C#');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `empleado`
--
ALTER TABLE `empleado`
  ADD PRIMARY KEY (`id_empleado`);

--
-- Indices de la tabla `empleado_lenguaje`
--
ALTER TABLE `empleado_lenguaje`
  ADD PRIMARY KEY (`id_empleado`,`id_lenguaje`),
  ADD KEY `id_lenguaje` (`id_lenguaje`);

--
-- Indices de la tabla `lenguaje`
--
ALTER TABLE `lenguaje`
  ADD PRIMARY KEY (`id_lenguaje`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `empleado`
--
ALTER TABLE `empleado`
  MODIFY `id_empleado` mediumint(9) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `lenguaje`
--
ALTER TABLE `lenguaje`
  MODIFY `id_lenguaje` mediumint(9) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `empleado_lenguaje`
--
ALTER TABLE `empleado_lenguaje`
  ADD CONSTRAINT `empleado_lenguaje_ibfk_1` FOREIGN KEY (`id_empleado`) REFERENCES `empleado` (`id_empleado`),
  ADD CONSTRAINT `empleado_lenguaje_ibfk_2` FOREIGN KEY (`id_lenguaje`) REFERENCES `lenguaje` (`id_lenguaje`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
