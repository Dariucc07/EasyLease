package com.easylease.EasyLease.model.DBPool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DBConnectionTest {
  private static DataSource dataSource;
  private DBConnection dbConnection;

  @BeforeAll
  static void init() throws Exception {
    MysqlDataSource mysqlDataSource = new MysqlDataSource();
    mysqlDataSource.setURL("jdbc:mysql://localhost:3306/easylease");
    mysqlDataSource.setUser("root");
    mysqlDataSource.setPassword("master");
    mysqlDataSource.setServerTimezone("UTC");
    mysqlDataSource.setVerifyServerCertificate(false);
    mysqlDataSource.setUseSSL(false);
    dataSource = mysqlDataSource;
  }

  @BeforeEach
  void setUp() {
    dbConnection = DBConnection.getInstance();
    dbConnection.setDataSource(dataSource);
  }

  @Test
  void testConnection() {
    assertNotNull(dbConnection.getConnection());
  }

  @Test
  void testStatement() throws Exception {
    Connection conn = dbConnection.getConnection();
    Statement stm = conn.createStatement();
    assertNotNull(stm);
  }

  @Test
  void testQuery() throws Exception {
    Connection conn = dbConnection.getConnection();
    Statement stm = conn.createStatement();
    stm.execute("SELECT 15 + 5");
    ResultSet rs = stm.getResultSet();
    assertTrue(rs.next());
    assertEquals(20, rs.getInt(1));
  }
}