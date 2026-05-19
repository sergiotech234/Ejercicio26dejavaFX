📊 Gestión de Empleados (JavaFX + JDBC + Oracle)

Este proyecto es una aplicación de escritorio desarrollada en JavaFX que permite visualizar, buscar y eliminar empleados almacenados en una base de datos Oracle mediante JDBC.

🚀 Funcionalidades
📋 Mostrar empleados en un TableView
🔎 Filtrar empleados por nombre en tiempo real
🗑️ Eliminar empleado seleccionado
🔗 Conexión a base de datos Oracle con JDBC

Oracle Database (XE o similar)
🗄️ Base de datos

La aplicación utiliza una tabla llamada empleado2.

Ejemplo de estructura:
CREATE TABLE empleado2 (
    nombre VARCHAR2(100),
    salario NUMBER
);

Los datos se obtienen directamente desde Oracle:

SELECT nombre, salario FROM empleado2
🔎 Búsqueda en tiempo real

Permite filtrar empleados por nombre usando:

FilteredList
Listener sobre TextField
🗑️ Eliminar empleado

El empleado seleccionado se elimina de:

TableView
Base de datos Oracle
DELETE FROM empleado2 WHERE nombre = ?
