<?php
    require_once(dirname(__FILE__)."./../../model/Connection.php");
    require_once(dirname(__FILE__)."./../../model/Encrypter.php");

    class LoginDAO
    {
        private static $dao;
        private static $db;

        private function __construct()
        {
            self::$db = Connection::getInstance()->connectDB();
        }

        public static function getInstance()
        {
            if(self::$dao == null)
                self::$dao = new LoginDAO();

            return self::$dao;
        }

        public function getUsuarioManual($nick, $pass)
        {
            $passEncriptado = Encrypter::getInstance()->encrypt($pass);

            $query = "SELECT u.id, u.nombre, u.direccion, u.correo, u.registro
            FROM credencial c
            JOIN usuario u ON c.usuario_id = u.id
            WHERE c.nick = '$nick'
            AND c.pass = '$passEncriptado'";

            //Obtiene los elementos de la consulta
            $result = mysqli_query(self::$db, $query);
            return mysqli_fetch_assoc($result);
        }

        public function getUsuarioMetodo($correo, $metodo)
        {
            $query = "SELECT u.id, u.nombre, u.direccion, u.correo, u.registro
            FROM usuario u
            JOIN cuenta cu ON u.id = cu.usuario_id
            JOIN metodo m ON m.id = cu.metodo_id
            WHERE cu.correo = '$correo'
            AND m.nombre = '$metodo'";

            //Obtiene los elementos de la consulta
            $result = mysqli_query(self::$db, $query);
            return mysqli_fetch_assoc($result);
        }
    }
?>