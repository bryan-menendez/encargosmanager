<?php
    header('Content-type: application/json');
    include 'dbconnect.php';

    if (isset($_POST["id"]))
    {
        $id = $_POST["id"];

        filter_var($id, FILTER_SANITIZE_STRING);

        $st = $db -> prepare("DELETE FROM encargos WHERE id = :id");
        $st->bindParam(':id', $id);

        $st->execute();

        if ($st->rowCount() == 1)
        {
            $response["success"] = 1;
            $response["message"] = "Encargo eliminado";
        }
        else
        {
            $response["success"] = 0;
            $response["message"] = "No se encontro el encargo solicitado";
        }
    }
    else
    {
        $response["success"] = 0;
        $response["message"] = "Faltan parametros";
    }

    echo json_encode($response);
?>