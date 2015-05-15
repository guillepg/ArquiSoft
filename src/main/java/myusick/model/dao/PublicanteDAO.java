package myusick.model.dao;

import myusick.model.connection.ConnectionAdmin;

import java.sql.*;

/**
 * Created by Cuenta de clase on 02/04/2015.
 */
public class PublicanteDAO {

    private Connection con;

    public void setConnection(Connection con){
        try{
            this.con = con;
            this.con.setAutoCommit(false);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public int insertarPublicante(boolean tipo){
        try {
            String query="insert into publicante (tipoPublicante) values (?)";
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setBoolean(1,tipo);
            int insertedRows = ps.executeUpdate();
            if(insertedRows == 1){
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                int uuid = keys.getInt(1);
                con.commit();
                return uuid;
            }else{
                con.rollback();
                return -1;
            }

        } catch (SQLException e) {
            try{
                con.rollback();
            }catch(SQLException e2){e2.printStackTrace();}
            e.printStackTrace();
            return -1;
        }
        
    }

    public String getAvatarById(int id){
        try{
            /* Primero intentamos ver si es una persona */
            String query="select avatar from persona where publicante_uuid = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                /* No es una persona, probamos con grupo */
                con.rollback();
                query = "select avatar from grupo where publicante_uuid = ?";
                ps = con.prepareStatement(query);
                ps.setInt(1,id);
                rs = ps.executeQuery();
                if(!rs.next()){
                    /* este publicante no tiene avatar */
                    con.rollback();
                    return null;
                }
            }
            /* Si llegamos aqui es que hay avatar */
            return rs.getString(1);

        }catch (SQLException e) {
            try{
                con.rollback();
            }catch(SQLException e2){e2.printStackTrace();}
            e.printStackTrace();
            return null;
        }
    }

    public String getNombreById(int id){
        try{
            /* Primero intentamos ver si es una persona */
            String query="select nombre from persona where publicante_uuid = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                /* No es una persona, probamos con grupo */
                con.rollback();
                query = "select nombre from grupo where publicante_uuid = ?";
                ps = con.prepareStatement(query);
                ps.setInt(1,id);
                rs = ps.executeQuery();
                if(!rs.next()){
                    /* este publicante no tiene avatar */
                    con.rollback();
                    return null;
                }
            }
            /* Si llegamos aqui es que hay avatar */
            return rs.getString(1);

        }catch (SQLException e) {
            try{
                con.rollback();
            }catch(SQLException e2){e2.printStackTrace();}
            e.printStackTrace();
            return null;
        }
    }

    public int editarPublicante(int uuid, boolean nuevo_tipo){
        try{
            String query4 = "update publicante set tipoPublicante=? where UUID=?";
            PreparedStatement ps = con.prepareStatement(query4);
            ps.setBoolean(1, nuevo_tipo);
            ps.setInt(2, uuid);
            int resul = ps.executeUpdate();
            return resul;
        }catch(Exception ex){
            ex.printStackTrace();return -1;
        }
    }

    public int borrarPublicante(int uuid){
        try{
            SeguirDAO sdao = new SeguirDAO();
            sdao.setConnection(ConnectionAdmin.getConnection());
            System.out.println("seguidor/seguido: "+sdao.eliminarSeguidorySeguido(uuid));
            sdao.closeConnection();

            String query2 = "delete from publicante where UUID=?";
            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setInt(1, uuid);
            int resul = ps2.executeUpdate();
            return resul;
        }catch(Exception ex){
            ex.printStackTrace();return -1;
        }
    }

    public boolean closeConnection(){
        try {
            con.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
