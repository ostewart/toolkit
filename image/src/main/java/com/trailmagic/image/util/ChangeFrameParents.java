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
import org.apache.log4j.Logger;

public class ChangeFrameParents {
    private static Logger s_log = Logger.getLogger(ChangeFrameParents.class);
    public static final void main(String[] args) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn =
                DriverManager.getConnection("jdbc:mysql://localhost/trailmagic?emulateLocators=true", "trailmagic", "password");

            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            String selectSql =
                "select image_ident.id as image_acl, frame_ident.id as frame_acl, frame_ident.parent_object as frame_parent_acl from images inner join image_frames as frame on images.image_id = frame.image_id inner join acl_object_identity as image_ident on image_ident.object_identity = CONCAT('com.trailmagic.image.Photo:',images.image_id) left join acl_object_identity as frame_ident on frame_ident.object_identity = CONCAT('com.trailmagic.image.ImageFrame:', frame.frame_id)";

            PreparedStatement selectStmt =
                conn.prepareStatement(selectSql);

            String updateSql =
                "UPDATE acl_object_identity set parent_object = ? "
                + "WHERE id = ?";

            PreparedStatement updateStmt =
                conn.prepareStatement(updateSql);

            ResultSet results = selectStmt.executeQuery();

            while (results.next()) {
                updateStmt.setLong(1, results.getLong("image_acl"));
                updateStmt.setLong(2, results.getLong("frame_acl"));
                s_log.info("old parent was: "
                           + results.getLong("frame_parent_acl"));
                updateStmt.execute();
            }
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