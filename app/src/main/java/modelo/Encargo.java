package modelo;

import androidx.annotation.NonNull;

public class Encargo {
    private int id;
    private String titulo;
    private String descripcion;
    private int precio;
    private boolean completado;

    public Encargo()
    {
        //blank
    }

    public Encargo(int id, String titulo, String descripcion, int precio, boolean completado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.completado = completado;
    }

    @NonNull
    @Override
    public String toString()
    {
        String str = "id: " + id + " \n name: " + titulo + "\n precio: " + precio;
        return str;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}
