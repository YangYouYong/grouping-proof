<html>
<body>
<form method="post" action="order.php">
<table>
<h1>Logistics Order Form</h1>
	<tr>
		<th></th>
		<th style="width:25%">Name</th>
		<th style="width:50%">Description</th>
		<th style="width:50px">Stock</th>
		<th style="width:50px">Price</th>
	</tr>
<?php
$con = mysql_connect("localhost","logistics","");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }

mysql_select_db("logistics", $con);

$result = mysql_query("SELECT t1.producttypeid, t1.name, t1.description, t1.price, COALESCE(t2.stock,0) as stock FROM `producttype` AS t1 LEFT JOIN (SELECT producttypeid, COUNT(producttypeid) AS stock FROM `product` WHERE groupid IS NULL GROUP BY producttypeid) AS t2 ON t1.producttypeid = t2.producttypeid ") or die(mysql_error());

while($row = mysql_fetch_array($result))
{
    echo "<tr>";
    echo "<td><input type='checkbox' name='product[]' value='";
    echo $row["producttypeid"];
    echo "'></td><td>";
    echo $row["name"];
    echo "</td><td>";
    echo $row["description"];
    echo "</td><td>";
    echo $row["stock"];
    echo "</td><td>";
    echo $row["price"];
    echo "</td>";
    echo "</tr>";
}
mysql_free_result($result);
mysql_close($con);
?>
</table>
<input type="submit" value="Order"><br />
</form>
</body>
</html>

