package rs.edu.matgim.zadatak;

public class Program {
//Ponovo poslato.
    public static void main(String[] args) {

        DB _db = new DB();
        _db.printNotUsedRacun();
        _db.prikazSvihRacuna();
        System.out.println(_db.zadatak(5, 3));
        _db.prikazSvihRacuna();
        
    }
}
 