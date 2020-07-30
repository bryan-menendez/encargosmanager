package modelo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

public class EncargoDAO
{
    public static ArrayList<Encargo> lista = new ArrayList<>();

    public ArrayList<Encargo> getEncargos()
    {
        ArrayList<Encargo> lista = new ArrayList<>();

        //TODO

        return lista;
    }

    public static void addEncargo(Encargo e)
    {
        lista.add(e);
    }

    public static void delEncargo (Encargo e)
    {
        lista.remove(e);
    }

    public static void delEncargo (int id)
    {
        for (Encargo e : lista){
            if (e.getId() == id)
            {
                lista.remove(e);
                break;
            }
        }
    }

    public static void delPosEncargo (int pos)
    {
        lista.remove(pos);
    }

    public static Encargo getEncargo(int id)
    {
        for (Encargo e : lista){
            if (e.getId() == id)
            {
                return e;
            }
        }

        return null;
    }

    public static Encargo getPosEncargo(int pos)
    {
        return lista.get(pos);
    }

    public static void modEncargo (Encargo nuevo)
    {
        //get old encargo and remove it
        lista.remove(getEncargo(nuevo.getId()));
        //add new
        lista.add(nuevo);
    }

    public static void modPosEncargo (Encargo nuevo, int pos)
    {
        lista.set(pos, nuevo);
    }

}
