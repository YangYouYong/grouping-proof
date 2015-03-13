<?php
$epc = addslashes($_GET['e']);
$type = addslashes($_GET['t']);
$tagkey = addslashes($_GET['k']);

$con = mysql_connect("localhost","logistics","");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("logistics", $con);

$result = mysql_query("INSERT INTO `product` (`epc`,`tagkey`,`groupid`,`producttypeid`,`checkedout`) VALUES('$epc', '$tagkey', NULL, '$type', NULL); ") or die(mysql_error());//mysql_real_escape_string (

mysql_close($con);
?>
