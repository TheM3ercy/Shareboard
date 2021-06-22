<?php
$hostname = "localhost:3306/media/pi/Volume/mysqlDatabase/mysql.sock";
$username = "shareboard";
$password = "Mo160703";
$db = "shareboard";

$dbconnect=mysqli_connect($hostname,$username,$password,$db);
if(isset($_GET['username'], $_GET['password'], $_GET['email'])){

if ($dbconnect->connect_error) {
    die("Database connection failed: " . $dbconnect->connect_error);
}

    $username = $_GET['username'];
    $password = $_GET['password'];
    $email = $_GET['email'];
    $user_string = uniqid(25);
    $sql = "INSERT INTO users (username, password, email, user_string) VALUES ('$username', '$password', '$email', '$user_string')";
    if ($dbconnect->query($sql) === TRUE) {
        echo "New record created successfully";
    } else {
        echo "Error: " . $sql . "<br>" . $dbconnect->error;
    }
}else {
    echo "Request failed(Incorrect Input)";
}
$dbconnect->close();
?>
