<?php
$hostname = "localhost:3306/media/pi/Volume/mysqlDatabase/mysql.sock";
$username = "shareboard";
$password = "Mo160703";
$db = "shareboard";

$dbconnect=mysqli_connect($hostname,$username,$password,$db);
if(isset($_GET['id'], $_GET['user_string'])){

    $id = $_GET['id'];
    $user_string = $_GET['user_string'];

    if ($dbconnect->connect_error) {
        die("Database connection failed: " . $dbconnect->connect_error);
    }
    $sql = "DELETE FROM clipboard WHERE user_string='$user_string' AND clipboard_id='$id'";
    if ($dbconnect->query($sql) === TRUE) {
        echo "Record sucessfully deleted";
    } else {
        echo "Error: " . $sql . "<br>" . $dbconnect->error;
    }
}else {
    echo "Request failed(Incorrect Input)";
}
$dbconnect->close();
?>