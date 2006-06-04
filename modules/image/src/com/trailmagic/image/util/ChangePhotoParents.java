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

public class ChangePhotoParents {
    private static Logger s_log = Logger.getLogger(ChangePhotoParents.class);
    public static final void main(String[] args) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn =
                DriverManager.getConnection("jdbc:mysql://localhost/trailmagic?emulateLocators=true", "trailmagic", "password");

            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            String selectSql =
                "select group_ids.id as group_acl, photos.id as photo_acl, photos.parent_object as photo_parent_acl from acl_object_identity as photos inner join acl_object_identity as frames on photos.parent_object = frames.id inner join image_frames on image_frames.frame_id = substring(frames.object_identity, position(':' IN frames.object_identity) +1) inner join image_groups as groups on groups.group_id = image_frames.group_id inner join acl_object_identity as group_ids on group_ids.object_identity = concat('com.trailmagic.image.ImageGroup:', groups.group_id) where frames.object_identity like 'com.trailmagic.image.ImageFrame%' and photos.object_identity like 'com.trailmagic.image.Photo%' and groups.type = 'roll'";

            PreparedStatement selectStmt =
                conn.prepareStatement(selectSql);

            String updateSql =
                "UPDATE acl_object_identity set parent_object = ? "
                + "WHERE id = ?";

            PreparedStatement updateStmt =
                conn.prepareStatement(updateSql);

            ResultSet results = selectStmt.executeQuery();

            while (results.next()) {
                updateStmt.setLong(1, results.getLong("group_acl"));
                updateStmt.setLong(2, results.getLong("photo_acl"));
                s_log.info("old parent was: "
                           + results.getLong("photo_parent_acl"));
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