package jda.test.db.javadb.bugs;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This test file demonstrated a bug in JavaDB which occurs for client/server 
 * mode and for BLOB-based data type.
 * 
 * If this bug is affecting DomainApp, use the patched version of the Java DB library which 
 * fixed this bug.
 */
public class Derby6737 {
  public static void main(String[] args) throws Exception {
    System.setProperty("derby.drda.startNetworkServer", "true");
    Connection c1 = DriverManager.getConnection("jdbc:derby:memory:db;create=true");
    Statement s1 = c1.createStatement();
    s1.execute("create table t(x int, c clob)");
    s1.execute("insert into t(x) values 1,2,3,4,5,6,7");
    s1.execute("update t set c = 'abcdefghi'");

    Connection c2 = DriverManager.getConnection("jdbc:derby://localhost/memory:db");
    Statement s2 = c2.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
    c2.setAutoCommit(false);
    ResultSet rs = s2.executeQuery("select * from t where x = 3");
    System.out.println(rs.last());
    System.out.println(rs.getRow());
    System.out.println(rs.first());
    System.out.println(rs.getInt(1));
    Clob clob = rs.getClob("c");

    // This call fails
    System.out.println(clob.length());
  }
}
