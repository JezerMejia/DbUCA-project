<?php

class Connection {
  private mysqli $mysql;
  private static ?Connection $instance = null;

  private function __construct() {
    $hostname = "localhost";
    $database = "dbuca";
    $username = "root";
    $password = "";

    $this->mysql = new mysqli($hostname, $username, $password, $database);
  }

  public static function get_instance(): Connection {
    if (!self::$instance) {
      self::$instance = new Connection();
    }
    return self::$instance;
  }

  public function connect(): mysqli {
    return $this->mysql;
  }

  public function disconnect(): void {
    try {
      $this->mysql->close();
    } catch (PDOException $e) {
      die($e->getMessage());
    }
  }
}
