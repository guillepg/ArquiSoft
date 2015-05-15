package myusick.model.dao;

import myusick.controller.dto.*;
import myusick.model.vo.Persona;
import myusick.model.connection.ConnectionAdmin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cuenta de clase on 02/04/2015.
 */
public class PersonaDAO {

    private Connection con;

    public void setConnection(Connection con){
        try{
            this.con = con;
            this.con.setAutoCommit(false);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Persona getDataProfile(int uuid){
        try{
            String queryString = "select nombre,descripcion,avatar from persona where publicante_uuid = ?";
            PreparedStatement preparedStatement = con.prepareStatement(queryString);
            preparedStatement.setInt(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return new Persona(uuid,resultSet.getString("nombre"),null,null,0,null,null,0,
                        resultSet.getString("descripcion"),null,resultSet.getString("avatar"));
            else return null;
        }catch (Exception e) {
            try{
                con.rollback();
            }catch(SQLException e2){e2.printStackTrace();}
            e.printStackTrace();
            return null;
        }
    }
    public boolean esUnaPersona(int uuid){
        try {
            String queryString = "select tipoPublicante from publicante where uuid=?";
            PreparedStatement preparedStatement = con.prepareStatement(queryString);
            preparedStatement.setInt(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                con.commit();
                return resultSet.getBoolean(1)==false;
            }
            else{
                con.rollback();
                return false;
            }
        }catch(SQLException e){
            try{
                con.rollback();
            }catch(SQLException e2){e2.printStackTrace();}
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<PublisherDTO> getGroupsByMember(int member){
        ArrayList<PublisherDTO> result = new ArrayList<>();
        try{
            String queryString = "select Publicante_UUID,nombre from Grupo where publicante_uuid " +
                    "in (select uuid_g from es_integrante where uuid_p=?)";
            PreparedStatement preparedStatement = con.prepareStatement(queryString);
            preparedStatement.setInt(1, member);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                result.add(new PublisherDTO(resultSet.getInt(1),resultSet.getString(2)));
            }
            con.commit();
            return result;
        }catch (Exception e) {
            try{
                con.rollback();
            }catch(SQLException e2){e2.printStackTrace();}
            e.printStackTrace();
            return null;
        }
    }

    public LoginDTO getLoginData(String email, String password){
        LoginDTO l = new LoginDTO();
        l.setUser(email);
        l.setPassword(password);
        try {
            String query="select publicante_uuid from persona where email=? and password=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            if(rs.next()){
                l.setUserId(rs.getInt(1));
                l.setGroups(getGroupsByMember(l.getUserId()));
                con.commit();
                return l;
            }else{
                /* El usuario no existe */
                con.rollback();
                return null;
            }
        } catch (SQLException e) {
            try{
                con.rollback();
            }catch(SQLException e2){e2.printStackTrace();}
            e.printStackTrace();
            return new LoginDTO();
        }
    }
    
    public int registerUser(RegisterDTO rd) {
        try {
            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            int uuid = pdao.insertarPublicante(false);
            if (uuid != -1) {
                String query = "insert into persona (Publicante_UUID,nombre,apellidos,email,password," +
                        "fechaNacimiento,ciudad,pais,telefono) values (?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, uuid);
                ps.setString(2, rd.getName());
                ps.setString(3, rd.getLastname());
                ps.setString(4, rd.getEmail());
                ps.setString(5, rd.getPassword());
                ps.setLong(6,Long.parseLong(rd.getBirthdate()));
                ps.setString(7, rd.getCity());
                ps.setString(8, rd.getCountry());
                ps.setString(9, rd.getPhone());
                int insertedRows = ps.executeUpdate();
                if (insertedRows == 1) {
                    pdao.closeConnection();
                    con.commit();
                    return uuid;
                }else{
                    pdao.closeConnection();
                    con.rollback();
                    return -1;
                }
            } else {
                pdao.closeConnection();
                con.rollback();
                return -1;
            }
        } catch (Exception e) {
            try{
                con.rollback();
            }catch(SQLException e2){e2.printStackTrace();}
            e.printStackTrace();
            return -1;
        }        
    }

    public List<ShortProfileDTO> buscarPorNombre(String nombre){
        List<ShortProfileDTO> resultado = new ArrayList<>();
        try{
            String query = "select Publicante_uuid, nombre, avatar from persona where nombre like ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, "%"+nombre+"%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int uuid = rs.getInt(1);
                ShortProfileDTO perfil = new ShortProfileDTO(uuid,rs.getString("nombre"),rs.getString("avatar"),false);
                resultado.add(perfil);
            }
            return resultado;
        }catch (Exception e) {
            try{
                con.rollback();
            }catch(SQLException e2){e2.printStackTrace();}
            e.printStackTrace();
            return null;
        }
    }

    public List<ShortProfileDTO> buscarPorTag(String tag){
        List<ShortProfileDTO> resultado = new ArrayList<>();
        try{
            String query = "select nombre,avatar, Publicante_UUID from persona where publicante_uuid in(" +
                    "  select UUID_P from persona_tiene_tag where idTag in (" +
                    "    select idTag from tag where nombreTag=?" +
                    "))";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, tag);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int uuid = rs.getInt("Publicante_UUID");
                ShortProfileDTO perfil = new ShortProfileDTO(uuid,rs.getString("nombre"),rs.getString("avatar"),false);
                resultado.add(perfil);
            }
            return resultado;
        }catch (Exception e) {
            try{
                con.rollback();
            }catch(SQLException e2){e2.printStackTrace();}
            e.printStackTrace();
            return null;
        }
    }

