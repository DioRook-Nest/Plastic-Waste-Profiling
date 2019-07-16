<?php
function execInBackground($cmd) {
    if (substr(php_uname(), 0, 7) == "Windows"){
        pclose(popen("start /B ". $cmd, "r")); 
    }
    else {
        exec($cmd . " > /dev/null &");  
    }
}
?>
<?php 

$input_dir="C:/xampp/htdocs/DeepBlue/upload/input";
$output_dir="C:/xampp/htdocs/DeepBlue/upload/output";
$color_dir="C:/xampp/htdocs/DeepBlue/upload/histogram";
$edge_dir="C:/xampp/htdocs/DeepBlue/upload/edge";
$ocr_dir="C:/xampp/htdocs/DeepBlue/upload/ocr";
$image=$_POST["image"];

//python yolo.py "C:/xampp/htdocs/DeepBlue/upload/input/1803409311_1550287988.jpeg" "C:/xampp/htdocs/DeepBlue/upload/output/1803409311_1550287988.jpeg" "C:/xampp/htdocs/DeepBlue/upload/histogram/1803409311_1550287988.jpeg" "C:/xampp/htdocs/DeepBlue/upload/edge/1803409311_1550287988.jpeg" "C:/xampp/htdocs/DeepBlue/upload/ocr/1803409311_1550287988.jpeg"

if(!file_exists($input_dir)){
	mkdir($input_dir,077,TRUE);
}
if(!file_exists($output_dir)){
	mkdir($output_dir,077,TRUE);
}
if(!file_exists($color_dir)){
	mkdir($color_dir,077,TRUE);
}
if(!file_exists($edge_dir)){
	mkdir($edge_dir,077,TRUE);
}
if(!file_exists($ocr_dir)){
	mkdir($ocr_dir,077,TRUE);
}

$im_name=rand()."_".time().".jpeg";
$input_dir=$input_dir."/".$im_name;
$output_dir=$output_dir."/".$im_name;
$color_dir=$color_dir."/".$im_name;
$edge_dir=$edge_dir."/".$im_name;
$ocr_dir=$ocr_dir."/".$im_name;

$rel="D:/darkflow-master";
#$abs=realpath($rel);
#$cmd='python  D:/darkflow-master/yolo.py'.' '.
$cmd='python  ml_code/darkflow-master/yolo.py'.' '.$input_dir.' '.$output_dir.' '.$color_dir.' '.$edge_dir.' '.$ocr_dir;
$cmd2='python  ml_codes/darkflow-master/main.py'.' '.$input_dir.' '.$output_dir.' '.$color_dir.' '.$edge_dir.' '.$ocr_dir;
if(file_put_contents($input_dir, base64_decode($image))){
	//$msg=array("Message"=>"The file is Uploaded","Status"=>"OK");
	//echo json_encode($msg);

	exec($cmd,$output,$return_var);
	//$o=execInBackground($cmd);

	
	$co=array("Message"=>"http://".$_SERVER['SERVER_ADDR']."/DeepBlue/upload/output"."/".$im_name,"Brands"=>$output);
	echo json_encode($co);
}else{
	$msg=array("Message"=>"Sorry! The file is Not Uploaded","Status"=>$_SERVER['SERVER_ADDR']."/DeepBlue/upload/output"."/".$input_dir);
	echo json_encode($msg);
	
	
}

#$o=execInBackground($cmd2);


?>