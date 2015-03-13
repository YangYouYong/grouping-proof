<?php
$groupid = addslashes($_GET['g']);
$sequence = base64_decode(addslashes($_GET['s']));
$verificationkey = addslashes($_GET['v']);

$con = mysql_connect("localhost","logistics","");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("logistics", $con);

$result = mysql_query("SELECT * FROM `group` WHERE groupid = '$groupid'") or die(mysql_error());

 $trip = null;
 if(mysql_num_rows($result)) {
	$row = mysql_fetch_array($result);
	if ($verificationkey == $row["verificationkey"])
	{
		$groupkey = $row["groupkey"];
		$rand = "\0\0\0\0\0\0\0\0";//mt_rand (0, 255);
		$fullhash = sha1($sequence . $rand . $groupkey);
		$m = base64_encode(substr($fullhash, 0, 4));
		$m2 =  base64_encode(substr($fullhash, 4, 8));
		$rand = base64_encode($rand);
		$trip = array('m' => $m, 'm2' => $m2, 'rand' => $rand, 'c' => '2');
	}
 }
 
    header('Content-type: application/json');
    echo json_encode(array('group'=>$trip));
  
mysql_close($con);
?>
