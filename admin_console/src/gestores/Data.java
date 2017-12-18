package gestores;

import static adminconsole.AdminConsole.*;

public class Data {

    public static models.Data editData(String s1, models.Data data) {
        try {
            r1 = 0;
            do {
                if (r1++ != 0) System.out.print("Por favor insira valores válidos para " + s1 + ".\n");
                System.out.print("Insira o ano d" + s1 + ": ");
                data.setAno(sc.nextInt());
                System.out.print("Insira o mês d" + s1 + ": ");
                data.setMes(sc.nextInt());
                System.out.print("Insira o dia d" + s1 + ": ");
                data.setDia(sc.nextInt());
            } while (!data.test());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static models.Data editHora(String s1, models.Data data) {
        try {
            r1 = 0;
            do {
                if (r1++ != 0) System.out.print("Por favor insira valores válidos para " + s1 + ".\n");
                System.out.print("Insira as horas d" + s1 + ": ");
                data.setHora(sc.nextInt());
                System.out.print("Insira os minutos d" + s1 + ": ");
                data.setMinuto(sc.nextInt());
                System.out.print("Insira os segundo d" + s1 + ": ");
                data.setSegundo(sc.nextInt());
            } while (!data.test());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static models.Data edit(String s1, models.Data data) {
        editData(s1, data);
        editHora(s1, data);
        return data;
    }
}
