<?php
$o = addslashes($_GET['o']);
$con = mysql_connect("localhost","logistics","");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("logistics", $con);

 $result = mysql_query("SELECT * FROM `product` WHERE groupid = '$o'") or die(mysql_error());
 $products = array();
 if(mysql_num_rows($result)) {
	while($r = mysql_fetch_assoc($result)) {
    		$products[] = $r;
	}
 }
 
 header('Content-type: application/json');
 echo json_encode(array('products'=>$products));
  
mysql_close($con);
?>

