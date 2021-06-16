<?php
$hostname = "localhost:3306/media/pi/Volume/mysqlDatabase/mysql.sock";
$username = "tester";
$password = "Mo160703";
$db = "shareboard";

$dbconnect=mysqli_connect($hostname,$username,$password,$db);

if(isset($_GET['user_string'])){

if ($dbconnect->connect_error) {
    die("Database connection failed: " . $dbconnect->connect_error);
}

$user_string = $_GET['user_string'];
$pc = $_GET['pc'];

$query = mysqli_query($dbconnect, "SELECT clipboard_content, upload_date FROM clipboard WHERE user_string='$user_string'")
or die (mysqli_error($dbconnect));

$dl = array();

while ($row = mysqli_fetch_array($query)) {
    array_push($dl, array('clipboard' => $row['clipboard_content'], 'upload_date' => $row['upload_date']));
}
$json = json_encode($dl);
echo $json;

}else{
    echo "Request failed(Incorrect Input)";
}

$dbconnect->close();
?>
