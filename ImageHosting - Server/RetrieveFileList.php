<?php
    if ($handle = opendir('./uploads')) {
        while (false !== ($entry = readdir($handle))) {
            if ($entry != "." && $entry != "..") {
                echo "$entry\n";
            }
        }
        closedir($handle);
   }
?>