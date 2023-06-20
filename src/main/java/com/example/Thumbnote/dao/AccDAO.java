package com.example.Thumbnote.dao;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AccDAO {

    public AccDAO(DataSource dataSource) {
        this.dataSource = dataSource;

    }



        private final DataSource dataSource;



        public boolean isAviable(String col, String username) {
            String sql = "SELECT COUNT(*) FROM users WHERE " + col + " = ?";
            try (var connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count == 0;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }

        public static boolean addAccount(String username, String password, String email) {
            String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, email);
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }

        public String getPassword(String username) {
            String sql = "SELECT password_hash FROM users WHERE username = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if(resultSet.next())
                        return resultSet.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        public long getId(String username) {
            String sql = "SELECT user_id FROM users WHERE username = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if(resultSet.next())
                        return resultSet.getLong(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return 0;
        }


        public String getUsername(long userId) {
            String sql = "SELECT username FROM users WHERE user_id = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if(resultSet.next())
                        return resultSet.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
