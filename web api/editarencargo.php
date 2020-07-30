<?php
    header('Content-type: application/json');
    include 'dbconnect.php';

    if (isset($_POST["id"]) && isset($_POST["titulo"]) && isset($_POST["descripcion"]) && isset($_POST["precio"]) &&
        isset($_POST["completado"]))
    {
        $id = $_POST["id"];
        $titulo = $_POST["titulo"];
        $descripcion = $_POST["descripcion"];
        $precio = $_POST["precio"];  
        $completado = $_POST["completado"];

        filter_var($id, FILTER_SANITIZE_STRING);
        filter_var($titulo, FILTER_SANITIZE_STRING);
        filter_var($descripcion, FILTER_SANITIZE_STRING);
        filter_var($precio, FILTER_SANITIZE_STRING);
        filter_var($completado, FILTER_SANITIZE_STRING);

        $st = $db -> prepare("UPDATE encargos SET titulo = :titulo, descripcion = :descripcion, precio = :precio, completado = :completado WHERE id = :id");

        $st->bindParam(':id', $id);
        $st->bindParam(':titulo', $titulo);
        $st->bindParam(':descripcion', $descripcion);
        $st->bindParam(':precio', $precio);
        $st->bindParam(':completado', $completado);

        $st->execute();

        if ($st->rowCount() == 1)
        {
            $response["success"] = 1;
            $response["message"] = "Encargo modificado";
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