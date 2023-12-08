package org.example;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        int opcion;
        Connection conexion = null;
        Scanner sc = new Scanner(System.in);
        try{
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/empresa", "root", "");
            conexion.setAutoCommit(false);
            while(true){
                System.out.println("-----MENU-----");
                System.out.println("Pulsa 1 - Para agregar un lenguaje de programacion");
                System.out.println("Pulsa 2 - Para mostrar el lenguajes de programación");
                System.out.println("Pulsa 3 - Para ingresar datos de un nuevo empleado");
                System.out.println("Pulsa 4 - Salir");
                opcion = sc.nextInt();

                switch (opcion){
                    case 1:
                        agregarLenguaje(conexion);
                        break;
                    case 2:
                        mostrarLenguaje(conexion);
                        break;
                    case 3:
                        ingresarDatos(conexion);
                        break;
                    case 4:
                        System.out.println("Fin del programa");
                        return;
                    default:
                        System.out.println("Numero introducido erroneo");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close(); //Cerramos la conexión
                }
            } catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public static void agregarLenguaje(Connection connection){
        Scanner sc = new Scanner(System.in);
        String lenguaje;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        try {
            System.out.println("¿Qué lenguaje quieres añadirle?");
            lenguaje = sc.nextLine();

            // Verificar si el lenguaje ya existe
            PreparedStatement checkStmt = connection.prepareStatement("SELECT COUNT(*) FROM lenguaje WHERE nombre = ?");
            checkStmt.setString(1, lenguaje);
            resultSet = checkStmt.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                System.out.println("El lenguaje ya existe. No se puede agregar duplicado.");
            } else {
                // Insertar el lenguaje si no existe
                pstm = connection.prepareStatement("INSERT INTO lenguaje (nombre) VALUES (?)");
                pstm.setString(1, lenguaje);
                pstm.executeUpdate();
                System.out.println("Lenguaje agregado con éxito");
                connection.commit();
            }

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } catch (NumberFormatException exe) {
            exe.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void mostrarLenguaje(Connection conexion){
        try (Statement stmt = conexion.createStatement()) {
            String query = "SELECT * FROM lenguaje";
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()){
                int idLenguaje = rs.getInt("id_lenguaje");
                String nombre = rs.getString("nombre");
                System.out.println("ID " + idLenguaje + " nombre " + nombre);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }catch (NumberFormatException ex){
            ex.printStackTrace();
        }
    }

    public static void ingresarDatos(Connection connection){
        Scanner sc = new Scanner(System.in);
        CallableStatement cstmt = null;

        try {
            System.out.println("Introduce el nombre del empleado");
            String nombre = sc.nextLine();
            System.out.println("Introduce el apellido del usuario");
            String apellido = sc.nextLine();
            String fecha;
            do {
                System.out.println("Introduce la fecha de nacimiento de empleado (DD/MM/YYYY)");
                fecha = sc.nextLine();
                if (!validarFecha(fecha)) {
                    System.out.println("Fecha no válida. Por favor, inténtalo de nuevo.");
                }
            } while (!validarFecha(fecha));

            System.out.println("Introduce el email");
            String email = sc.nextLine();
            System.out.println("Introduce el lenguaje de programacion");
            String lenguaje = sc.nextLine();

            cstmt = connection.prepareCall("{CALL insertar_programador(?, ?, ?, ?, ?, ?)}");
            cstmt.setString(1, nombre);
            cstmt.setString(2, apellido);
            cstmt.setString(3, fecha);
            cstmt.setString(4, email);
            cstmt.setString(5, lenguaje);
            cstmt.registerOutParameter(6, Types.INTEGER);

            cstmt.execute();

            connection.commit();

            int idEmpleado = cstmt.getInt(6);
            System.out.println("Empleado agregado con éxito. ID del empleado: " + idEmpleado);

        }catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }catch (NumberFormatException exe){
            exe.printStackTrace();
        }finally {
            try {
                cstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void borrarEmpleado(Connection connection) {
        PreparedStatement pst = null;
        Scanner sc = new Scanner(System.in);
        String nombre;

        try {
            System.out.println("Cual es el nombre del empleado que quieres borrar");
            nombre = sc.nextLine();
            int idEmpleado = obtenerIdEmpleadoPorNombre(connection, nombre);
            pst = connection.prepareStatement("DELETE FROM empleados WHERE id_empleado = ?");
            pst.setInt(1, idEmpleado);

            int filasBorradas = pst.executeUpdate();

            if (filasBorradas > 0) {
                System.out.println("Se ha eliminado el empleado con ID " + idEmpleado + " de la base de datos.");
            } else {
                System.out.println("No se encontró ningún empleado con ID " + idEmpleado + ".");
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static boolean validarFecha(String fecha) {
        // Establecer el formato de fecha esperado
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);

        try {
            // Intentar parsear la fecha, esto lanzará una excepción si la fecha no es válida
            dateFormat.parse(fecha);
            return true; // La fecha es válida
        } catch (ParseException e) {
            return false; // La fecha no es válida
        }
    }

    public static int obtenerIdEmpleadoPorNombre(Connection connection, String nombre) {
        PreparedStatement pst = null;
        Scanner sc = new Scanner(System.in);
        ResultSet rs = null;
        int idEmpleado = -1; // Valor predeterminado en caso de que no se encuentre el empleado


        try {
            pst = connection.prepareStatement("SELECT id_empleado FROM empleados WHERE nombre = ?");
            pst.setString(1, nombre);

            rs = pst.executeQuery();

            if (rs.next()) {
                idEmpleado = rs.getInt("id_empleado");
            } else {
                System.out.println("No se encontró ningún empleado con el nombre: " + nombre);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return idEmpleado;
    }

    public static void actualizarCampoEmpleado(Connection connection) {
        Scanner sc = new Scanner(System.in);
        PreparedStatement pst = null;
        int id;
        String puesto;

        try {
            System.out.println("Introduce la id del trabajador que quieres updatear");
            id = sc.nextInt();
            System.out.println("Que puesto tiene ahora");
            puesto = sc.nextLine();
            pst = connection.prepareStatement("UPDATE empleados SET puesto = ? WHERE id_empleado = ?");
            pst.setString(1, puesto);
            pst.setInt(2, id);

            int filasActualizadas = pst.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("Se ha actualizado el puesto " + puesto + " del empleado con ID " + id);
            } else {
                System.out.println("No se pudo actualizar");
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}