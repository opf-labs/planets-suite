package eu.planets_project.pp.plato.run;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;
import org.apache.openejb.OpenEJB;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.jetty.webapp.WebAppContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Start up the web application from within the test context.  Good for debugging.
 *
 * @author Gerald de Jong <geralddejong@gmail.com>
 */

public class PlatoStarter {

    private static Logger LOG = Logger.getLogger(PlatoStarter.class);

    /**
     * @param args if not 8080 then pass port as an integer arg
     * @throws Exception when it goes wrong
     */
    public static void main(String... args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        System.setProperty("openejb.base", "src/test/resources");
        Properties props = new Properties();
        props.put("openejb.jndiname.format","{ejbClass.simpleName}/{interfaceType.annotationName}");
        OpenEJB.init(props);
        LOG.info("Binding data source");
        bindDataSource();
        LOG.info("Binding entity manager");
        bindEntityManager();
        LOG.info("Setting up Jetty server on port " + port);
        Server server = new Server(port);
        server.addHandler(new WebAppContext(getProjectRoot().getAbsolutePath() + "/webapp-plato/src/main/webapp", "/plato"));
        HashUserRealm planetsRealm = new HashUserRealm("PlanetsRealm");
        server.setUserRealms(new UserRealm[]{planetsRealm});
        server.start();
    }

    private static void bindDataSource() throws PropertyVetoException, NamingException, SQLException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("org.hsqldb.jdbcDriver");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        dataSource.setJdbcUrl("jdbc:hsqldb:mem:testdb");
        Connection con = dataSource.getConnection();
        if (con != null) {
            con.close();
            new InitialContext().rebind("java:/platoDatasource", dataSource);
        }
        else {
            throw new SQLException("Connection factory gives no connections!");
        }
    }

    private static void bindEntityManager() throws NamingException {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("platoDatabase");
        new InitialContext().rebind("java:/platoEntityManagerFactory", factory);
    }

    private static File getProjectRoot() throws IOException {
        return getProjectRoot(new File(".").getCanonicalFile());
    }

    private static File getProjectRoot(File here) {
        if (here == null) {
            throw new RuntimeException("Couldn't find root");
        }
        else if (isProjectParentDir(here)) {
            return here;
        }
        else {
            return getProjectRoot(here.getParentFile());
        }
    }

    private static boolean isProjectParentDir(File here) {
        return checkFor(here, "webapp-plato");
    }

    private static boolean checkFor(File here, String... subDirectories) {
        File[] subdirs = here.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        for (String subDirectory : subDirectories) {
            if (!checkFor(subDirectory, subdirs)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkFor(String name, File[] subdirs) {
        if (subdirs == null) return false;

        for (File subdir : subdirs) {
            if (subdir.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}