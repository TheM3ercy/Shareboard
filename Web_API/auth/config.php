<?php
define('DB_SERVER', 'localhost:3306/media/pi/Volume/mysqlDatabase/mysql.sock');
define('DB_USERNAME', 'tester');
define('DB_PASSWORD', 'Mo160703');
define('DB_NAME', 'shareboard');

$link = mysqli_connect(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_NAME);

if($link === false){
    die("ERROR: Could not connect. " . mysqli_connect_error());
}
?>