    public List<ShortProfileDTO> buscarPorAptitud(String aptitud){
        List<ShortProfileDTO> resultado = new ArrayList<>();
        try{
            String query = "select nombre, avatar, Publicante_UUID from persona where publicante_uuid in(" +
                    "  select UUID_P from tiene_aptitud where idAptitud in (" +
                    "    select idAptitud from aptitud where nombre=?" +
                    "))";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, aptitud);
            ResultSet rs = ps.executeQuery();
            AptitudDAO adao = new AptitudDAO(); adao.setConnection(ConnectionAdmin.getConnection());
            TagDAO tdao = new TagDAO(); tdao.setConnection(ConnectionAdmin.getConnection());
            PublicacionDAO pdao = new PublicacionDAO(); pdao.setConnection(ConnectionAdmin.getConnection());
            while(rs.next()){
                int uuid = rs.getInt("Publicante_UUID");
                ShortProfileDTO perfil = new ShortProfileDTO(uuid,rs.getString("nombre"),rs.getString("avatar"),false);
                resultado.add(perfil);
            }
            adao.closeConnection();
            tdao.closeConnection();
            pdao.closeConnection();
            return resultado;
        }catch (Exception e) {
            try{
                con.rollback();
            }catch(SQLException e2){e2.printStackTrace();}
            e.printStackTrace();
            return null;
        }
    }

    public boolean setNombre(int UUID, String nombre){
        try {
            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            if(nombre.length()>20 || nombre.length()==0) return false;
            if (UUID != -1) {
                String query = "update persona set nombre=? where Publicante_UUID=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, nombre);
                ps.setInt(2, UUID);
                int alteredRows = ps.executeUpdate();
                if (alteredRows == 1) {
                    pdao.closeConnection();
                    con.commit();
                    return true;
                }else{
                    pdao.closeConnection();
                    con.rollback();
                    return false;
                }
            } else {
                pdao.closeConnection();
                con.rollback();
                return false;
            }
        }catch(Exception ex){
            return false;
        }
    }

    public boolean setApellidos(int UUID, String ap){
        try {
            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            if(ap.length()>60 || ap.length()==0) return false;
            if (UUID != -1) {
                String query = "update persona set apellidos=? where Publicante_UUID=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, ap);
                ps.setInt(2, UUID);
                int alteredRows = ps.executeUpdate();
                if (alteredRows == 1) {
                    pdao.closeConnection();
                    con.commit();
                    return true;
                }else{
                    pdao.closeConnection();
                    con.rollback();
                    return false;
                }
            } else {
                pdao.closeConnection();
                con.rollback();
                return false;
            }
        }catch(Exception ex){
            return false;
        }
    }

    public boolean setAvatar(int UUID, String url){
        try {
            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            if(url.length()>100 || url.length()==0) return false;
            if (UUID != -1) {
                String query = "update persona set avatar=? where Publicante_UUID=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, url);
                ps.setInt(2, UUID);
                int alteredRows = ps.executeUpdate();
                if (alteredRows == 1) {
                    pdao.closeConnection();
                    con.commit();
                    return true;
                }else{
                    pdao.closeConnection();
                    con.rollback();
                    return false;
                }
            } else {
                pdao.closeConnection();
                con.rollback();
                return false;
            }
        }catch(Exception ex){
            return false;
        }
    }

    public boolean setEmail(int UUID, String mail){
        try {
            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            if(mail.length()>60 || mail.length()==0) return false;
            if (UUID != -1) {
                String query = "update persona set email=? where Publicante_UUID=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, mail);
                ps.setInt(2, UUID);
                int alteredRows = ps.executeUpdate();
                if (alteredRows == 1) {
                    pdao.closeConnection();
                    con.commit();
                    return true;
                }else{
                    pdao.closeConnection();
                    con.rollback();
                    return false;
                }
            } else {
                pdao.closeConnection();
                con.rollback();
                return false;
            }
        }catch(Exception ex){
            return false;
        }
    }

    public boolean setPassword(int UUID, String pass){
        try {
            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            if(pass.length()>20 || pass.length()==0) return false;
            if (UUID != -1) {
                String query = "update persona set pass=? where Publicante_UUID=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, pass);
                ps.setInt(2, UUID);
                int alteredRows = ps.executeUpdate();
                if (alteredRows == 1) {
                    pdao.closeConnection();
                    con.commit();
                    return true;
                }else{
                    pdao.closeConnection();
                    con.rollback();
                    return false;
                }
            } else {
                pdao.closeConnection();
                con.rollback();
                return false;
            }
        }catch(Exception ex){
            return false;
        }
    }

    public boolean setNacimiento(int UUID, int nac){
        try {
            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            if (UUID != -1) {
                String query = "update persona set nacimiento=? where Publicante_UUID=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, nac);
                ps.setInt(2, UUID);
                int alteredRows = ps.executeUpdate();
                if (alteredRows == 1) {
                    pdao.closeConnection();
                    con.commit();
                    return true;
                }else{
                    pdao.closeConnection();
                    con.rollback();
                    return false;
                }
            } else {
                pdao.closeConnection();
                con.rollback();
                return false;
            }
        }catch(Exception ex){
            return false;
        }
    }

    public boolean setCiudad(int UUID, String city){
        try {
            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            if(city.length()>45 || city.length()==0) return false;
            if (UUID != -1) {
                String query = "update persona set ciudad=? where Publicante_UUID=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, city);
                ps.setInt(2, UUID);
                int alteredRows = ps.executeUpdate();
                if (alteredRows == 1) {
                    pdao.closeConnection();
                    con.commit();
                    return true;
                }else{
                    pdao.closeConnection();
                    con.rollback();
                    return false;
                }
            } else {
                pdao.closeConnection();
                con.rollback();
                return false;
            }
        }catch(Exception ex){
            return false;
        }
    }

    public boolean setPais(int UUID, String pais){
        try {
            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            if(pais.length()>45 || pais.length()==0) return false;
            if (UUID != -1) {
                String query = "update persona set pais=? where Publicante_UUID=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, pais);
                ps.setInt(2, UUID);
                int alteredRows = ps.executeUpdate();
                if (alteredRows == 1) {
                    pdao.closeConnection();
                    con.commit();
                    return true;
                }else{
                    pdao.closeConnection();
                    con.rollback();
                    return false;
                }
            } else {
                pdao.closeConnection();
                con.rollback();
                return false;
            }
        }catch(Exception ex){
            return false;
        }
    }

    public boolean setTelefono(int UUID, int tel){
        try {
            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            if (UUID != -1) {
                String query = "update persona set telefono=? where Publicante_UUID=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, tel);
                ps.setInt(2, UUID);
                int alteredRows = ps.executeUpdate();
                if (alteredRows == 1) {
                    pdao.closeConnection();
                    con.commit();
                    return true;
                }else{
                    pdao.closeConnection();
                    con.rollback();
                    return false;
                }
            } else {
                pdao.closeConnection();
                con.rollback();
                return false;
            }
        }catch(Exception ex){
            return false;
        }
    }

    public boolean setDescripcion(int UUID, String descr){
        try {
            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            if(descr.length()>144 || descr.length()==0) return false;
            if (UUID != -1) {
                String query = "update persona set descripcion=? where Publicante_UUID=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, descr);
                ps.setInt(2, UUID);
                int alteredRows = ps.executeUpdate();
                if (alteredRows == 1) {
                    pdao.closeConnection();
                    con.commit();
                    return true;
                }else{
                    pdao.closeConnection();
                    con.rollback();
                    return false;
                }
            } else {
                pdao.closeConnection();
                con.rollback();
                return false;
            }
        }catch(Exception ex){
            return false;
        }
    }

    public boolean borrarPersona(int uuid){
        try {
            String query1 = "delete from persona_tiene_tag where UUID_P=?";
            PreparedStatement ps1 = con.prepareStatement(query1);
            ps1.setInt(1, uuid);
            int eliminadas_relacion1 = ps1.executeUpdate();

            String query2 = "delete from tiene_aptitud where UUID_P=?";
            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setInt(1, uuid);
            int eliminadas_relacion2 = ps2.executeUpdate();

            String query3 = "delete from pendiente_aceptacion where persona=?";
            PreparedStatement ps3 = con.prepareStatement(query3);
            ps3.setInt(1, uuid);
            int eliminadas_relacion3 = ps3.executeUpdate();

            String query4 = "delete from es_integrante where UUID_P=?";
            PreparedStatement ps4 = con.prepareStatement(query4);
            ps4.setInt(1, uuid);
            int eliminadas_relacion4 = ps4.executeUpdate();

            String query5 = "delete from persona where Publicante_UUID=?";
            PreparedStatement ps5 = con.prepareStatement(query5);
            ps5.setInt(1, uuid);
            int eliminadas_relacion5 = ps5.executeUpdate();

            System.out.println("1: "+eliminadas_relacion1);
            System.out.println("2: "+eliminadas_relacion2);
            System.out.println("3: "+eliminadas_relacion3);
            System.out.println("4: "+eliminadas_relacion4);
            System.out.println("persona: " + eliminadas_relacion5);

            con.commit();

            PublicanteDAO pdao = new PublicanteDAO();
            pdao.setConnection(ConnectionAdmin.getConnection());
            int eliminadas_entidad = pdao.borrarPublicante(uuid);
            pdao.closeConnection();

            System.out.println("publicante:" +eliminadas_entidad);

            if (eliminadas_entidad == 1) {
                con.commit();
                return true;
            }else{
                con.rollback();
                return false;
            }
        }catch(Exception ex){
            ex.printStackTrace();return false;
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
