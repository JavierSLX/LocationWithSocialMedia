<?php
    require_once(dirname(__FILE__)."./../../controller/Login/LoginDAO.php");

    $correo = $_POST['correo'];
    $metodo = $_POST['metodo'];

    $dao = LoginDAO::getInstance();

    echo json_encode($dao->getUsuarioMetodo($correo, $metodo));
?>