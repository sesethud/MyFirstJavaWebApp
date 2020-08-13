import static spark.Spark.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
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

        String dbDiskURL = "jdbc:h2:file:./greetdb";
//        String dbMemoryURL = "jdbc:h2:mem:greetdb";

        Jdbi jdbi = Jdbi.create(dbDiskURL, "sa", "");

// get a handle to the database
        Handle handle = jdbi.open();


// create the table if needed
        handle.execute("create table if not exists greet ( id integer identity, name varchar(50), counter int )");


        port(getHerokuAssignedPort());
        // root is 'src/main/resources', so put files in 'src/main/resources/public'

        staticFiles.location("/public"); // Static files
       // List <String> names= new ArrayList<>();
        Map<String, Integer> users = new HashMap<>();
      //  get("/hello", (req, res) -> "Hello World");
        //get("/greet", (req, res) -> "Hello!");
        get("/hello", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            return new ModelAndView(map, "hello.handlebars");
        }, new HandlebarsTemplateEngine());



        get("/",(req,res) ->{

            //get all from the database
            List<String> usersFromDatabase = handle.createQuery("select name from greet")
                    .mapTo(String.class)
                    .list();

            Map<String,Object> map =new HashMap<>();

            map.put("users",usersFromDatabase);
            map.put("counter",usersFromDatabase.size());

            return new ModelAndView(map,"hello.handlebars");

        },new HandlebarsTemplateEngine());


        post("/greet",(req,res)->{
            String username=req.queryParams("username");
            String greeting="";
            if(!username.trim().equals("")){
                greeting=String.format("Hello %s",username);


                //add users to the database
                handle.execute("insert into greet (name) values (?)",username);
            }

            List<String> localUsers=handle.createQuery("select name from greet")
                    .mapTo(String.class)
                    .list();
            Map<String, Object> map = new HashMap<>();
//            handle.execute("insert into greet (name) values(?)",userList);

            map.put("users",localUsers);    // here
            map.put("greeting",greeting);
            return new ModelAndView(map, "hello.handlebars");
        },new HandlebarsTemplateEngine());



        post("/hello", (req, res) -> {

           // Map<String, Integer> users = new HashMap<>();
            Map<String, Object> map = new HashMap<>();
            String username = req.queryParams("username");
            // create the greeting message
            // ***
           // names.add(username);
            if(!username.trim().equals("")  ){//&& users.containsKey(username)){
                //users.put(username, users.get(username) +1); // if username exist
                //handle.execute("insert into greet (name) values (?)",username);
                //

                int count = handle.select("select count(*) from greet where name = ?", username)
                        .mapTo(int.class)
                        .findOnly();
                if (count==0){
                    handle.execute("insert into greet (name) values (?)",username);
                }

            }

           else{
//                //users.put(username, 1);
                handle.execute("insert into greet (name) values (?)",username);
          }
              // Code for radio buttons
          /*  String language=req.queryParams("Language");
            String emptyString="";
            switch (language){
                case "Xhosa":
                    emptyString = "Mholo "+username;
                    break;
                case "English":
                    emptyString ="Hello "+ username;break;
                case "Zulu": emptyString = "Sawubona "+username; break;
                //default:break;
            }
//          map.put("users",names);
            //**
            // put it in the map which is passed to the template - the value will be merged into the template
            map.put("greeting", emptyString);*/

            List<String> videoUsers = handle.createQuery("select name from greet")
                    .mapTo(String.class)
                    .list();


            map.put("users", videoUsers);
            map.put("size",videoUsers.size());


            return new ModelAndView(map, "hello.handlebars");
        }, new HandlebarsTemplateEngine());
    }




}
