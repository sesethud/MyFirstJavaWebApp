import static spark.Spark.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class App {
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    public static void main(String []args){


        port(getHerokuAssignedPort());


        // root is 'src/main/resources', so put files in 'src/main/resources/public'
        staticFiles.location("/public"); // Static files

      //  List <String> names= new ArrayList<>();
        Map<String, Integer> users = new HashMap<>();
      //  get("/hello", (req, res) -> "Hello World");
        get("/greet", (req, res) -> "Hello!");

        get("/hello", (req, res) -> {

            Map<String, Object> map = new HashMap<>();
            return new ModelAndView(map, "hello.handlebars");

        }, new HandlebarsTemplateEngine());

        post("/hello", (req, res) -> {

            Map<String, Object> map = new HashMap<>();
            String username=req.queryParams("username");
            // create the greeting message

            //***
            String greeting = "Hello, " + username;
           // names.add(username);
            if(users.containsKey(username)){
                users.put(username, users.get(username) +1); // if username exist
            }
            else{
                users.put(username, 1);
            }
//            map.put("users",names);
            //**

            // put it in the map which is passed to the template - the value will be merged into the template
            map.put("greeting", greeting);
            map.put("users", users);

            return new ModelAndView(map, "hello.handlebars");

        }, new HandlebarsTemplateEngine());


    }
}
