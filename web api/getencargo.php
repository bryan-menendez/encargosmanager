<?php
    header('Content-type: application/json');

    include 'dbconnect.php';
    $encargo = array();
    $response = array();

    if (isset($_GET['id']))
    {
        try
        {
            $st = $db -> prepare("SELECT * FROM encargos WHERE id = :id");
            $st->setFetchMode(PDO::FETCH_ASSOC);
            $st->bindValue(":id", $_GET['id']);
            $st->execute();
        
            if ($row = $st->fetch())
            {
                $encargo["titulo"] = $row["titulo"];
                $encargo["descripcion"] = $row["descripcion"];
                $encargo["precio"] = $row["precio"];
                $encargo["completado"] = $row["completado"];
        
                $response["success"] = 1;
                $response["data"] = $encargo;
            }
            else
            {
                $response["success"] = 0;
                $response["message"] = "Encargo no encontrado";
            }
        }
        catch (Exception $ex)
        {
            $response["success"] = 0;
            $response["message"] = $ex->getMessage();
        }
    }
    else
    {
        $response["success"] = 0;
        $response["message"] = "Faltan parametros";
    }
    
    echo json_encode($response);
?>