<html>
<body>
<?php 

// echo $_POST["firstname"];

ini_set('error_reporting', E_ALL);
ini_set('display_errors',1);

require_once 'curl.php';
require_once 'helper.php';

class xmlrpc_client {
    private $url;
    function __construct($url, $autoload=true) {
        $this->url = $url;
        $this->connection = new curl;
        $this->methods = array();
        if ($autoload) {
            $resp = $this->call('system.listMethods', null);
            $this->methods = $resp;
        }
    }
    public function call($method, $params = null) {
        $post = xmlrpc_encode_request($method, $params);
        return xmlrpc_decode($this->connection->post($this->url, $post));
    }
}

//sanitizes text
function sanitize($text) {
    $text = stripslashes($text);
    $text = strip_tags($text);
// 	$text = punct($text);
	$text = preg_replace('/[[:^print:]]/', '', $text);
    return $text;
}

//returns an error sentence if the ratio of punctuation characters to alphanumeric characters is >4
function punct($text){
    $numAlpha = 0;
    preg_match_all("/\w/", $text, $numAlpha);
    $numPunct = 0;
    preg_match_all("/\p{P}\p{S}/", $text, $numPunct);
    if ( (count($numAlpha[0])+1)/(count($numPunct[0])+1) < 4.0 ){
        $text = "Too much punctuation, please try again.";
    }
    return $text; 
}

// $data  = file_get_contents("php://input");
echo $_POST['firstname'];
// $data  = json_decode($data, TRUE);
// echo $data;
// $text  = sanitize($data['firstname']);
// echo $text;


$rpc = "http://localhost:8082";

$client = new xmlrpc_client($rpc, true);

$resp = $client->call('sample.rain', array($_POST['firstname']));

echo $resp;

//$jsonres = json_decode(sanitize_text($resp));

//echo json_encode($jsonres);
?>

</body>
</html>
