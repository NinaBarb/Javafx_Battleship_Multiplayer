/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.utils;

import hr.algebra.controller.GameViewController;
import hr.algebra.model.JNDIInfo;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 *
 * @author Marija
 */
public class JndiUtils {

    public JndiUtils() {
    }

    private static final String FILE_LOCATION = "c:\\konfig\\";
    private static final String SERVER_PORT = "server.port";
    private static final String SERVER_NAME = "server.name";
    private static final String SERVER_REGISTRY = "server.registry";

    public static JNDIInfo getConfigurationInfo() {
        Hashtable environment = new Hashtable();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        environment.put(Context.PROVIDER_URL, "file:" + FILE_LOCATION);
        JNDIInfo info = new JNDIInfo();

        try {
            Context context = new InitialContext(environment);

            NamingEnumeration enumeration = context.listBindings("");

            List<String> fileNames = new ArrayList<>();

            while (enumeration.hasMore()) {
                Binding file = (Binding) enumeration.next();
                fileNames.add(file.getName());
            }

            String foundFile = fileNames
                    .stream()
                    .filter(f -> f.equals("konfiguracija.properties"))
                    .findFirst()
                    .get();

            Properties appProps = new Properties();
            appProps.load(new FileInputStream(FILE_LOCATION
                    + foundFile));

            info.setPort(appProps.getProperty(SERVER_PORT));
            info.setName(appProps.getProperty(SERVER_NAME));
            info.setRegistry(appProps.getProperty(SERVER_REGISTRY));

        } catch (NamingException | IOException ex) {
            Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }
}
