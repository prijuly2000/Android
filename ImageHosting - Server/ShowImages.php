
<html>
<body>
<form>
<?php
$dir='uploads/';
$file_display = array('jpg', 'jpeg', 'png', 'gif');

if (file_exists($dir) == false) 
{
   echo 'Directory \''. $dir. '\' not found!';
} 
else 
{
   $dir_contents = scandir($dir);
   foreach ($dir_contents as $file) 
   {
   	$file_type = strtolower(end(explode('.', $file)));
	if ($file !== '.' && $file !== '..' && in_array($file_type, $file_display) == true)     
	{
            echo '<input type="checkbox" class="files" name="CheckBoxes[]" value="'.$file.'"><img src="'.$dir.'/'.$file.'"height="50"width="50" />';
     	}
   }
}
?>
<input type="button" value="hello" onclick="categoryOnClick()"/>

<script type="text/javascript">
function categoryOnClick()
{
	
	var inputElements = document.getElementsByTagName('input');
	for(var i=0; inputElements[i]; ++i)
	{
      		if(inputElements[i].className==="files" && inputElements[i].checked)
		{
           		Android.receiveValueFromJs(inputElements[i].value);
           		
      		}
	}
	Android.download();
	
	
}
</script>

</form>
</body>
</html>