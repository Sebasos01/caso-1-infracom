package main.java.util;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Utiles {

    private static int codigoHash(List<String> mensajes){
        List<Integer> temp = new ArrayList<>();
        Pattern pattern = Pattern.compile("^M(\\d+)");
        Matcher matcher = pattern.matcher("");
        for (String mensaje : mensajes) {
            matcher.reset(mensaje);
            if (matcher.find()){
                temp.add(Integer.valueOf(matcher.group(1)));
            }
        }
        temp = temp.stream().sorted().collect(Collectors.toList());
        return temp.hashCode();
    }

    public static boolean checkSum(List<String> mensajes1, List<String> mensajes2){
        return codigoHash(mensajes1) == codigoHash(mensajes2);
    }

}
