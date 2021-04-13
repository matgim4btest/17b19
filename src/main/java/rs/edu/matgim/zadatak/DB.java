package rs.edu.matgim.zadatak;

import java.sql.*;
import java.text.SimpleDateFormat;

public class DB {

    String connectionString = "jdbc:sqlite:src\\main\\java\\Banka.db";

    public void printNotUsedRacun() {
        try ( Connection conn = DriverManager.getConnection(connectionString);  Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT * FROM Racun WHERE BrojStavki = 0");
            while (rs.next()) {
                int IdRac = rs.getInt("IdRac");
                String Status = rs.getString("Status");
                int BrojStavki = rs.getInt("BrojStavki");
                int DozvMinus = rs.getInt("DozvMinus");
                int Stanje = rs.getInt("Stanje");
                int IdFil = rs.getInt("IdFil");
                int IdKom = rs.getInt("IdKom");
                System.out.println(String.format("%d\t%s\t%d\t%d\t%d\t%d\t%d", IdRac, Status, BrojStavki, DozvMinus, Stanje, IdFil, IdKom));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    public void prikazSvihRacuna()
    {
        String sql = "SELECT * FROM Racun";
        try ( Connection conn = DriverManager.getConnection(connectionString);  PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            ResultSet rs = ps.executeQuery();
            conn.commit();
            while (rs.next()) {
                int IdRac = rs.getInt("IdRac");
                String Status = rs.getString("Status");
                int BrojStavki = rs.getInt("BrojStavki");
                int DozvMinus = rs.getInt("DozvMinus");
                int Stanje = rs.getInt("Stanje");
                int IdFil = rs.getInt("IdFil");
                int IdKom = rs.getInt("IdKom");
                System.out.println(String.format("%d\t%s\t%d\t%d\t%d\t%d\t%d", IdRac, Status, BrojStavki, DozvMinus, Stanje, IdFil, IdKom));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    public float zadatak(int idFil, int idKom) 
    {
        float suma = 0;
        int idStavka = 0, k=0;
        String sql = "SELECT SUM(-DozvMinus-Stanje) AS Suma FROM Racun WHERE IdKom = ? AND Stanje < -DozvMinus";
        String sql1 = "UPDATE Racun SET Stanje = -DozvMinus, Status = 'A' WHERE IdKom = ? AND Stanje < -DozvMinus";
        String sqlIdSta = "SELECT MAX(IdSta)+1 FROM Stavka";
        String sqlRedBroj = "SELECT BrojStavki+1,-DozvMinus-Stanje,IdRac FROM Racun WHERE IdKom = ? AND Stanje < -DozvMinus";
        
        try ( Connection conn = DriverManager.getConnection(connectionString);  
                PreparedStatement ps = conn.prepareStatement(sql); 
                PreparedStatement ps1 = conn.prepareStatement(sql1);
                PreparedStatement psPom = conn.prepareStatement(sqlIdSta);
                PreparedStatement psRedBroj = conn.prepareStatement(sqlRedBroj);) {
            
            conn.setAutoCommit(false);
            ps.setInt(1, idKom);
            ResultSet rs = ps.executeQuery();
            rs.next();
            suma = rs.getFloat("Suma");
            //suma = rs.getFloat(1); ako indexiramo kolone
            
            
            
            ResultSet rsPom = psPom.executeQuery();
            rsPom.next();
            idStavka = rsPom.getInt(1);
            
            ResultSet rsRedBroj = psRedBroj.executeQuery();
            while(rsRedBroj.next())
            {
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                
                SimpleDateFormat formatter1= new SimpleDateFormat("HH:mm");
                Date time = new Date(System.currentTimeMillis());
                
                String sql2 = "INSERT INTO Stavka(IdSta,RedBroj,Datum,Vreme,Iznos,IdFil,IdRac) VALUES(?,?,?,?,?,?,?)";
                String sqlUplata = "INSERT INTO Uplata(IdSta, Osnov) VALUES (?,?)";
                try (PreparedStatement ps2 = conn.prepareStatement(sql2); PreparedStatement psUplata = conn.prepareStatement(sql2)) {
                ps2.setInt(1, idStavka + k);
                ps2.setInt(2, rsRedBroj.getInt(1));
                ps2.setString(3, formatter.format(date));
                ps2.setString(4, formatter1.format(time));
                ps2.setFloat(5, rsRedBroj.getFloat(2));
                ps2.setInt(6, idFil);
                ps2.setInt(7, rsRedBroj.getInt(3));
                ps2.execute();
                
                psUplata.setInt(1, idStavka + k); k++;
                psUplata.setString(2, "Uplata na zahtev građanina");
                psUplata.execute();
                
                } catch (SQLException ex) {
                    System.out.println("Greska prilikom povezivanja na bazu");
                    System.out.println(ex);
                }
            }
            
            ps1.setInt(1, idKom);
            ps1.executeUpdate();
            
            
            conn.commit();
            System.out.println("Uspešna realizacija.");

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
        
        
        return suma;
    }

}
