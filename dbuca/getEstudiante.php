<?php
include_once "conexion.php";

http_response_code(500);

function parse_estudiante(array $data): string {
  $estudiante = "{";

  $estudiante .= "\"id\": " . $data["id"] . ",";
  $estudiante .= "\"nombres\": \"" . $data["nombres"] . "\",";
  $estudiante .= "\"apellidos\": \"" . $data["apellidos"] . "\",";
  $estudiante .= "\"carrera\": \"" . $data["carrera"] . "\",";
  $estudiante .= "\"año\": " . $data["año"] . "";

  $estudiante .= "}";
  return $estudiante;
}

function get_estudiante_by_id(mysqli $mysql, string $id): string {
  $result = $mysql->execute_query("SELECT * FROM estudiante WHERE id = ?", [
    $id,
  ]);

  $estudiante = $result->fetch_assoc();
  $estudiante = parse_estudiante($estudiante);
  $estudiante = trim($estudiante);

  http_response_code(200);

  $result->close();
  return $estudiante;
}

function get_all_estudiantes(mysqli $mysql): string {
  http_response_code(400);
  $result = $mysql->query("SELECT * FROM estudiante;");

  $estudiantes = "";
  if ($mysql->affected_rows > 0) {
    $estudiantes = "{\"data\": [";
    while ($row = $result->fetch_assoc()) {
      $estudiantes = $estudiantes . parse_estudiante($row);
      $estudiantes = $estudiantes . ",";
    }

    $estudiantes = trim($estudiantes);
    $estudiantes = $estudiantes . "]}";
  }

  http_response_code(200);

  $result->close();
  return $estudiantes;
}

if ($_SERVER["REQUEST_METHOD"] == "GET") {
  $conn = Connection::get_instance();
  $mysql = $conn->connect();

  if (isset($_GET["id"])) {
    $id_estudiante = $_GET["id"];
    $estudiante = get_estudiante_by_id($mysql, $id_estudiante);
    echo $estudiante;
    return;
  }

  $estudiantes = get_all_estudiantes($mysql);
  echo $estudiantes;

  $conn->disconnect();
} else {
  http_response_code(400);
  echo "No se realizó una solicitud GET";
}
