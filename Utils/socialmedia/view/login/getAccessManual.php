<?php
    require_once(dirname(__FILE__)."./../../controller/Login/LoginDAO.php");

    $nick = $_POST['nick'];
    $pass = $_POST['pass'];

    $dao = LoginDAO::getInstance();
    
    echo json_encode($dao->getUsuarioManual($nick, $pass));
?>