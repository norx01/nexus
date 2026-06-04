-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost:8889
-- Tiempo de generación: 04-06-2026 a las 00:28:33
-- Versión del servidor: 8.0.35
-- Versión de PHP: 8.3.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `nexus`
--

--
-- Volcado de datos para la tabla `ambientes`
--

INSERT INTO `ambientes` (`id_ambiente`, `nombre`, `sede`, `estado`, `observaciones`) VALUES
(1, '202 Sala de sistemas', 'Salesiano', 'Disponible', 'prueba');

--
-- Volcado de datos para la tabla `llaves`
--

INSERT INTO `llaves` (`id_llave`, `id_ambiente`, `nombre`, `estado`) VALUES
(1, 1, 'llave 202', 'Disponible');

--
-- Volcado de datos para la tabla `personal`
--

INSERT INTO `personal` (`id_personal`, `nombre`, `documento`, `telefono`, `correo`, `direccion`, `rol`) VALUES
(1, 'juan', '123', '321', 'juan@sena.com', 'calle', 'Instructor');

--
-- Volcado de datos para la tabla `personal_externo`
--

INSERT INTO `personal_externo` (`id_personal_externo`, `documento`, `empresa`, `estado`, `fecha_creacion`, `fecha_visita`, `hora_ingreso`, `hora_salida`, `motivo_visita`, `nombre`, `telefono`, `tiempo_estancia`, `tipo_documento`) VALUES
(1, '123', 'logis', 'Activo', NULL, '2026-04-23', '00:33:00', NULL, 'sdsd', 'juan', '321', 0, 'CC'),
(2, '123', 'logis', 'Activo', NULL, '2026-04-23', '00:33:00', NULL, 'sdsd', 'juan', '321', 0, 'CC');

--
-- Volcado de datos para la tabla `prestamo_ambientes`
--

INSERT INTO `prestamo_ambientes` (`id_prestamo`, `id_personal`, `id_ambiente`, `fecha`, `hora_entrada`, `hora_salida`) VALUES
(1, 1, 1, '2026-05-06', '17:00:00', '20:00:00');

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`id_rol`, `descripcion`, `estado`) VALUES
(1, 'Administrador', 'Activo'),
(2, 'Vigilante', 'Activo');

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `apellido`, `fecha`, `dni`, `correo`, `documento`, `username`, `password`, `id_rol`) VALUES
(7, 'jose', 'Cruz', '2025-03-23', 111, 'ahsdjh@snnmjbd', 0, '', '', 1),
(8, 'Daniela', 'perez', '2026-02-13', 741, 'daniela@lkdshgkj', 0, '', '', 1),
(11, 'andres', 'puentes', '2026-03-24', 987, 'sjkgdfag', 0, '', '', 1),
(13, 'Admin', 'Sistema', '2024-01-01', NULL, 'admin@nexus.com', 0, 'admin', '$2a$10$lx5Wplehb7sr5KwJ1PbdFutH1efwVflfa5LwkVZByvB/uGxtjsurm', 1),
(14, 'Vigilante', 'Demo', '2024-01-01', NULL, 'vigilante@nexus.com', 1, 'vigilante', '$2a$10$uUu4bTVjFGv1kRlI1uVMm.pzxaTtEfR8FIiRp8i5DrMxx6fbQORWy', 2);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
