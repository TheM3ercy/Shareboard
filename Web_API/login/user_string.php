<?php
$hostname = "localhost:3306/media/pi/Volume/mysqlDatabase/mysql.sock";
$username = "tester";
$password = "Mo160703";
$db = "shareboard";

$dbconnect=mysqli_connect($hostname,$username,$password,$db);

if(isset($_GET['username'], $_GET['password'])){
    $username = $_GET['username'];
    $password = $_GET['password'];
    $correct = false;

    if ($dbconnect->connect_error) {
        die("Database connection failed: " . $dbconnect->connect_error);
    }

    $query = mysqli_query($dbconnect, "SELECT username, password FROM users")
    or die (mysqli_error($dbconnect));

    while ($row = mysqli_fetch_array($query)) {
        if ($row['username'] == $username && $row['password'] == $password){
            $query = mysqli_query($dbconnect, "SELECT user_string FROM users WHERE username='$username'")
            or die (mysqli_error($dbconnect));

            $dl = array();

            while ($row = mysqli_fetch_array($query)) {
                array_push($dl, array('user_string' => $row['user_string']));
            }
            $json = json_encode($dl);
            echo $json;
            $correct = true;
        }

    }
    if($correct == false){
        echo "Username or Password incorrect!";
    }


}else{
    echo "Enter credentials!";
}

$dbconnect->close();
?>