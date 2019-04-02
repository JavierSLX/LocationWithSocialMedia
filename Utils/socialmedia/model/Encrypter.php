<?php
    //Clase singleton que se encarga de encriptar y desencriptar un elemento 
    class Encrypter
    {
        private static $encrypter;
        private static $key = "marquesada!466";

        //Constructor privado
        private function __construct()
        {
        }

        //Metodo instanciado de la clase
        public static function getInstance()
        {
            if(!isset(self::$encrypter))
                self::$encrypter = new Encrypter();

            return self::$encrypter;
        }

        //Sobrecarga de métodos (unica forma de hacerlo)
        public function encrypt($input, $llave = null)
        {
            $clave = $llave == null ? self::$key : $llave;

            return base64_encode(mcrypt_encrypt(MCRYPT_RIJNDAEL_256, md5($clave),
            $input, MCRYPT_MODE_CBC, md5(md5($clave))));
        }

        public function decrypt($input, $llave = null)
        {
            $clave = $llave == null ? self::$key : $llave;

            return rtrim(mcrypt_decrypt(MCRYPT_RIJNDAEL_256, md5($clave), 
            base64_decode($input), MCRYPT_MODE_CBC, md5(md5($clave))), "\0");
        }

        //Evita que el objeto se pueda clonar
        public function __clone()
        {
            trigger_error("No se puede clonar la clase Encrypter");
        }
    }
?>