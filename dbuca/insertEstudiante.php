<?php
include_once "conexion.php";

http_response_code(500);

function insert_estudiante(
  mysqli $mysql,
  string $nombres,
  string $apellidos,
  string $carrera,
  string $año,
) {
  http_response_code(400);
  $query =
    "INSERT INTO estudiante (nombres, apellidos, carrera, año) VALUES(?, ?, ?, ?);";

  $result = $mysql->execute_query($query, [
    $nombres,
    $apellidos,
    $carrera,
    $año,
  ]);

  if ($result) {
    http_response_code(200);
    echo "Registro guardado";
  } else {
    echo "Error al guardar el registro: {$mysql->error}";
  }
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
  $conn = Connection::get_instance();
  $mysql = $conn->connect();

  $nombres = $_POST["nombres"];
  $apellidos = $_POST["apellidos"];
  $carrera = $_POST["carrera"];
  $año = $_POST["año"];

  insert_estudiante($mysql, $nombres, $apellidos, $carrera, $año);

  $conn->disconnect();
} else {
  http_response_code(400);
  echo "No se realizó una solicitud POST";
}
