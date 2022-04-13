
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Dates {
    public static void main(String[] args) {
        LocalDateTime fechaIngresada = LocalDateTime.parse("2000-09-13T00:00:00.00");
        String fechaNacimiento = fechaIngresada.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime().toString();
        Instant instant = Instant.parse(fechaNacimiento);
        
        System.out.println(fechaNacimiento);
    }
}
