<?php
    header('Content-type: application/json');
    include 'dbconnect.php';

    if (isset($_POST["titulo"]) && isset($_POST["descripcion"]) && isset($_POST["precio"]) &&
        isset($_POST["completado"]))
    {
        $titulo = $_POST["titulo"];
        $descripcion = $_POST["descripcion"];
        $precio = $_POST["precio"];  
        $completado = $_POST["completado"];

        filter_var($titulo, FILTER_SANITIZE_STRING);
        filter_var($descripcion, FILTER_SANITIZE_STRING);
        filter_var($precio, FILTER_SANITIZE_STRING);
        filter_var($completado, FILTER_SANITIZE_STRING);

        $st = $db -> prepare("INSERT INTO encargos VALUES (null, :titulo, :descripcion, :precio, :completado)");

        $st->bindParam(':titulo', $titulo);
        $st->bindParam(':descripcion', $descripcion);
        $st->bindParam(':precio', $precio);
        $st->bindParam(':completado', $completado);

        $st->execute();

        if ($st->rowCount() == 1)
        {
            $response["success"] = 1;
            $response["message"] = "Encargo agregado";
        }
        else
        {
            $response["success"] = 0;
            $response["message"] = "Creacion de nuevo encargo fallido";
        }
    }
    else
    {
        $response["success"] = 0;
        $response["message"] = "Faltan parametros";
    }

    echo json_encode($response);
?>