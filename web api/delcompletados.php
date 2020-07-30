<?php
    header('Content-type: application/json');
    include 'dbconnect.php';

    try
    {
        $st = $db -> prepare("DELETE FROM encargos WHERE completado = 1");
        $st->execute();

        $response["success"] = 1;
        $response["message"] = "Encargos eliminados";
    }
    catch (Exception $ex)
    {
        $response["success"] = 0;
        $response["message"] = "Error";
    }

    echo json_encode($response);
?>