<?php
$file_path = "uploads/";
$name   = urldecode($_POST['name']);
$image = file_get_contents("$file_path"."$image");
echo "$image";
?>