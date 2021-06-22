<?php
$hostname = "localhost:3306/media/pi/Volume/mysqlDatabase/mysql.sock";
$username = "shareboard";
$password = "Mo160703";
$db = "shareboard";

$dbconnect = mysqli_connect($hostname, $username, $password, $db);
if (isset($_GET['user_string'], $_GET['content'])) {

    $user_string = $_GET['user_string'];
    $content = $_GET['content'];

    if ($dbconnect->connect_error) {
        die("Database connection failed: " . $dbconnect->connect_error);
    }
    $sql = "INSERT INTO clipboard (clipboard_content, user_string) VALUES ('$content', '$user_string')";
    if ($dbconnect->query($sql) === TRUE) {
        $query = mysqli_query($dbconnect, "SELECT clipboard_id FROM clipboard WHERE user_string='$user_string' AND clipboard_content='$content'")
        or die (mysqli_error($dbconnect));
        $dl = array();

        while ($row = mysqli_fetch_array($query)) {
            array_push($dl, array('id' => $row['clipboard_id']));
        }

        $last_id = end($dl);
        $json = json_encode($last_id);
        echo $json;
        $correct = true;
    } else {
        echo "Error: " . $sql . "<br>" . $dbconnect->error;
    }
} else {
    echo "Request failed(Incorrect Input)";
}
$dbconnect->close();
?>
