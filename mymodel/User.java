package mymodel;

/**
 * This class represents a Data Transfer Object for the User. This DTO can be used thoroughout all
 * layers, the data layer, the controller layer and the view layer.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public class User {

    // Properties ---------------------------------------------------------------------------------

    private Long id;
    private String username;
    private String password;
    private String email;
    private Integer age;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Default constructor.
     */
    public User() {
        // Always keep the default constructor alive in a Javabean class.
    }

    /**
     * Minimal constructor. Contains required fields.
     * @param id The ID of this User. Set it to null in case of a new and unexisting user.
     * @param username The username of this User.
     * @param password The password of this User.
     */
    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * Full constructor. Contains required and optional fields.
     * @param id The ID of this User. Set it to null in case of a new and unexisting user.
     * @param username The username of this User.
     * @param password The password of this User.
     * @param email The email address of this User.
     * @param age The age of this User.
     */
    public User(Long id, String username, String password, String email, Integer age) {
        this(id, username, password);
        this.email = email;
        this.age = age;
    }

    // Getters ------------------------------------------------------------------------------------

    /**
     * Returns the ID of this User.
     * @return The ID of this User.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the username of this User.
     * @return The username of this User.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password of this User.
     * @return The password of this User.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the email address of this User.
     * @return The email address of this User.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the age of this User.
     * @return The age of this User.
     */
    public Integer getAge() {
        return age;
    }

    // Setters ------------------------------------------------------------------------------------

    /**
     * Sets the ID of this User.
     * @param id The ID of this User.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the username of this User.
     * @param username The username of this User.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password of this User.
     * @param password The password of this User.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the email address of this User.
     * @param email The email address of this User.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the age of this User.
     * @param age The age of this User.
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    // Override -----------------------------------------------------------------------------------

    /**
     * The user ID is unique for each User. So this should compare User by ID only.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        return (other instanceof User) && (id != null) ? id.equals(((User) other).id) : (other == this);
    }

    /**
     * The user ID is unique for each User. So User with same ID should return same hashcode.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
    }

    /**
     * Returns the String representation of this User. Not required, it just pleases reading logs.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return String.format("User[id=%d,username=%s,password=%s,email=%s,age=%d]", 
            id, username, password, email, age);
    }

}