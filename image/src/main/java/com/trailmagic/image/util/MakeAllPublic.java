/*
 * Copyright (c) 2006 Oliver Stewart.  All Rights Reserved.
 *
 * This file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.trailmagic.image.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MakeAllPublic {
    public static final void main(String[] args) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn =
                DriverManager.getConnection("jdbc:mysql://localhost/trailmagic?emulateLocators=true", "trailmagic", "password");

            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            String selectSql =
                "SELECT id FROM acl_object_identity";

            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            ResultSet results = selectStmt.executeQuery();
            ArrayList<Long> ids = new ArrayList<Long>();
            while (results.next()) {
                ids.add(results.getLong(1));
            }
            System.out.println("got " + ids.size() + " rows");

            String insertSql =
                "insert into acl_permission (acl_object_identity, recipient, mask)"
                + " values (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertSql);

            for (Long id : ids) {
                stmt.setLong(1, id);
                stmt.setString(2, "ROLE_EVERYONE");
                stmt.setInt(3, 2);
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}