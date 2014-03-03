package mydao;

import static mydao.DAOUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mymodel.User;

/**
 * This class represents a SQL Database Access Object for the {@link User} DTO. This DAO should be
 * used as a central point for the mapping between the User DTO and a SQL database.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public final class UserDAO {

    // Constants ----------------------------------------------------------------------------------

    private static final String SQL_FIND_BY_ID =
        "SELECT id, username, password, email, age FROM user WHERE id = ?";
    private static final String SQL_FIND_BY_USERNAME_AND_PASSWORD =
        "SELECT id, username, password, email, age FROM user WHERE username = ? AND password = ?";
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT id, username, password, email, age FROM user ORDER BY id";
    private static final String SQL_INSERT =
        "INSERT INTO user (username, password, email, age) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE user SET username = ?, password = ?, email = ?, age = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM user WHERE id = ?";
    private static final String SQL_EXIST_USERNAME =
        "SELECT id FROM user WHERE username = ?";
    private static final String SQL_EXIST_EMAIL =
        "SELECT id FROM user WHERE email = ?";
    private static final String SQL_FIND_BY_USERNAME =
            "SELECT id FROM user WHERE username = ?";

    // Vars ---------------------------------------------------------------------------------------

    private DAOFactory daoFactory;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an User DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * @param daoFactory The DAOFactory to construct this User DAO for.
     */
    UserDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the user from the database matching the given ID, otherwise null.
     * @param id The ID of the user to be returned.
     * @return The user from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public User find(Long id) throws DAOException {
        return find(SQL_FIND_BY_ID, id);
    }

    /**
     * Returns the user from the database matching the given username and password, otherwise null.
     * @param username The username of the user to be returned.
     * @param password The password of the user to be returned.
     * @return The user from the database matching the given username and password, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public User find(String username, String password) throws DAOException {
        return find(SQL_FIND_BY_USERNAME_AND_PASSWORD, username, hashMD5(password));
    }

    public User findByName(String username) throws DAOException {
        return find(SQL_FIND_BY_USERNAME, username);
    }
    /**
     * Returns the user from the database matching the given SQL query with the given values.
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return The user from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private User find(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = mapUser(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return user;
    }

    /**
     * Returns a list of all users from the database ordered by user ID. The list is never null and
     * is empty when the database does not contain any user.
     * @return A list of all users from the database ordered by user ID.
     * @throws DAOException If something fails at database level.
     */
    public List<User> list() throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<User> users = new ArrayList<User>();

        try {
            connection = daoFactory.getConnection();
            preparedStatement = connection.prepareStatement(SQL_LIST_ORDER_BY_ID);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return users;
    }

    /**
     * Create the given user in the database. The user ID must be null, otherwise it will throw
     * IllegalArgumentException. If the user ID value is unknown, rather use {@link #save(User)}.
     * After creating, the DAO will set the obtained ID in the given user.
     * @param user The user to be created in the database.
     * @throws IllegalArgumentException If the user ID is not null.
     * @throws DAOException If something fails at database level.
     */
    public void create(User user) throws IllegalArgumentException, DAOException {
        if (user.getId() != null) {
            throw new IllegalArgumentException("User is already created, the user ID is not null.");
        }
        
        Object[] values = {
            user.getUsername(),
            hashMD5IfNecessary(user.getPassword()),
            user.getEmail(),
            user.getAge()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_INSERT, true, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Creating user failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Creating user failed, no generated key obtained.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, generatedKeys);
        }
    }

    /**
     * Update the given user in the database. The user ID must not be null, otherwise it will throw
     * IllegalArgumentException. If the user ID value is unknown, rather use {@link #save(User)}.
     * @param user The user to be updated in the database.
     * @throws IllegalArgumentException If the user ID is null.
     * @throws DAOException If something fails at database level.
     */
    public void update(User user) throws DAOException {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User is not created yet, the user ID is null.");
        }

        Object[] values = {
            user.getUsername(),
            hashMD5IfNecessary(user.getPassword()),
            user.getEmail(),
            user.getAge(),
            user.getId()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_UPDATE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Updating user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }

    /**
     * Save the given user in the database. If the user ID is null, then it will invoke
     * {@link #create(User)}, else it will invoke {@link #update(User)}.
     * @param user The user to be saved in the database.
     * @throws DAOException If something fails at database level.
     */
    public void save(User user) throws DAOException {
        if (user.getId() == null) {
            create(user);
        } else {
            update(user);
        }
    }

    /**
     * Delete the given user from the database. After deleting, the DAO will set the ID of the given
     * user to null.
     * @param user The user to be deleted from the database.
     * @throws DAOException If something fails at database level.
     */
    public void delete(User user) throws DAOException {
        Object[] values = { user.getId() };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_DELETE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Deleting user failed, no rows affected.");
            } else {
                user.setId(null);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }

    /**
     * Returns true if the given username exist in the database.
     * @param username The username which is to be checked in the database.
     * @return True if the given username exist in the database.
     * @throws DAOException If something fails at database level.
     */
    public boolean existUsername(String username) throws DAOException {
        return exist(SQL_EXIST_USERNAME, username);
    }

    /**
     * Returns true if the given email address exist in the database.
     * @param email The email address which is to be checked in the database.
     * @return True if the given email address exist in the database.
     * @throws DAOException If something fails at database level.
     */
    public boolean existEmail(String email) throws DAOException {
        return exist(SQL_EXIST_EMAIL, email);
    }

    /**
     * Returns true if the given SQL query with the given values returns at least one row.
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return True if the given SQL query with the given values returns at least one row.
     * @throws DAOException If something fails at database level.
     */
    private boolean exist(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean exist = false;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            exist = resultSet.next();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return exist;
    }

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Generate MD5 hash for the given password if necessary. That is, if it is not already hashed.
     * @param password The password to generate a hash for if necessary.
     * @return The hash of the given password or the same value if it is already hashed.
     */
    private static String hashMD5IfNecessary(String password) {
        return !"^[a-f0-9]{32}$".matches(password) ? hashMD5(password) : password;
    }

    /**
     * Map the current row of the given ResultSet to an User.
     * @param resultSet The ResultSet of which the current row is to be mapped to an User.
     * @return The mapped User from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    private static User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
            resultSet.getLong("id"),
            resultSet.getString("username"),
            resultSet.getString("password"),
            resultSet.getString("email"),
            resultSet.getObject("age") != null ? resultSet.getInt("age") : null
        );
    }

}