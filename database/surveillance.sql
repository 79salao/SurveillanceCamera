SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `surveillance`
--
CREATE DATABASE surveillance;

--
-- Estructura de tabla para la tabla `records`
--

CREATE TABLE `records` (
  `id` int(11) NOT NULL,
  `date` datetime NOT NULL,
  `duration` int(11) NOT NULL DEFAULT 0,
  `camera` int(11) NOT NULL,
  `video_location` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `records`
--

INSERT INTO `records` (`id`, `date`, `duration`, `camera`, `video_location`) VALUES
(156, '2020-02-21 09:31:01', 23, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_09:31:01.h264'),
(157, '2020-02-20 00:32:02', 10, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_09:32:02.h264'),
(158, '2020-02-19 10:32:02', 10, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_09:32:02.h264'),
(159, '2020-02-19 12:32:02', 10, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_09:32:02.h264'),
(160, '2020-02-18 12:32:02', 10, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_09:32:02.h264'),
(161, '2020-02-16 14:32:02', 10, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_09:32:02.h264'),
(162, '2020-02-16 14:32:02', 10, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_09:32:02.h264'),
(163, '2020-02-15 20:32:02', 10, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_09:32:02.h264'),
(164, '2020-02-10 20:32:02', 10, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_09:32:02.h264'),
(165, '2020-02-21 09:43:27', 16, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_09:43:27.h264'),
(166, '2020-02-21 09:45:31', 14, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_09:45:31.h264'),
(167, '2020-02-21 10:13:39', 12, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_10:13:39.h264'),
(168, '2020-02-21 10:15:05', 15, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_10:15:05.h264'),
(169, '2020-02-21 10:19:21', 9, 1, '/home/pi/Desktop/SurveillanceCamera-master/2020_02_21_10:19:21.h264');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(16) NOT NULL DEFAULT '',
  `password` varchar(16) NOT NULL DEFAULT '',
  `email` varchar(50) NOT NULL DEFAULT '',
  `name` varchar(50) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `email`, `name`) VALUES
(0, 'admin', 'testTesting4Pi', 'tbonotifica@hotmail.com', 'admin'),
(2, 'aps', 'aps', 'alu.112478@usj.es', 'Angela');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `records`
--
ALTER TABLE `records`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `records`
--
ALTER TABLE `records`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=170;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
