package server.handler;

import server.Server;
import server.handler.sql.ConnectionService;
import server.handler.sql.QuerySQL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandServer {

    public CommandServer() {
    }

    protected String authInStorage(String user, String password) {
        QuerySQL sql = new QuerySQL();
        if (sql.tryAuthInStorage(ConnectionService.connectMySQL(), user, password)) {
            return new String("Wellcome " + user);
        }
        return new String("Wrong username/password");
    }

    protected String regInStorage(String user, String password, String storage) {
        QuerySQL sql = new QuerySQL();
        if (!sql.isLoginInDb(ConnectionService.connectMySQL(), user)) {
            String addressStorage = storage + "\\" + user;
            if (sql.tryToRegistNewUser(ConnectionService.connectMySQL(), user, password)) {
                try {
                    if (Files.exists(Path.of(addressStorage))) {
                        Files.delete(Path.of(addressStorage));
                    }
                    Files.createDirectories(Path.of(addressStorage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new String("Registration in the system is completed");
            } else {
                return new String("New user registration failed");
            }
        }
        return new String("User " + user + " is already exist");
    }
        protected StringBuilder callHelpManual() throws IOException {
            StringBuilder sb = new StringBuilder();
            InputStream is = Server.class.getClassLoader().getResourceAsStream("help.txt");
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader file = new BufferedReader(isr);
            String line = null; // line read from file
            while ((line = file.readLine()) != null) {
                sb.append(line + "\n\r");
            }
            file.close(); isr.close(); is.close();
            return sb;
        }
    }



