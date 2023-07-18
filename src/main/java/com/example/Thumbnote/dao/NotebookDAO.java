package com.example.Thumbnote.dao;


import com.example.Thumbnote.objects.Notebook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class NotebookDAO {
    private final DataSource dataSource;


    public NotebookDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean doesExist(long userId, String notebookName){
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM notebooks WHERE user_id = ? AND notebook_name = ?");
            stmt.setLong(1, userId);
            stmt.setString(2, notebookName);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            stmt.close();

            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addNotebook(Notebook notebook) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO notebooks (user_id, notebook_name,description) VALUES (?, ?,?)");
            stmt.setLong(1, notebook.getUserId());
            stmt.setString(2, notebook.getNotebookName());
            stmt.setString(3,notebook.getDescription());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Notebook getById(long notebookId) {
        Notebook notebook = null;
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM notebooks WHERE notebook_id = ?");
            stmt.setLong(1, notebookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                long userId = rs.getLong("user_id");
                String notebookName = rs.getString("notebook_name");

                String description = rs.getString("description");
                notebook = new Notebook(notebookId,notebookName,description,userId);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notebook;
    }

    public List<Notebook> getAllNotebooks(long userId) {
        List<Notebook> notebooks = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM notebooks WHERE user_id = ?");
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                long notebookId = rs.getLong("notebook_id");
                String notebookName = rs.getString("notebook_name");
                String description = rs.getString("description");
               Notebook  notebook = new Notebook(notebookId,notebookName,description,userId);
                notebooks.add(notebook);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notebooks;
    }






    public boolean updateNotebook(Notebook notebook) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE notebooks SET user_id = ?, notebook_name = ?, description = ? WHERE notebook_id = ?");
            stmt.setLong(1, notebook.getUserId());
            stmt.setString(2, notebook.getNotebookName());
            stmt.setString(3,notebook.getDescription());
            stmt.setLong(4, notebook.getNotebookId());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected>0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteNotebook(long id) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt2 = conn.prepareStatement("update notes set notebook_id=null where notebook_id=?");
            stmt2.setLong(1,id);
            stmt2.executeUpdate();
            stmt2.close();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM notebooks WHERE notebook_id = ?");
            stmt.setLong(1,id);
            int rowsAffected = stmt.executeUpdate();

            stmt.close();
            return rowsAffected>0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}