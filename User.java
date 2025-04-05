package application;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    private String userName;
    private String password;
    private String role;

    /**
     * Constructor to initialize a new User object with userName, password, and role.
     * 
     * @param userName
     * @param password
     * @param role
     */
    public User( String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }
    
    /**
     * Sets the role of the user.
     * @param role
     */
    public void setRole(String role) {
    	this.role=role;
    }
    
    /**
     * @return User Name
     */
    public String getUserName() { return userName; }
    
    /**
     * @return Users password
     */
    public String getPassword() { return password; }
    
    /**
     * @return Users role
     */
    public String getRole() { return role; }
}
