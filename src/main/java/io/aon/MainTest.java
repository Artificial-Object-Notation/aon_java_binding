package io.aon;

public class MainTest {

    public static void main(String[] args) {
        String json = """
        {
          "id": 1,
          "nome": "Alice",
          "profile": {
            "enderecos": [
              { "cep": "06114020", "rua": "Rua das Dores" },
              { "cep": "06114021", "rua": "Rua Azul" }
            ],
            "idade": 30
          }
        }
        """;

        System.out.println("==== JSON → AON ====");
        String aon = Aon.jsonToAon(json, "users");
        System.out.println(aon);

        System.out.println("\n==== AON → JSON ====");
        String back = Aon.aonToJson(aon);
        System.out.println(back);
    }
}
