<?php
$epc = addslashes($_GET['e']);

$con = mysql_connect("localhost","logistics","");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("logistics", $con);

$result = mysql_query("SELECT * from `product` WHERE epc = '$epc'") or die(mysql_error());//mysql_real_escape_string (

$row =  mysql_fetch_assoc($result);
$group = $row['groupid'];

$result = mysql_query("UPDATE `product` SET checkedout = NOW() WHERE epc = '$epc'") or die(mysql_error());//mysql_real_escape_string (

$result = mysql_query("SELECT productid FROM `product` WHERE checkedout IS NULL AND groupid = '$group'") or die(mysql_error());
$c =  mysql_num_rows($result);

if ($c <= 0)
{
	$result = mysql_query("UPDATE `group` SET locked = '1' WHERE groupid = '$group'") or die(mysql_error());//mysql_real_escape_string (
}

mysql_close($con);
?>
