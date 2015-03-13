<?php
$con = mysql_connect("localhost","logistics","");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("logistics", $con);

$result = mysql_query("INSERT INTO `orders` (orderid) VALUES (NULL);") or die(mysql_error());
$result = mysql_query("SELECT LAST_INSERT_ID();") or die(mysql_error());
$row = mysql_fetch_row($result);
$orderid=$row[0];

$groupkey = base64_encode(mt_rand(0,255));
$verificationkey = base64_encode(mt_rand(0,255).mt_rand(0,255).mt_rand(0,255).mt_rand(0,255).mt_rand(0,255).mt_rand(0,255).mt_rand(0,255).mt_rand(0,255));
$result = mysql_query("INSERT INTO `group` (orderid, groupkey, verificationkey) VALUES('$orderid', '$groupkey', '$verificationkey')") or die(mysql_error());
$result = mysql_query("SELECT LAST_INSERT_ID();") or die(mysql_error());
$row = mysql_fetch_row($result);
$groupid = $row[0];
$products = $_POST['product'];
$n = count($products);
for ($i =0; $i < $n; $i++)
{
$productid = $products[$i];
$result = mysql_query("UPDATE `product` SET groupid='$groupid' WHERE groupid IS NULL AND producttypeid = '$productid' LIMIT 1") or die(mysql_error());
}
?>
<html>
<body>
<table>
<h1>Logistics Order Complete</h1>
<h2>Thank you for ordering, here is your information<h2>
	<tr>
		<th style="width:25%">Name</th>
		<th style="width:50%">Description</th>
		<th style="width:50px">Price</th>
	</tr>
<?
$con = mysql_connect("localhost","logistics","");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("logistics", $con);
$total = 0;
$result = mysql_query("SELECT * FROM `producttype` WHERE producttypeid IN (SELECT producttypeid FROM `product` WHERE groupid = '$groupid') ") or die(mysql_error());

while($row = mysql_fetch_array($result))
{
	$total += $row["price"];
    echo "<tr>";
    echo "<td>";
    echo $row["name"];
    echo "</td><td>";
    echo $row["description"];
    echo "</td><td>";
    echo $row["price"];
    echo "</td>";
    echo "</tr>";
}
mysql_free_result($result);
mysql_close($con);
?>
<tr>
<td></td><td></td><td></td><td></td></tr>
<tr><td><strong>Total:</strong></td><td></td><td><? echo $total; ?></td></tr>
</table>
<br/>
<strong>RFID Verification Code:</strong><br />
<img src="http://chart.apis.google.com/chart?cht=qr&chs=120x120&chl=<? echo $groupid; ?>%3Aproject..com%3A<? echo $verificationkey; ?>"><br />
Please print this receipt for your records.
</body>
</html>

