<?php
include_once "conexion.php";

http_response_code(500);

function update_estudiante(
  mysqli $mysql,
  int $id,
  string $nombres,
  string $apellidos,
  string $carrera,
  string $año,
) {
  http_response_code(400);
  $query =
    "UPDATE estudiante SET nombres = ?, apellidos = ?, carrera = ?, año = ? WHERE id = ?";

  $result = $mysql->execute_query($query, [
    $nombres,
    $apellidos,
    $carrera,
    $año,
    $id,
  ]);

  if ($result) {
    http_response_code(200);
    echo "Registro actualizado";
  } else {
    echo "Error al modificar el registro: {$mysql->error}";
  }
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
  $conn = Connection::get_instance();
  $mysql = $conn->connect();

  $id = $_POST["id"];
  $nombres = $_POST["nombres"];
  $apellidos = $_POST["apellidos"];
  $carrera = $_POST["carrera"];
  $año = $_POST["año"];

  update_estudiante($mysql, $id, $nombres, $apellidos, $carrera, $año);

  $conn->disconnect();
} else {
  http_response_code(400);
  echo "No se realizó una solicitud POST";
}
