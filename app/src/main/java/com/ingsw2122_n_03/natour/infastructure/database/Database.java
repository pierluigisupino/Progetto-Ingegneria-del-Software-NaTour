package com.ingsw2122_n_03.natour.infastructure.database;

import com.ingsw2122_n_03.natour.BuildConfig;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    private Connection connection;

    private static Database instance = null;

    private final String host = BuildConfig.DATABASE_HOST;
    private final String database = BuildConfig.DATABASE;
    private final int port = Integer.parseInt(BuildConfig.DATABASE_PORT);
    private final String user = BuildConfig.DATABSE_USER;
    private final String pass = BuildConfig.DATABASE_PASSWORD;
    private String url = "jdbc:postgresql://%s:%d/%s";
    private boolean status;

    private Database() {
        this.url = String.format(this.url, this.host, this.port, this.database);
        connect();
    }

    public static Database getInstance() {
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }

    private void connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("org.postgresql.Driver");
                    connection = DriverManager.getConnection(url, user, pass);
                    status = true;
                } catch (Exception e) {
                    status = false;
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            this.status = false;
        }
    }

}
