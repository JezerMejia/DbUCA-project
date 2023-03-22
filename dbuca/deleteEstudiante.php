<?php
include_once "conexion.php";

http_response_code(500);

function delete_estudiante(
  mysqli $mysql,
  int $id
) {
  http_response_code(400);
  $query =
    "DELETE FROM estudiante WHERE id = ?";

  $result = $mysql->execute_query($query, [
    $id
  ]);

  if ($result) {
    http_response_code(200);
    echo "Registro eliminado";
  } else {
    echo "Error al eliminar el registro: {$mysql->error}";
  }
}

if ($_SERVER["REQUEST_METHOD"] == "DELETE") {
  $conn = Connection::get_instance();
  $mysql = $conn->connect();

  $data = file_get_contents("php://input");
  $request_vars = array();
  parse_str($data, $request_vars );

  $id = $_REQUEST["id"];

  delete_estudiante($mysql, $id);

  $conn->disconnect();
} else {
  http_response_code(400);
  echo "No se realizó una solicitud DELETE";
}
