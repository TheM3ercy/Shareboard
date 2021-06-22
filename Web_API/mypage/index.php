<?php
session_start();
require_once "../auth/config.php";

if ($_SESSION["loggedin"] != true) {
    header("Location: https://www.omnic-systems.com/shareboard/auth/login.php");
    exit;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (isset($_POST['so'])) {
        session_destroy();
        header("Location: https://www.omnic-systems.com/shareboard/auth/login.php");
        exit;
    } elseif (isset($_POST['dae'])) {
        $sql = "DELETE FROM clipboard WHERE user_string=?";
        if ($stmt = mysqli_prepare($link, $sql)) {
            mysqli_stmt_bind_param($stmt, "s", $_SESSION["user_string"]);
            if (mysqli_stmt_execute($stmt)) {
                $secondsWait = 0;
                header("Refresh:$secondsWait");
                echo "Deleting was successful.";
                exit;
            } else {
                echo "Something went wrong. Please try again later.";
            }
            mysqli_stmt_close($stmt);
        } else {
            echo "Something went wrong. Please try again later.";
            echo $stmt->error;
        }
        mysqli_close($link);
    } elseif (isset($_POST['da'])) {
        $sql = "DELETE FROM clipboard WHERE user_string=?";
        if ($stmt = mysqli_prepare($link, $sql)) {
            mysqli_stmt_bind_param($stmt, "s", $_SESSION["user_string"]);
            if (mysqli_stmt_execute($stmt)) {
                echo "Deleting was successful.";
            } else {
                echo "Something went wrong. Please try again later.";
            }
        } else {
            echo "Something went wrong. Please try again later.";
        }
        $sql = "DELETE FROM users WHERE id=?";
        if ($stmt = mysqli_prepare($link, $sql)) {
            mysqli_stmt_bind_param($stmt, "s", $_SESSION["id"]);
            if (mysqli_stmt_execute($stmt)) {
                session_destroy();
                header("Location: https://www.omnic-systems.com/shareboard/auth/login.php");
            } else {
                echo "Something went wrong. Please try again later.";
            }
        } else {
            echo "Something went wrong. Please try again later.";
        }
        mysqli_close($link);
    }
}
?>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8">
    <title>My Page | Shareboard</title>
    <link rel="stylesheet" href="../css/mypage.css?v=3.4">
</head>
<body>
<div class="header">
    <div class="logo">
        <h1>Shareboard</h1>
    </div>
    <div class="settings">
        <button onclick="dropdown()" class="dropbtn"><?php echo $_SESSION["username"] ?></button>
        <div class="dropdiv">
            <div id="dropdownDiv" class="dropdown-content settings">
                <form method="POST">
                    <input type="submit" name="dae" value="Delete all Entries">
                    <input type="submit" name="so" value="Sign out">
                    <input type="submit" name="da" value="Delete Account">
                </form>
            </div>
        </div>
    </div>
</div>
<div class="content">
    <div class="content-description">
        <p>Below you see your Items, that are stored in Shareboard.</p>
    </div>
    <div>
        <?php
        $user_string = $_SESSION['user_string'];
        $sql = "SELECT clipboard_id, clipboard_content, upload_date FROM clipboard WHERE user_string='$user_string'";
        if (!($tb = mysqli_query($link,"$sql"))){
            echo "Failed to retieve data from db.";
        }else{
        ?>
        <table class="styled-table">
            <thead>
            <tr>
                <th width="50%">Content</th>
                <th width="40%">Upload Date</th>
                <th width="10%"></th>
            </tr>
            </thead>
            <tbody>
            <?php
            if (mysqli_num_rows($tb) == 0) {
                echo '<tr><td colspan="4">No Rows stored in Shareboard.</td></tr>';
            } else {
                while ($row = mysqli_fetch_assoc($tb)) {
                    $id = $row['clipboard_id'];
                    echo "<tr><td>{$row['clipboard_content']}</td><td>{$row['upload_date']}</td><td><a href='remove.php?user_string=$user_string&id=$id'>remove</a></td></tr>\n";
                }
            }
            }
            ?>
            </tbody>
        </table>
    </div>
</div>
<script type="text/javascript">
    function dropdown() {
        document.getElementById("dropdownDiv").classList.toggle("show");
            }
</script>
</body>
</html>

