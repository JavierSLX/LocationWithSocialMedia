<?php
    //Clase Singleton que se encarga de conectar a la BD
    class Connection
    {
        private static $connection;
        private static $db;

        private function __construct($host = null, $user = null, $pass = null, $name = null, $port = null)
        {
            $host = $host == null ? "localhost" : $host;
            $user = $user == null ? "ultrachip" : $user;
            $pass = $pass == null ? "ultrachip" : $pass;
            $name = $name == null ? "socialmedia" : $name;
            $port = $port == null ? "3300" : $port;

            self::$db = new mysqli($host, $user, $pass, $name, $port);
        }

        public static function getInstance($host = null, $user = null, $pass = null, $db = null, $port = null)
        {
            if(self::$connection == null)
                self::$connection = new Connection($host, $user, $pass, $db, $port);

            return self::$connection;
        }

        public function connectDB()
        {
            if(self::$db->connect_error)
                die();
            else
            {
                self::$db->set_charset("utf8");
                return self::$db;
            }
        }

        public function __clone()
        {
            trigger_error("No se puede clonar la clase ".get_class(self::$connection));
        }
    }
?>