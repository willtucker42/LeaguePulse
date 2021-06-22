<?php
$servername = "reddit-database.cx9mmdexfpd7.us-west-1.rds.amazonaws.com";
$username = "willtucker42";
$password = "Createaccou1090";
$dbname = "reddit-database";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT title,twitter_id,reddit_id,twitter_handle,twitter_name,created_utc,permalink,self_text,trending_level,url,twitter_media_url,media_type,user_profile_pic_url FROM TrendingRedditTwitter ORDER BY created_utc DESC LIMIT 1500";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // output data of each row
	$rows = array();
    while($row = $result->fetch_assoc()) {
		$rows[] = $row;
    }
} else {
    echo "0 results";
}
echo json_encode($rows);
$conn->close();
?>