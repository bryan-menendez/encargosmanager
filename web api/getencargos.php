<?php
    header('Content-type: application/json');

    include 'dbconnect.php';
    $result = array();
    $encargo = array();
    $response = array();

    try
    {
        $st = $db -> query("SELECT * from encargos");
        $st->setFetchMode(PDO::FETCH_ASSOC);
    
        while($row = $st->fetch())
        {
            $encargo["id"] = $row["id"];
            $encargo["titulo"] = $row["titulo"];
            $encargo["descripcion"] = $row["descripcion"];
            $encargo["precio"] = $row["precio"];
            $encargo["completado"] = $row["completado"];
    
            $result[] = $encargo;
        }

        $response["success"] = 1;
        $response["data"] = $result;
    }
    catch (Exception $ex)
    {
        $response["success"] = 0;
	    $response["message"] = $ex->getMessage();
    }

    echo json_encode($response);
?>