<?php
require_once "../auth/config.php";

if(isset($_GET['user_string'], $_GET['id'])){

    $user_string = $_GET['user_string'];
    $id = $_GET['id'];

    $sql = mysqli_query($link, "DELETE FROM clipboard WHERE user_string='$user_string' AND clipboard_id='$id'");

    if($sql)
    {
        mysqli_close($link); // Close connection
        header("location:index.php"); // redirects to all records page
        exit;
    }
    else
    {
        echo "Error deleting record"; // display error message if not delete
    }

}
?>
