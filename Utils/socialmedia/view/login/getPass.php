<?php
    require_once(dirname(__FILE__)."./../../model/Encrypter.php");

    $pass = $_POST['pass'];
    $isEncrypted = $_POST['isEncrypted'];

    $encrypter = Encrypter::getInstance();
    if($isEncrypted == 0)
        $array = array("clave" => $encrypter->encrypt($pass));
    else
        $array = array("clave" => $encrypter->decrypt($pass));

    echo json_encode($array);
?>