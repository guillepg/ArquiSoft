package myusick.model.test;

import myusick.model.dto.GroupDTO;
import myusick.model.dto.ProfileDTO;
import myusick.model.dto.PublicationsDTO;
import myusick.model.dto.RegisterDTO;
import myusick.model.services.MyusickService;
import myusick.persistence.connection.ConnectionAdmin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Cuenta de clase on 04/04/2015.
 */
public class ServiceTest {

    public static void main (String [] args){
        MyusickService m = new MyusickService();
//        List<ProfileDTO> l = m.buscarPorNombre("Foo");
//        if(l == null){
//            System.out.println("Ha salido null");
//        }else
//            System.out.println(l);
        System.out.println(m.eliminarDeGrupo(1,6));


        //m.registrarTag("soyUnaMierda",1);
////        LoginDTO l = m.getLoginData("foo@bar.com","1234");
////        System.out.printf(l.toString());
//        ProfileDTO p = m.getProfileData(1);
//        System.out.println(p.toString());
//        try {
//            Connection c = ConnectionAdmin.getConnection();
//            String query="select publicante_uuid from persona where email=? and password=?";
//            PreparedStatement ps = c.prepareStatement(query);
//            ps.setString(1,"foo@bar.com");
//            ps.setString(2,"1234");
//            ResultSet rs = ps.executeQuery();
//            int i=1;
//            if(rs.next()){
//                System.out.println(rs.getInt(1));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

    }
}
