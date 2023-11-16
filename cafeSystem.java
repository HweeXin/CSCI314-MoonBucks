//import statements
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.ImageIcon;  

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
//LOG IN PAGE
class LoginPage //Boundary
{
    private JFrame frame;
    private JTextField usernameField;
    private static String username_user;
    private JPasswordField passwordField;

    //default constructor
    public LoginPage()
    {
        //creating the frame
        frame = new JFrame("Login page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(1280, 800);

        // Creating the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(251,227,148));

        // Create the NORTH BorderLayout Panel (for Logo)
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(251,227,148));
        JLabel lblLogo = new JLabel(new ImageIcon("MoonBucks Images/MoonbucksLogo.png"));
        logoPanel.add(lblLogo);

        // Create the CENTER BorderLayout Panel (for User Details)
        JPanel userDetails = new JPanel(new GridLayout(3, 2));
        userDetails.setBackground(new Color(251,227,148));

        //creating the contents of the userDetails panel and adding into the JPanel
        JLabel lblUsername = new JLabel("Username:");
        userDetails.add(lblUsername);
        usernameField = new JTextField();
        userDetails.add(usernameField);

        JLabel lblPassword = new JLabel("Password:");
        userDetails.add(lblPassword);
        passwordField = new JPasswordField();
        userDetails.add(passwordField);

        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(new Color(251,227,148));
        userDetails.add(emptyPanel);

        JButton btnLogin = new JButton("Login");
        userDetails.add(btnLogin);

        // Create the WEST BorderLayout Panel (for image)
        JPanel emptyPanelWest = new JPanel();
        emptyPanelWest.setBackground(new Color(251,227,148));
        JLabel imageWEST = new JLabel(new ImageIcon("MoonBucks Images/coffeeWEST.png"));
        emptyPanelWest.add(imageWEST);

        // Create the EAST BorderLayout Panel (for image)
        JPanel emptyPanelEast = new JPanel();
        emptyPanelEast.setBackground(new Color(251,227,148));
        JLabel imageEAST = new JLabel(new ImageIcon("MoonBucks Images/coffeeEAST.png"));
        emptyPanelEast.add(imageEAST);

        // Create the SOUTH BorderLayout Panel (for image)
        JPanel emptyPanelSouth = new JPanel();
        emptyPanelSouth.setBackground(new Color(251,227,148));
        JLabel imageSOUTH = new JLabel(new ImageIcon("MoonBucks Images/coffeeSOUTH.png"));
        emptyPanelSouth.add(imageSOUTH);

        // Add all of the panels to the main panel
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(userDetails, BorderLayout.CENTER);
        mainPanel.add(emptyPanelEast, BorderLayout.EAST);
        mainPanel.add(emptyPanelWest, BorderLayout.WEST);
        mainPanel.add(emptyPanelSouth, BorderLayout.SOUTH);

        //adding the function call for login button
        btnLogin.addActionListener(e -> authenticate());
        
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public static String getUsername()
    {
        return username_user;
    }

    public void authenticate()
    {
        //Authenticating the username and password
        try 
        {
            String username = usernameField.getText();
            username_user = username;

            String password = new String(passwordField.getPassword());
            authenticateLoginPageAndLogout auth = new authenticateLoginPageAndLogout(); //calling the controller
            String accessType = auth.checkUser(username, password);

            if (auth.isAccountSuspended(username) == true)
            {
                JOptionPane.showMessageDialog(null, "Account suspended. Please contact admin.");
                return;
            }

            if (accessType != null && !accessType.isEmpty())
            {
                frame.dispose(); //closing the frame after opening the homepage
                HomePage homepg = new HomePage(accessType);
            }

            else
            {
                usernameField.setText("");
                passwordField.setText("");
            }
        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
        }
    }

}

class authenticateLoginPageAndLogout //Controller
{
    //default constructor
    public authenticateLoginPageAndLogout()
    {

    }

    //check if username and password matches
    public String checkUser (String username, String password)
    {
        LogInLogOutEntity  sql = new LogInLogOutEntity (); //this calls the entitiy class, "SQL"

        try
        {
            String accessType = sql.runLogin(username, password);
            return accessType;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean isAccountSuspended(String username)
    {
        LogInLogOutEntity l = new LogInLogOutEntity();

        if (l.isAccountSuspended(username) == true)
        {
            return true;
        }

        return false;
    }

    //logging out
    public void logout()
    {
        LoginPage login = new LoginPage();
    }
}

class LogInLogOutEntity //Entity
{
    //default constructor
    public LogInLogOutEntity ()
    {
        
    }

    //void methods
    public String runLogin(String usernameForAuth, String passwordForAuth)
    {
        try
        {
            String accessType = "";
            String url = "jdbc:mysql://127.0.0.1:3306/CafeSystem"; //this is the url for your mySql database connection
            String username = "root"; //this is the mySql connection username
            String password = "sql123"; //this is the mySql connection password

            Class.forName("com.mysql.cj.jdbc.Driver"); //this is default for all machines and must always be included

            Connection connection = DriverManager.getConnection(url, username, password);

            Statement statement = connection.createStatement();

            //selecting and printing what database I want using the variable "resultSet"
            ResultSet resultSet = statement.executeQuery("select * from user_accounts");

            //Printing out the information
            while (resultSet.next())
            {
                //System.out.println(resultSet.getInt(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3));
            }

            // Fetch user details including access type from the database
            String sql = "SELECT access_level FROM user_accounts WHERE username= ? AND Password= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usernameForAuth);
            preparedStatement.setString(2, passwordForAuth);
            ResultSet rs = preparedStatement.executeQuery();
            
            //checking what user is this and grant respective access
            if (rs.next())
            {
                JOptionPane.showMessageDialog(null, "Login successful...");
                accessType = rs.getString("access_level");
            }

            else
            {
                JOptionPane.showMessageDialog(null, "Incorrect username and/or password...");
                
            }

            rs.close();
            preparedStatement.close();

            return accessType;
        }

        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return "Error in running the login";
    }

    public boolean isAccountSuspended(String username) 
    {
        // Query the database to check if the account is suspended
        String sql = "SELECT is_suspended FROM user_accounts WHERE username = ?";
        boolean isSuspended = false;

        try 
        {
            String url = "jdbc:mysql://127.0.0.1:3306/CafeSystem"; //this is the url for your mySql database connection
            String username1 = "root"; //this is the mySql connection username
            String password = "sql123"; //this is the mySql connection password
            Class.forName("com.mysql.cj.jdbc.Driver"); //this is default for all machines and must always be included
            Connection connection = DriverManager.getConnection(url, username1, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            
            if (rs.next()) 
            {
                int isSuspendedValue = rs.getInt("is_suspended");
                isSuspended = (isSuspendedValue == 1);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } 
        catch (ClassNotFoundException e) 
        {
            e.printStackTrace();
        }

        return isSuspended;
    }
}

class HomePage extends JFrame//Boundary 
{
    private JFrame frame;
    static GraphicsDevice device = GraphicsEnvironment
        .getLocalGraphicsEnvironment().getScreenDevices()[0];

    //default constructor
    public HomePage(String accessType)
    {
        frame = new JFrame ("Home Page");
        frame.setSize(1280, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Creating the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel);

        // Create the NORTH BorderLayout Panel (for Logo)
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(251,227,148));
        JLabel lblLogo = new JLabel(new ImageIcon("MoonBucks Images/MoonbucksBanner.png"));
        logoPanel.add(lblLogo);
        // Add the logo banner panel to the main panel
        mainPanel.add(logoPanel, BorderLayout.NORTH);

        // Create the CENTER BorderLayout Panel
        JPanel homePageType = new JPanel();

        switch (accessType) 
        {
            case "admin":
                homePageType.add(new HomePageAdmin());
                mainPanel.add(homePageType, BorderLayout.CENTER);
                break;
            case "cafe_owner":
                homePageType.add(new HomePageCafe_Owner());
                mainPanel.add(homePageType, BorderLayout.CENTER);
                break;
            case "cafe_manager":
                homePageType.add(new HomePageCafe_Manager());
                mainPanel.add(homePageType, BorderLayout.CENTER);
                break;
            case "cafe_staff":
                homePageType.add(new HomePageCafe_Staff());
                mainPanel.add(homePageType, BorderLayout.CENTER);
                break;
            default:
                homePageType.add(new JLabel("Unknown access type"));
                mainPanel.add(homePageType, BorderLayout.CENTER);
                break;
        }

        //logout button
        JButton btnLogout = new JButton("Log out");
        btnLogout.setPreferredSize(new Dimension(150,50));
        mainPanel.add(btnLogout, BorderLayout.SOUTH);

        //log out action
        authenticateLoginPageAndLogout auth = new authenticateLoginPageAndLogout();
        btnLogout.addActionListener(e -> frame.dispose());
        btnLogout.addActionListener(e -> auth.logout());

        //setting the frame to be visible
        frame.setVisible(true);
    }
}

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
//SYSTEM ADMIN HOMEPAGE
class HomePageAdmin extends JPanel
{
    public HomePageAdmin()
    {
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        JPanel userAccountButtons = new JPanel(new GridLayout(1, 7));
        JPanel userProfileButtons = new JPanel(new GridLayout(1, 7));
        
        //create user button
        JButton btnCreateUser = new JButton("Create User Account");
        btnCreateUser.addActionListener(e -> {
            HomePageAdmin_create_user_account_boundary boundary = new HomePageAdmin_create_user_account_boundary();
            boundary.createUserAccount();
        });

        //view all user button
        JButton btnViewAllUser = new JButton("View All Users");
        btnViewAllUser.addActionListener(e -> {
            HomePageAdmin_view_user_account_boundary boundary = new HomePageAdmin_view_user_account_boundary();
            boundary.viewAllUser();
        });

        //update user button
        JButton btnUpdateUserDetails = new JButton("Update User Details");
        btnUpdateUserDetails.addActionListener(e -> {
            HomePageAdmin_update_user_account_boundary boundary = new HomePageAdmin_update_user_account_boundary();
            boundary.updateUserAccount();
        });

        //delete user button
        JButton btnDeleteUser = new JButton("Delete User");
        btnDeleteUser.addActionListener(e -> {
            HomePageAdmin_delete_user_account_boundary boundary = new HomePageAdmin_delete_user_account_boundary();
            boundary.deleteUserAccount();
        });

        //search button
        JButton btnSearchUser = new JButton("Search For User");
        btnSearchUser.addActionListener(e -> {
            HomePageAdmin_search_user_account_boundary boundary = new HomePageAdmin_search_user_account_boundary();
            boundary.searchUserAccount();
        });

        //suspend user button
        JButton btnSuspendUser = new JButton("Suspend User");
        btnSuspendUser.addActionListener(e -> { 
            HomePageAdmin_suspend_user_account_boundary boundary = new HomePageAdmin_suspend_user_account_boundary();
            boundary.suspendUserAccount();
        });

        //unsuspend user button
        JButton btnUnsuspendUser = new JButton("Unsuspend User");
        btnUnsuspendUser.addActionListener(e -> { 
            HomePageAdmin_suspend_user_account_boundary boundary = new HomePageAdmin_suspend_user_account_boundary();
            boundary.unsuspendUserAccount();
        });

        //create user profile button
        JButton btnCreateUserProfile = new JButton("Create User Profile");
        btnCreateUserProfile.addActionListener(e -> {
            HomePageAdmin_create_user_profile_boundary boundary = new HomePageAdmin_create_user_profile_boundary();
            boundary.createUserProfile();
        });

        //viewing user profile button
        JButton btnViewUserProfile = new JButton("View all User Profiles");
        btnViewUserProfile.addActionListener(e -> {
            HomePageAdmin_view_user_profile_boundary boundary = new HomePageAdmin_view_user_profile_boundary();
            boundary.viewAllUser();
        });

        //updating user profile button
        JButton btnUpdateUserProfile = new JButton("Update User Profile");
        btnUpdateUserProfile.addActionListener(e -> {
            HomePageAdmin_update_user_profile_boundary boundary = new HomePageAdmin_update_user_profile_boundary();
            boundary.updateUserAccount();
        });

        //deleting user profile
        JButton btnDeleteUserProfile = new JButton("Delete User Profile");
        btnDeleteUserProfile.addActionListener(e -> {
            HomePageAdmin_delete_user_profile_boundary boundary = new HomePageAdmin_delete_user_profile_boundary();
            boundary.deleteUserProfile();
        });

        JButton btnSearchUserProfile = new JButton("Search User Profile");
        btnSearchUserProfile.addActionListener(e -> {
            HomePageAdmin_search_user_profile_boundary boundary = new HomePageAdmin_search_user_profile_boundary();
            boundary.searchUserProfile();
        });

        //adding the buttons to the JPanel
        userAccountButtons.add(btnCreateUser);
        userAccountButtons.add(btnViewAllUser);
        userAccountButtons.add(btnUpdateUserDetails);
        userAccountButtons.add(btnDeleteUser);
        userAccountButtons.add(btnSearchUser);
        userAccountButtons.add(btnSuspendUser);
        userAccountButtons.add(btnUnsuspendUser);
        mainPanel.add(userAccountButtons);
        //------------user profile button -------------//
        userProfileButtons.add(btnCreateUserProfile);
        userProfileButtons.add(btnViewUserProfile);
        userProfileButtons.add(btnUpdateUserProfile);
        userProfileButtons.add(btnDeleteUserProfile);
        userProfileButtons.add(btnSearchUserProfile);
        mainPanel.add(userProfileButtons);

        add(mainPanel);
    }
}

//CREATE USER ACCOUNT (DONE)
class HomePageAdmin_create_user_account_boundary 
{
   public HomePageAdmin_create_user_account_boundary()
   {

   }

   public void createUserAccount()
   {
        String username = JOptionPane.showInputDialog("Enter Username:");
        String password = JOptionPane.showInputDialog("Enter Password:");
        String fullName = JOptionPane.showInputDialog("Enter Full Name:");
        String email = JOptionPane.showInputDialog("Enter Email:");

        AccessLevelRetrieval_boundary b = new AccessLevelRetrieval_boundary();

        // Combobox for access level
        String[] accessLevels = b.getAccessLevel();
        JComboBox<String> accessLevelComboBox = new JComboBox<>(accessLevels);

        int result = JOptionPane.showOptionDialog(
                null,
                accessLevelComboBox,
                "Select Access Level",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null
        );

        String accessLevel;

        if (result == JOptionPane.OK_OPTION) 
        {
            accessLevel = (String) accessLevelComboBox.getSelectedItem();
        } 

        else 
        {
            JOptionPane.showMessageDialog(
                    null,
                    "Operation canceled by the user",
                    "Canceled",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;  // Exit method if user cancels
        }

        HomePageAdmin_create_user_account_controller controller = new HomePageAdmin_create_user_account_controller();
        boolean finalResult = controller.createUserAccount(username, password, fullName, email, accessLevel);

        if (finalResult) 
        {
            JOptionPane.showMessageDialog(null, "User account created successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Failed to create user account.");
        }
   }
}

class HomePageAdmin_create_user_account_controller
{
    private HomePageAdmin_create_user_account_entity entity;

    public HomePageAdmin_create_user_account_controller() 
    {
        entity = new HomePageAdmin_create_user_account_entity();
    }

    public boolean createUserAccount(String username, String password, String fullName, String email, String accessLevel) 
    {

        boolean success = entity.createUserAccount(username, password, fullName, email, accessLevel);

        return success;
    }
}

class HomePageAdmin_create_user_account_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME1 = "root";
    private static final String PASSWORD1 = "sql123";

    public boolean createUserAccount(String username, String password, String fullName, String email, String accessLevel) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME1, PASSWORD1)) 
        {
            String sql = "INSERT INTO user_accounts (username, password, full_name, email, access_level) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, fullName);
                preparedStatement.setString(4, email);
                preparedStatement.setString(5, accessLevel);

                int rowsInserted = preparedStatement.executeUpdate();

                return rowsInserted > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

    }
}

//VIEW USER ACCOUNT (DONE)
class HomePageAdmin_view_user_account_boundary
{
    public HomePageAdmin_view_user_account_boundary()
    {
        
    }

    HomePageAdmin_view_user_account_controller controller = new HomePageAdmin_view_user_account_controller();

    public void viewAllUser()
    {
        ArrayList<User> users = controller.viewAllUser();

        // Create a table model and populate it with user data
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Admin ID");
        tableModel.addColumn("Username");
        tableModel.addColumn("Full Name");
        tableModel.addColumn("Email");
        tableModel.addColumn("Access Level");

        for (User user : users) 
        {
            tableModel.addRow(new Object[]{user.getAdminId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getAccessLevel()});
        }

        // Create a JTable using the table model
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Display the table in a JFrame
        JFrame frame = new JFrame("User List");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setVisible(true);
    }
}

class HomePageAdmin_view_user_account_controller
{
    private HomePageAdmin_view_user_account_entity entity;

    public HomePageAdmin_view_user_account_controller()
    {
        entity = new HomePageAdmin_view_user_account_entity();
    }

    public ArrayList<User> viewAllUser()
    {
        ArrayList<User> users = entity.getAllUsers();

        return users;
    }
}

class HomePageAdmin_view_user_account_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public ArrayList<User> getAllUsers() 
    {
        ArrayList<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "SELECT * FROM user_accounts";
            try (Statement statement = connection.createStatement()) 
            {
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) 
                {
                    int adminId = resultSet.getInt("admin_id");
                    String username = resultSet.getString("username");
                    String fullName = resultSet.getString("full_name");
                    String email = resultSet.getString("email");
                    String accessLevel = resultSet.getString("access_level");
                    // Create a User object with the retrieved data
                    User user = new User(adminId, username, fullName, email, accessLevel);
                    users.add(user);
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return users;
    }
}

//UPDATE USER ACCOUNT (DONE)
class HomePageAdmin_update_user_account_boundary
{
    public HomePageAdmin_update_user_account_boundary()
    {

    }

    HomePageAdmin_update_user_account_controller controller = new HomePageAdmin_update_user_account_controller();

    public void updateUserAccount() 
        {
            String username = JOptionPane.showInputDialog("Enter Username:");
            String newFullName = JOptionPane.showInputDialog("Enter New Full Name:");
            String newEmail = JOptionPane.showInputDialog("Enter New Email:");

                  AccessLevelRetrieval_boundary b = new AccessLevelRetrieval_boundary();
            // Combobox for access level
                    String[] accessLevels = b.getAccessLevel();
                    JComboBox<String> accessLevelComboBox = new JComboBox<>(accessLevels);

                    int result = JOptionPane.showOptionDialog(
                            null,
                            accessLevelComboBox,
                            "Select Access Level",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            null,
                            null
                    );

                    String accessLevel;

                    if (result == JOptionPane.OK_OPTION) 
                    {
                        accessLevel = (String) accessLevelComboBox.getSelectedItem();
                    } 

                    else 
                    {
                        JOptionPane.showMessageDialog(
                                null,
                                "Operation canceled by the user",
                                "Canceled",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        return;  // Exit method if user cancels
                    }

            boolean success = controller.updateUserAccount(username, newFullName, newEmail, accessLevel);

            if (success) 
            {
                JOptionPane.showMessageDialog(null, "User account updated successfully!");
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "Failed to update user account.");
            }
    }
}

class HomePageAdmin_update_user_account_controller
{
    private HomePageAdmin_update_user_account_entity entity;

        public HomePageAdmin_update_user_account_controller() 
        {
            entity = new HomePageAdmin_update_user_account_entity();
        }

        public boolean updateUserAccount(String username, String newFullName, String newEmail, String accessLevel) 
        {
            boolean success = entity.updateUserAccount(username, newFullName, newEmail, accessLevel);

            return success;
    }
}

class HomePageAdmin_update_user_account_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public boolean updateUserAccount(String username, String newFullName, String newEmail, String accessLevel) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "UPDATE user_accounts SET full_name = ?, email = ?, access_level = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, newFullName);
                preparedStatement.setString(2, newEmail);
                preparedStatement.setString(3, accessLevel);
                preparedStatement.setString(4, username);

                int rowsUpdated = preparedStatement.executeUpdate();

                return rowsUpdated > 0;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }
}

//DELETE USER ACCOUNT (DONE)
class HomePageAdmin_delete_user_account_boundary
{
    public HomePageAdmin_delete_user_account_boundary()
    {

    }

    HomePageAdmin_delete_user_account_controller controller = new HomePageAdmin_delete_user_account_controller();

    public void deleteUserAccount() 
    {
        String username = JOptionPane.showInputDialog("Enter Username for deletion:");

        boolean success = controller.deleteUserAccount(username);

        if (success) 
        {
            JOptionPane.showMessageDialog(null, "User account deleted successfully!");
        } else 
        {
            JOptionPane.showMessageDialog(null, "Failed to delete user account.");
        }
    }
}

class HomePageAdmin_delete_user_account_controller
{
    private HomePageAdmin_delete_user_account_entity entity;

    public HomePageAdmin_delete_user_account_controller() 
    {
        entity = new HomePageAdmin_delete_user_account_entity();
    }

    public boolean deleteUserAccount(String username) 
    {
        boolean success = entity.deleteUserAccount(username);

        return success;
    }
}

class HomePageAdmin_delete_user_account_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
        private static final String USERNAME = "root";
        private static final String PASSWORD = "sql123";

        public boolean deleteUserAccount(String username) 
        {
            try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
            {
                String sql = "DELETE FROM user_accounts WHERE username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
                {
                    preparedStatement.setString(1, username);

                    int rowsDeleted = preparedStatement.executeUpdate();

                    return rowsDeleted > 0;
                }
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
                return false;
            }
        }
}

//SEARCH A USER ACCOUNT (DONE)
class HomePageAdmin_search_user_account_boundary
{
    public HomePageAdmin_search_user_account_boundary()
    {

    }

    HomePageAdmin_search_user_account_controller controller = new HomePageAdmin_search_user_account_controller();

    public void searchUserAccount() 
        {
            String username = JOptionPane.showInputDialog("Enter Username:");

            String userDetails = controller.searchUserAccount(username);

            if (userDetails != null) 
            {
                JOptionPane.showMessageDialog(null, userDetails);
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "User not found.");
            }
        }
}

class HomePageAdmin_search_user_account_controller
{
    private HomePageAdmin_search_user_account_entity entity;

        public HomePageAdmin_search_user_account_controller() 
        {
            entity = new HomePageAdmin_search_user_account_entity();
        }

        public String searchUserAccount(String username) 
        {
            String userDetails = entity.searchUserAccount(username);

            if (userDetails != null) 
            {
                return userDetails;
            } 
            else 
            {
                return null;
            }
        }
}

class HomePageAdmin_search_user_account_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public String searchUserAccount(String username) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "SELECT * FROM user_accounts WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, username);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) 
                {
                    int adminId = resultSet.getInt("admin_id");
                    String fullName = resultSet.getString("full_name");
                    String email = resultSet.getString("email");
                    String accessLevel = resultSet.getString("access_level");

                    return "Admin ID: " + adminId +
                            "\nUsername: " + username +
                            "\nFull Name: " + fullName +
                            "\nEmail: " + email +
                            "\nAccess Level: " + accessLevel;
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return null;
    }
}

//SUSPEND A USER ACCOUNT (DONE)
class HomePageAdmin_suspend_user_account_boundary
{
    public HomePageAdmin_suspend_user_account_boundary()
    {

    }

    HomePageAdmin_suspend_user_account_controller controller = new HomePageAdmin_suspend_user_account_controller();

    public void suspendUserAccount() 
    {
        String username = JOptionPane.showInputDialog("Enter Username:");
        boolean success = controller.suspendUserAccount(username, true);

        if (success) 
        {
            JOptionPane.showMessageDialog(null, "Account suspended successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Failed to suspend account.");
        }
    }

    public void unsuspendUserAccount() 
    {
        String username = JOptionPane.showInputDialog("Enter Username:");
        boolean success = controller.suspendUserAccount(username, false);

        if (success)
        {
            JOptionPane.showMessageDialog(null, "Account unsuspended successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Failed to unsuspend account.");
        }
    }
}

class HomePageAdmin_suspend_user_account_controller
{
    private HomePageAdmin_suspend_user_account_entity entity;

    public HomePageAdmin_suspend_user_account_controller() 
    {
        entity = new HomePageAdmin_suspend_user_account_entity();
    }

    public boolean suspendUserAccount(String username, boolean b) 
    {
        boolean success = entity.suspendUserAccount(username, b);

        return success;
    }

    public boolean unsuspendUserAccount(String username, boolean b) 
    {
        boolean success = entity.suspendUserAccount(username, b);

        return success;
    }
}

class HomePageAdmin_suspend_user_account_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public boolean suspendUserAccount(String username, boolean suspend) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "UPDATE user_accounts SET is_suspended = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setBoolean(1, suspend);
                preparedStatement.setString(2, username);

                int rowsUpdated = preparedStatement.executeUpdate();

                return rowsUpdated > 0;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return false;
    }
}

//---------------------------------USER PROFILE---------------------------------------//
//CREATE USER PROFILE (DONE)
class HomePageAdmin_create_user_profile_boundary 
{
   public HomePageAdmin_create_user_profile_boundary()
   {

   }

   public void createUserProfile()
   {
        String profileName = JOptionPane.showInputDialog("Enter profile name:");

        HomePageAdmin_create_user_profile_controller controller = new HomePageAdmin_create_user_profile_controller();
        boolean finalResult = controller.createUserProfile(profileName);

        if (finalResult) 
        {
            JOptionPane.showMessageDialog(null, "User profile created successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Failed to create user profile.");
        }
   }
}

class HomePageAdmin_create_user_profile_controller
{
    private HomePageAdmin_create_user_profile_entity entity;

    public HomePageAdmin_create_user_profile_controller() 
    {
        entity = new HomePageAdmin_create_user_profile_entity();
    }

    public boolean createUserProfile(String profileName) 
    {

        boolean success = entity.createUserProfile(profileName);

        return success;
    }
}

class HomePageAdmin_create_user_profile_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME1 = "root";
    private static final String PASSWORD1 = "sql123";

    public boolean createUserProfile(String profileName) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME1, PASSWORD1)) 
        {
            String sql = "INSERT INTO user_profile (profile) VALUES (?)";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, profileName);

                int rowsInserted = preparedStatement.executeUpdate();

                return rowsInserted > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

    }
}

//VIEW USER PROFILES (DONE)
class HomePageAdmin_view_user_profile_boundary
{
    HomePageAdmin_view_user_profile_controller controller = new HomePageAdmin_view_user_profile_controller();

    public void viewAllUser()
    {
        ArrayList<User_profile> users = controller.viewAllUser();

        // Create a table model and populate it with user data
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("user profile ID");
        tableModel.addColumn("Profile");

        for (User_profile user : users) 
        {
            tableModel.addRow(new Object[]{user.getID(), user.getProfileName()});
        }

        // Create a JTable using the table model
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Display the table in a JFrame
        JFrame frame = new JFrame("User profile List");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setVisible(true);
    }

}

class HomePageAdmin_view_user_profile_controller
{
    private HomePageAdmin_view_user_profile_entity entity;

    public HomePageAdmin_view_user_profile_controller()
    {
        entity = new HomePageAdmin_view_user_profile_entity();
    }

    public ArrayList<User_profile> viewAllUser()
    {
        ArrayList<User_profile> users = entity.getAllUsers();

        return users;
    }
}

class HomePageAdmin_view_user_profile_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public ArrayList<User_profile> getAllUsers() 
    {
        ArrayList<User_profile> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "SELECT * FROM user_profile";
            try (Statement statement = connection.createStatement()) 
            {
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) 
                {
                    int adminId = resultSet.getInt("user_profile_id");
                    String username = resultSet.getString("profile");

                    // Create a User object with the retrieved data
                    User_profile user = new User_profile(adminId, username);
                    users.add(user);
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return users;
    }
}

//UPDATE USER PROFILE (DONE)
class HomePageAdmin_update_user_profile_boundary
{
    public HomePageAdmin_update_user_profile_boundary()
    {

    }

    HomePageAdmin_update_user_profile_controller controller = new HomePageAdmin_update_user_profile_controller();

    public void updateUserAccount() 
        {
            String input = JOptionPane.showInputDialog("Enter user_profile id:");
            int id = Integer.parseInt(input);
            String newFullName = JOptionPane.showInputDialog("Enter new profile name:");

            boolean success = controller.updateUserProfile(id, newFullName);

            if (success) 
            {
                JOptionPane.showMessageDialog(null, "User account updated successfully!");
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "Failed to update user account.");
            }
    }
}

class HomePageAdmin_update_user_profile_controller
{
    private HomePageAdmin_update_user_profile_entity entity;

    public HomePageAdmin_update_user_profile_controller() 
    {
        entity = new HomePageAdmin_update_user_profile_entity();
    }

    public boolean updateUserProfile(int id, String newProfileName) 
    {
        boolean success = entity.updateUserProfile(id, newProfileName);

        return success;
    }
}

class HomePageAdmin_update_user_profile_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public boolean updateUserProfile(int id, String newProfileName) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "UPDATE user_profile SET profile = ? WHERE user_profile_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, newProfileName);
                preparedStatement.setInt(2, id);

                int rowsUpdated = preparedStatement.executeUpdate();

                return rowsUpdated > 0;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }
}

//DELETE USER PROFILE (DONE)
class HomePageAdmin_delete_user_profile_boundary
{
    public HomePageAdmin_delete_user_profile_boundary()
    {

    }

    HomePageAdmin_delete_user_profile_controller controller = new HomePageAdmin_delete_user_profile_controller();

    public void deleteUserProfile() 
    {
        String input = JOptionPane.showInputDialog("Enter user profile id for deletion:");
        int id = Integer.parseInt(input);
        boolean success = controller.deleteUserProfile(id);

        if (success) 
        {
            JOptionPane.showMessageDialog(null, "User profile deleted successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Failed to delete user profile.");
        }
    }
}

class HomePageAdmin_delete_user_profile_controller
{
    private HomePageAdmin_delete_user_profile_entity entity;

    public HomePageAdmin_delete_user_profile_controller() 
    {
        entity = new HomePageAdmin_delete_user_profile_entity();
    }

    public boolean deleteUserProfile(int id) 
    {
        boolean success = entity.deleteUserProfile(id);

        return success;
    }
}

class HomePageAdmin_delete_user_profile_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public boolean deleteUserProfile(int id) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "DELETE FROM user_profile WHERE user_profile_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setInt(1, id);

                int rowsDeleted = preparedStatement.executeUpdate();

                return rowsDeleted > 0;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }
}

//SEARCH USER PROFILE (DONE)
class HomePageAdmin_search_user_profile_boundary
{
    public HomePageAdmin_search_user_profile_boundary()
    {

    }

    HomePageAdmin_search_user_profile_controller controller = new HomePageAdmin_search_user_profile_controller();

    public void searchUserProfile() 
        {
            String input = JOptionPane.showInputDialog("Enter user profile id:");
            int id = Integer.parseInt(input);

            String userDetails = controller.searchUserProfile(id);

            if (userDetails != null) 
            {
                JOptionPane.showMessageDialog(null, userDetails);
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "User not found.");
            }
        }
}

class HomePageAdmin_search_user_profile_controller
{
    private HomePageAdmin_search_user_profile_entity entity;

    public HomePageAdmin_search_user_profile_controller() 
    {
        entity = new HomePageAdmin_search_user_profile_entity();
    }

    public String searchUserProfile(int id) 
    {
        String userDetails = entity.searchUserProfile(id);

        if (userDetails != null) 
        {
            return userDetails;
        } 
        else 
        {
            return null;
        }
    }
}

class HomePageAdmin_search_user_profile_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public String searchUserProfile(int id) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "SELECT * FROM user_profile WHERE user_profile_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setInt(1, id);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) 
                {
                    int Id = resultSet.getInt("user_profile_id");
                    String profileName = resultSet.getString("profile");

                    return "Admin ID: " + Id +
                            "\nFull Name: " + profileName;

                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return null;
    }
}

//-------------------------------------------------------------------------//
//DROPDOWN BOX BOUNDARY
class AccessLevelRetrieval_boundary
{
    public AccessLevelRetrieval_boundary()
    {

    }

    AccessLevelRetrieval_controller access = new AccessLevelRetrieval_controller();

    public String[] getAccessLevel()
    {
        return access.getAccessLevel();
    }

}

//DROPDOWN BOX CONTROLLER
class AccessLevelRetrieval_controller
{
    public AccessLevelRetrieval_controller()
    {

    }

    AccessLevelRetrieval access = new AccessLevelRetrieval();

    public String[] getAccessLevel()
    {
        return access.getAccessLevels();
    }

}

//DROPDOWN BOX ENTITY
class AccessLevelRetrieval 
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public String[] getAccessLevels() 
    {
        ArrayList<String> accessLevels = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "SELECT DISTINCT profile FROM user_profile";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                try (ResultSet resultSet = preparedStatement.executeQuery()) 
                {
                    while (resultSet.next()) 
                    {
                        accessLevels.add(resultSet.getString("profile"));
                    }
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return accessLevels.toArray(new String[0]);
    }
}

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
//CAFE OWNER HOMEPAGE
class HomePageCafe_Owner extends JPanel
{
    public HomePageCafe_Owner()
    {
        //create work slot button
        JButton btnCreateWorkSlots = new JButton("Create Work Slots");
        btnCreateWorkSlots.addActionListener(e -> {
            HomePageCafe_Owner_create_work_slots_boundary boundary = new HomePageCafe_Owner_create_work_slots_boundary();
            boundary.createWorkSlots();
        });

        //view work slot button
        JButton btnViewWorkSlots = new JButton("View Work Slots");
        btnViewWorkSlots.addActionListener(e -> {
            HomePageCafe_Owner_view_work_slots_boundary boundary = new HomePageCafe_Owner_view_work_slots_boundary();
            boundary.viewWorkSlots();
        });

        //update work slot button
        JButton btnUpdateWorkSlots = new JButton("Update Work Slots");
        btnUpdateWorkSlots.addActionListener(e -> {
            HomePageCafe_Owner_update_work_slots_boundary boundary = new HomePageCafe_Owner_update_work_slots_boundary();
            boundary.updateWorkSlot();
        });

        //delete work slot button
        JButton btnDeleteWorkSlots = new JButton("Delete Work Slots");
        btnDeleteWorkSlots.addActionListener(e -> {
            HomePageCafe_Owner_delete_work_slots_boundary boundary = new HomePageCafe_Owner_delete_work_slots_boundary();
            boundary.deleteWorkSlot();
        });

        //search work slot button
        JButton btnSearchWorkSlots = new JButton("Search For Work Slots");
        btnSearchWorkSlots.addActionListener(e -> {
            HomePageCafe_Owner_search_work_slots_boundary boundary = new HomePageCafe_Owner_search_work_slots_boundary();
            boundary.searchWorkSlot();
        });

        //adding the buttons
        add(btnCreateWorkSlots);
        add(btnViewWorkSlots);
        add(btnUpdateWorkSlots);
        add(btnDeleteWorkSlots);
        add(btnSearchWorkSlots);
    }
}

//CREATE WORK SLOT (DONE)
class HomePageCafe_Owner_create_work_slots_boundary
{
    public HomePageCafe_Owner_create_work_slots_boundary()
    {
        
    }

    HomePageCafe_Owner_create_work_slots_controller controller = new HomePageCafe_Owner_create_work_slots_controller();

    public void createWorkSlots() 
    {
        String username = " ";//cannot be null
        String date = JOptionPane.showInputDialog("Enter date:");
        String time = JOptionPane.showInputDialog("Enter time:");
        //String job_role = JOptionPane.showInputDialog("Enter job_role:");
        String[] jobRoleArray = {"Chef", "Waiter", "Cashier"};
            JComboBox<String> accessLevelComboBox = new JComboBox<>(jobRoleArray);

            int result = JOptionPane.showOptionDialog(
                    null,
                    accessLevelComboBox,
                    "Select Job Role",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    null
            );

        String jobRole;

        if (result == JOptionPane.OK_OPTION) 
        {
            jobRole = (String) accessLevelComboBox.getSelectedItem();
        } 

        else 
        {
            JOptionPane.showMessageDialog(
                    null,
                    "Operation canceled by the user",
                    "Canceled",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;  // Exit method if user cancels
        }

        String is_booked = "0";

        boolean success = controller.createWorkSlots(username, date, time, jobRole, is_booked);
        
        if (success) 
        {
            JOptionPane.showMessageDialog(null, "Work slot created successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Failed to create work slot.");
        }
    }
    
}

class HomePageCafe_Owner_create_work_slots_controller
{
    private HomePageCafe_Owner_create_work_slots_entity entity;

        public HomePageCafe_Owner_create_work_slots_controller() 
        {
            entity = new HomePageCafe_Owner_create_work_slots_entity();
        }

        public boolean createWorkSlots(String username, String date, String time, String job_role, String is_booked) 
        {
            boolean success = entity.createWorkSlot(username, date, time, job_role, is_booked);

            return success;
        }
}

class HomePageCafe_Owner_create_work_slots_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME1 = "root";
    private static final String PASSWORD1 = "sql123";

    public boolean createWorkSlot(String username, String date, String time, String job_role, String is_booked) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME1, PASSWORD1)) 
        {
            String sql = "INSERT INTO work_slots (username, date, time, job_role, is_booked) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, date);
                preparedStatement.setString(3, time);
                preparedStatement.setString(4, job_role);
                preparedStatement.setString(5, is_booked);

                int rowsInserted = preparedStatement.executeUpdate();

                return rowsInserted > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

    }
}

//VIEW WORK SLOT (DONE)
class HomePageCafe_Owner_view_work_slots_boundary
{
    public HomePageCafe_Owner_view_work_slots_boundary()
    {

    }

    HomePageCafe_Owner_view_work_slots_controller controller = new HomePageCafe_Owner_view_work_slots_controller();

    public void viewWorkSlots()
    {
        try
        {
            ResultSet resultSet = controller.viewWorkSlots();

            // Process the ResultSet and display work slots
            StringBuilder workSlotsData = new StringBuilder();
            while (resultSet.next())
            {
                String id = resultSet.getString("work_slot_id");
                String username = resultSet.getString("username");
                String date = resultSet.getString("date");
                String time = resultSet.getString("time");
                String jobRole = resultSet.getString("job_role");
                boolean isBooked = resultSet.getBoolean("is_booked");

                workSlotsData.append("Work slot: ").append(id).append(".) ")
                        .append("Username: ").append(username)
                        .append(", Date: ").append(date)
                        .append(", Time: ").append(time)
                        .append(", Job Role: ").append(jobRole)
                        .append(", Booked: ").append(isBooked ? 1 : 0)
                        .append("\n");
            }

            // Use a JScrollPane to make the JOptionPane scrollable
            JTextArea textArea = new JTextArea(workSlotsData.toString());
            JScrollPane scrollPane = new JScrollPane(textArea);

            JDialog dialog = new JDialog();
            dialog.setTitle("Work Slots");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.add(scrollPane);
            dialog.setSize(700, 700);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);


            // Display work slots data in a JOptionPane with scroll pane
            //JOptionPane.showMessageDialog(null, scrollPane, "Work Slots", JOptionPane.INFORMATION_MESSAGE);
            // Display work slots data in a JOptionPane
            //JOptionPane.showMessageDialog(null, workSlotsData.toString(), "Work Slots", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to retrieve work slots.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class HomePageCafe_Owner_view_work_slots_controller
{
    private HomePageCafe_Owner_view_work_slots_entity entity;

    public HomePageCafe_Owner_view_work_slots_controller()
    {
        entity = new HomePageCafe_Owner_view_work_slots_entity();
    }

    public ResultSet viewWorkSlots()
    {
        try
        {
           return entity.retrieveWorkSlots();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }

    }
}

class HomePageCafe_Owner_view_work_slots_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public ResultSet retrieveWorkSlots() throws SQLException
    {
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT * FROM work_slots");
    }
}

//UPDATE WORK SLOT (DONE)
class HomePageCafe_Owner_update_work_slots_boundary
{
    public HomePageCafe_Owner_update_work_slots_boundary()
    {

    }

    HomePageCafe_Owner_update_work_slots_controller controller = new HomePageCafe_Owner_update_work_slots_controller();

     public void updateWorkSlot() 
    {
        try
        {
            // Get inputs from user (similar to the create work slot method)
            String idString = JOptionPane.showInputDialog("Enter ID of the work slot to manage:");
            int id = Integer.parseInt(idString);
            String date = JOptionPane.showInputDialog("Enter date of the work slot to update:");
            String time = JOptionPane.showInputDialog("Enter time of the work slot to update:");
            //String newJobRole = JOptionPane.showInputDialog("Enter new job role:");
            String[] jobRoleArray = {"Chef", "Waiter", "Cashier"};
                JComboBox<String> accessLevelComboBox = new JComboBox<>(jobRoleArray);

                int result = JOptionPane.showOptionDialog(
                        null,
                        accessLevelComboBox,
                        "Select new Job Role",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        null
                );

            String jobRole;

            if (result == JOptionPane.OK_OPTION) 
            {
                jobRole = (String) accessLevelComboBox.getSelectedItem();
            } 

            else 
            {
                JOptionPane.showMessageDialog(
                        null,
                        "Operation canceled by the user",
                        "Canceled",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;  // Exit method if user cancels
            }
            String newIsBooked = "0";

            boolean success = controller.updateWorkSlot(id, date, time, jobRole, newIsBooked);
            
            if (success) 
            {
                JOptionPane.showMessageDialog(null, "Work slot updated successfully!");
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "Failed to update work slot.");
            }
        }   
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, "Failed to update work slot.");
        }
    }
}

class HomePageCafe_Owner_update_work_slots_controller
{
    private HomePageCafe_Owner_update_work_slots_entity entity;

    public HomePageCafe_Owner_update_work_slots_controller() 
    {
        entity = new HomePageCafe_Owner_update_work_slots_entity();
    }

    public boolean updateWorkSlot(int id, String date, String time, String newJobRole, String newIsBooked) 
    {
        boolean success = entity.updateWorkSlot(id, date, time, newJobRole, newIsBooked);
        
        return success;
    }
}

class HomePageCafe_Owner_update_work_slots_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public boolean updateWorkSlot(int id, String date, String time, String newJobRole, String newIsBooked) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "UPDATE work_slots SET time = ?, job_role = ?, is_booked = ?, date = ? WHERE work_slot_id = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, time);
                preparedStatement.setString(2, newJobRole);
                preparedStatement.setString(3, newIsBooked);
                preparedStatement.setString(4, date);
                preparedStatement.setInt(5, id);

                int rowsUpdated = preparedStatement.executeUpdate();

                return rowsUpdated > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}

//DELETE WORK SLOT (DONE)
class HomePageCafe_Owner_delete_work_slots_boundary
{
    public HomePageCafe_Owner_delete_work_slots_boundary()
    {

    }
    
    HomePageCafe_Owner_delete_work_slots_controller controller = new HomePageCafe_Owner_delete_work_slots_controller();

    public void deleteWorkSlot() 
    {
        // Get inputs from user
        String idString = JOptionPane.showInputDialog("Enter ID of the work slot to delete:");
        int workSlotId = Integer.parseInt(idString);

        boolean success = controller.deleteWorkSlot(workSlotId);
        
        if (success) 
        {
            JOptionPane.showMessageDialog(null, "Work slot deleted successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Failed to delete work slot.");
        }
    }

}

class HomePageCafe_Owner_delete_work_slots_controller
{
    private HomePageCafe_Owner_delete_work_slots_entity entity;

    public HomePageCafe_Owner_delete_work_slots_controller() 
    {
        entity = new HomePageCafe_Owner_delete_work_slots_entity();
    }

    public boolean deleteWorkSlot(int workSlotId) 
    {
        boolean success = entity.deleteWorkSlot(workSlotId);

        return success;
    }
}

class HomePageCafe_Owner_delete_work_slots_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public boolean deleteWorkSlot(int workSlotId) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "DELETE FROM work_slots WHERE work_slot_id = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setInt(1, workSlotId);

                int rowsDeleted = preparedStatement.executeUpdate();

                return rowsDeleted > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}

//SEARCH WORK SLOT (DONE)
class HomePageCafe_Owner_search_work_slots_boundary
{
    public HomePageCafe_Owner_search_work_slots_boundary()
    {

    }

    HomePageCafe_Owner_search_work_slots_controller controller = new HomePageCafe_Owner_search_work_slots_controller();

    public void searchWorkSlot() 
    {
        // Get input from user
        String date = JOptionPane.showInputDialog("Enter date of the work slot to search:");

        ArrayList<WorkSlot> workSlots = controller.searchWorkSlot(date);
        
        if (!workSlots.isEmpty()) 
        {
            StringBuilder result = new StringBuilder();
            // Display the found work slots
            for (WorkSlot slot : workSlots) {
                result.append("Work Slot ID: ").append(slot.getWorkSlotId()).append("\n");
                result.append("Username: ").append(slot.getUsername()).append("\n");
                result.append("Date: ").append(slot.getDate()).append("\n");
                result.append("Time: ").append(slot.getTime()).append("\n");
                result.append("Job Role: ").append(slot.getJobRole()).append("\n");
                result.append("Is Booked: ").append(slot.getIsBooked()).append("\n");
                result.append("------------\n");
            }

            // Create a custom JFrame with a JTextArea within JScrollPane
            JFrame frame = new JFrame("Search Results");
            JTextArea textArea = new JTextArea(result.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane);
            frame.setSize(700, 700);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            //JOptionPane.showMessageDialog(null, result.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "No work slots found for the given date.");
        }
    }
}

class HomePageCafe_Owner_search_work_slots_controller
{
    private HomePageCafe_Owner_search_work_slots_entity entity;

    public HomePageCafe_Owner_search_work_slots_controller() 
    {
        entity = new HomePageCafe_Owner_search_work_slots_entity();
    }

    public ArrayList<WorkSlot> searchWorkSlot(String date) 
    {

        ArrayList<WorkSlot> workSlots = entity.searchWorkSlots(date);
        return workSlots;
    }
}

class HomePageCafe_Owner_search_work_slots_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public ArrayList<WorkSlot> searchWorkSlots(String date) 
    {
        ArrayList<WorkSlot> workSlots = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "SELECT * FROM work_slots WHERE date = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, date);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    WorkSlot slot = new WorkSlot();
                    slot.setWorkSlotId(resultSet.getInt("work_slot_id"));
                    slot.setUsername(resultSet.getString("username"));
                    slot.setDate(resultSet.getString("date"));
                    slot.setTime(resultSet.getString("time"));
                    slot.setJobRole(resultSet.getString("job_role"));
                    slot.setIsBooked(resultSet.getString("is_booked"));

                    workSlots.add(slot);
                }
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return workSlots;
    }
}

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
//CAFE MANAGER HOMEPAGE
class HomePageCafe_Manager extends JPanel
{
    public HomePageCafe_Manager()
    {
        //Directing to the bid boundary
        JButton btnApproveBid = new JButton("Bids");
        btnApproveBid.addActionListener(e -> {
            HomePageCafe_Manager_approve_bid_boundary homepg = new HomePageCafe_Manager_approve_bid_boundary();
            homepg.openBidPage();
        });

        //Assign staff to work slot button
        JButton btnAssignStaff = new JButton("Assign Staff");
        btnAssignStaff.addActionListener(e -> {
            HomePageCafe_Manager_Assign_employee_boundary boundary = new HomePageCafe_Manager_Assign_employee_boundary();
            boundary.AssignBid();
        });

        //search for workslots (based on date)
        JButton btnSearchForWorkSlot = new JButton("Search For Work Slot");
        btnSearchForWorkSlot.addActionListener(e -> {
            HomePageCafe_Manager_search_work_slot_boundary boundary = new HomePageCafe_Manager_search_work_slot_boundary();
            boundary.searchWorkSlot();
        });

        //search for all unoccupied work slots
        JButton btnViewAllUnoccupiedWorkSlot = new JButton("View All Unoccupied Work Slot");
        btnViewAllUnoccupiedWorkSlot.addActionListener(e -> {
            HomePageCafe_Manager_view_unoccupied_work_slot_boundary boundary = new HomePageCafe_Manager_view_unoccupied_work_slot_boundary();
            boundary.viewWorkSlots();
        });

        JButton btnViewToBeApprovedWorkSlot = new JButton("View Work Slots To Be Approved");
        btnViewToBeApprovedWorkSlot.addActionListener(e -> {
            HomePageCafe_Manager_view_work_slot_to_be_approved_boundary boundary = new HomePageCafe_Manager_view_work_slot_to_be_approved_boundary();
            boundary.viewWorkSlots();
        });


        //adding buttons
        add(btnApproveBid);
        add(btnAssignStaff);
        add(btnSearchForWorkSlot);
        add(btnViewAllUnoccupiedWorkSlot);
        add(btnViewToBeApprovedWorkSlot);

    }
}

//APPROVE AND REJECT BID 
class HomePageCafe_Manager_approve_bid_boundary extends JPanel
{
    protected JPanel panel;

    public void openBidPage()
    {
        JFrame bidsFrame = new JFrame("Bids page");

        //approve bid button (setting is_booked = 1 in work_slots database)
        JButton btnToApproveBid = new JButton("Approve bid");
        btnToApproveBid.addActionListener(e -> {
            HomePageCafe_Manager_approve_bid_boundary2 boundary2 = new HomePageCafe_Manager_approve_bid_boundary2();
            boundary2.approveBid();
        });

        //reject bid button (setting name  = " " in work_slots database)
        JButton btnToRejectBid = new JButton("Reject bid");
        btnToRejectBid.addActionListener(e -> {
            HomePageCafe_Manager_reject_bid_boundary boundary = new HomePageCafe_Manager_reject_bid_boundary();
            boundary.rejectBid();
        });

        // Create a panel to hold the buttons and set its layout to FlowLayout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(btnToApproveBid);
        buttonPanel.add(btnToRejectBid);

        // Set the content pane of the frame to the button panel
        bidsFrame.setContentPane(buttonPanel);

        //setting the page configurations
        bidsFrame.pack();
        bidsFrame.setVisible(true);
    }
}

//APPROVE BID (DONE)
class HomePageCafe_Manager_approve_bid_boundary2
{
    public HomePageCafe_Manager_approve_bid_boundary2()
    {
        
    }

    HomePageCafe_Manager_approve_bid_controller controller = new HomePageCafe_Manager_approve_bid_controller();

    public void approveBid()
    {
        // Get inputs from user (similar to the create work slot method)
        String input = JOptionPane.showInputDialog("Enter work slot id");
        int workID = Integer.parseInt(input);
        String newIsBooked = "1";

        boolean success = controller.approveBid(workID, newIsBooked);
        
        if (success) 
        {
            JOptionPane.showMessageDialog(null, "Bid approved successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Failed to approve bid.");
        }
    }
}

class HomePageCafe_Manager_approve_bid_controller
{ 
    HomePageCafe_Manager_approve_bid_entity entity;

    public HomePageCafe_Manager_approve_bid_controller()
    {
        entity = new HomePageCafe_Manager_approve_bid_entity();
    }

    public boolean approveBid(int workID, String newIsBooked)
    {
        boolean success = entity.updateWorkSlot(workID, newIsBooked);

        return success;
    }
}

class HomePageCafe_Manager_approve_bid_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public boolean updateWorkSlot(int workID, String newIsBooked) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "UPDATE work_slots SET is_booked = ? WHERE work_slot_id = ?";
            String sql2 = "UPDATE user_accounts SET bids = bids - 1 WHERE username = (SELECT username FROM work_slots WHERE work_slot_id = ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 PreparedStatement preparedStatement2 = connection.prepareStatement(sql2)) 
            {
                preparedStatement.setString(1, newIsBooked);
                preparedStatement.setInt(2, workID);

                preparedStatement2.setInt(1, workID);

                int rowsUpdated = preparedStatement.executeUpdate();
                int rowsUpdated2 = preparedStatement2.executeUpdate();

                return rowsUpdated > 0 && rowsUpdated2 > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}

//REJECT BID (DONE)
class HomePageCafe_Manager_reject_bid_boundary
{
    public HomePageCafe_Manager_reject_bid_boundary()
    {

    }

    HomePageCafe_Manager_reject_bid_controller controller = new HomePageCafe_Manager_reject_bid_controller();

    public void rejectBid()
    {
        // Get inputs from user (similar to the create work slot method)
        String input = JOptionPane.showInputDialog("Enter work slot id");
        int workId = Integer.parseInt(input);
        String newIsBooked = "0";

        boolean success = controller.rejectBid(workId, newIsBooked);
        
        if (success) 
        {
            JOptionPane.showMessageDialog(null, "Bid rejected successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Failed to reject bid.");
        }
    }
}

class HomePageCafe_Manager_reject_bid_controller
{
    HomePageCafe_Manager_reject_bid_entity entity;

    public HomePageCafe_Manager_reject_bid_controller()
    {
        entity = new HomePageCafe_Manager_reject_bid_entity();
    }

    public boolean rejectBid(int workId, String newIsBooked)
    {
        boolean success = entity.updateWorkSlot(workId, newIsBooked);
        
        return success;
    }
}

class HomePageCafe_Manager_reject_bid_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public boolean updateWorkSlot(int workId, String newIsBooked) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "UPDATE work_slots SET username = ?, is_Booked = ? WHERE work_slot_id = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, " ");
                preparedStatement.setInt(3, workId);
                preparedStatement.setString(2, newIsBooked);

                int rowsUpdated = preparedStatement.executeUpdate();

                return rowsUpdated > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}

//ASSIGN EMPLOYEE (DONE)
class HomePageCafe_Manager_Assign_employee_boundary
{
    public HomePageCafe_Manager_Assign_employee_boundary()
    {

    }

    HomePageCafe_Manager_Assign_employee_controller controller = new HomePageCafe_Manager_Assign_employee_controller();

    public void AssignBid()
    {
        // Get inputs from user (similar to the create work slot method)
        String name = JOptionPane.showInputDialog("Enter username of employee\nto assign:");
        String input = JOptionPane.showInputDialog("Enter work slot id:");
        int workid = Integer.parseInt(input);
        String newIsBooked = "1";

        boolean success = controller.AssignBid(name, workid, newIsBooked);
        
        if (success) 
        {
            JOptionPane.showMessageDialog(null, "Bid assigned successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Failed to assign bid.");
        }
    }
}

class HomePageCafe_Manager_Assign_employee_controller
{
    HomePageCafe_Manager_Assign_employee_entity entity;

    public HomePageCafe_Manager_Assign_employee_controller()
    {
        entity = new HomePageCafe_Manager_Assign_employee_entity();
    }

    public boolean AssignBid(String name, int workId, String newIsBooked)
    {

        boolean success = entity.updateWorkSlot(name, workId, newIsBooked);
        
        return success;
    }
}

class HomePageCafe_Manager_Assign_employee_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public boolean updateWorkSlot(String name, int workId, String newIsBooked) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "UPDATE work_slots SET username = ?, is_booked = ? WHERE work_slot_id = ? AND is_booked = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, newIsBooked);
                preparedStatement.setInt(3, workId);
                preparedStatement.setString(4, "0");

                int rowsUpdated = preparedStatement.executeUpdate();

                return rowsUpdated > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}

//SEARCH WORK SLOT BASED ON DATE (DONE)
class HomePageCafe_Manager_search_work_slot_boundary
{
    public HomePageCafe_Manager_search_work_slot_boundary()
    {

    }

    HomePageCafe_Manager_search_work_slot_controller controller = new HomePageCafe_Manager_search_work_slot_controller();

    public void searchWorkSlot() 
    {
        // Get input from user
        String date = JOptionPane.showInputDialog("Enter date of the work slot to search:");

        ArrayList<WorkSlot> workSlots = controller.searchWorkSlot(date);
        
        if (!workSlots.isEmpty()) 
        {
            StringBuilder result = new StringBuilder();
            // Display the found work slots
            for (WorkSlot slot : workSlots) {
                result.append("Work Slot ID: ").append(slot.getWorkSlotId()).append("\n");
                result.append("Username: ").append(slot.getUsername()).append("\n");
                result.append("Date: ").append(slot.getDate()).append("\n");
                result.append("Time: ").append(slot.getTime()).append("\n");
                result.append("Job Role: ").append(slot.getJobRole()).append("\n");
                result.append("Is Booked: ").append(slot.getIsBooked()).append("\n");
                result.append("------------\n");
            }

            // Create a custom JFrame with a JTextArea within JScrollPane
            JFrame frame = new JFrame("Search Results");
            JTextArea textArea = new JTextArea(result.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane);
            frame.setSize(700, 700);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);

            //JOptionPane.showMessageDialog(null, result.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "No work slots found for the given date.");
        }
    }
}

class HomePageCafe_Manager_search_work_slot_controller
{
    private HomePageCafe_Manager_search_work_slot_entity entity;

    public HomePageCafe_Manager_search_work_slot_controller() 
    {
        entity = new HomePageCafe_Manager_search_work_slot_entity();
    }

    public ArrayList<WorkSlot> searchWorkSlot(String date) 
    {
        ArrayList<WorkSlot> workSlots = entity.searchWorkSlots(date);
        
        return workSlots;
    }
}

class HomePageCafe_Manager_search_work_slot_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public ArrayList<WorkSlot> searchWorkSlots(String date) 
    {
        ArrayList<WorkSlot> workSlots = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "SELECT * FROM work_slots WHERE date = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, date);
                //preparedStatement.setString(2, " ");

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    WorkSlot slot = new WorkSlot();
                    slot.setWorkSlotId(resultSet.getInt("work_slot_id"));
                    slot.setUsername(resultSet.getString("username"));
                    slot.setDate(resultSet.getString("date"));
                    slot.setTime(resultSet.getString("time"));
                    slot.setJobRole(resultSet.getString("job_role"));
                    slot.setIsBooked(resultSet.getString("is_booked"));

                    workSlots.add(slot);
                }
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return workSlots;
    }
}

//VIEW ALL UNOCCUPIED WORK SLOT (DONE)
class HomePageCafe_Manager_view_unoccupied_work_slot_boundary
{
    public HomePageCafe_Manager_view_unoccupied_work_slot_boundary()
    {

    }

    HomePageCafe_Manager_view_unoccupied_work_slot_controller controller = new HomePageCafe_Manager_view_unoccupied_work_slot_controller();

    public void viewWorkSlots()
    {
        try
        {
            ResultSet resultSet = controller.viewWorkSlots();

            // Process the ResultSet and display work slots
            StringBuilder workSlotsData = new StringBuilder();
            while (resultSet.next())
            {
                String id = resultSet.getString("work_slot_id");
                String username = resultSet.getString("username");
                String date = resultSet.getString("date");
                String time = resultSet.getString("time");
                String jobRole = resultSet.getString("job_role");
                boolean isBooked = resultSet.getBoolean("is_booked");

                workSlotsData.append("Work slot: ").append(id).append(".) ")
                        .append("Username: ").append(username)
                        .append(", Date: ").append(date)
                        .append(", Time: ").append(time)
                        .append(", Job Role: ").append(jobRole)
                        .append(", Booked: ").append(isBooked ? 1 : 0)
                        .append("\n");
            }

            // Create a custom dialog with a JTextArea inside a JScrollPane
            JDialog dialog = new JDialog();
            dialog.setTitle("Work Slots");
            dialog.setSize(700, 700);
            dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JTextArea textArea = new JTextArea(workSlotsData.toString());
            textArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(textArea);
            dialog.add(scrollPane);

            dialog.setVisible(true);
            // Display work slots data in a JOptionPane
            //JOptionPane.showMessageDialog(null, workSlotsData.toString(), "Work Slots", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to retrieve work slots.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class HomePageCafe_Manager_view_unoccupied_work_slot_controller
{
    private HomePageCafe_Manager_view_unoccupied_work_slot_entity entity;

    public HomePageCafe_Manager_view_unoccupied_work_slot_controller()
    {
        entity = new HomePageCafe_Manager_view_unoccupied_work_slot_entity();
    }

    public ResultSet viewWorkSlots()
    {
        try
        {
            ResultSet resultSet = entity.retrieveWorkSlots();
            return resultSet;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

class HomePageCafe_Manager_view_unoccupied_work_slot_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public ResultSet retrieveWorkSlots() throws SQLException
    {
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT * FROM work_slots WHERE username = ' ' AND is_booked = '0'");
    }
}

//VIEW ALL WORKSLOTS TO BE APPROVED
class HomePageCafe_Manager_view_work_slot_to_be_approved_boundary
{
    public HomePageCafe_Manager_view_work_slot_to_be_approved_boundary()
    {

    }

    HomePageCafe_Manager_view_work_slot_to_be_approved_controller controller = new HomePageCafe_Manager_view_work_slot_to_be_approved_controller();

    public void viewWorkSlots()
    {
        try
        {
            ResultSet resultSet = controller.viewWorkSlots();

            // Process the ResultSet and display work slots
            StringBuilder workSlotsData = new StringBuilder();
            while (resultSet.next())
            {
                String id = resultSet.getString("work_slot_id");
                String username = resultSet.getString("username");
                String date = resultSet.getString("date");
                String time = resultSet.getString("time");
                String jobRole = resultSet.getString("job_role");
                boolean isBooked = resultSet.getBoolean("is_booked");

                workSlotsData.append("Work slot: ").append(id).append(".) ")
                        .append("Username: ").append(username)
                        .append(", Date: ").append(date)
                        .append(", Time: ").append(time)
                        .append(", Job Role: ").append(jobRole)
                        .append(", Booked: ").append(isBooked ? 1 : 0)
                        .append("\n");
            }

            // Display work slots data in a JOptionPane
            JOptionPane.showMessageDialog(null, workSlotsData.toString(), "Work Slots", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to retrieve work slots.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class HomePageCafe_Manager_view_work_slot_to_be_approved_controller
{
    private HomePageCafe_Manager_view_work_slot_to_be_approved_entity entity;

    public HomePageCafe_Manager_view_work_slot_to_be_approved_controller()
    {
        entity = new HomePageCafe_Manager_view_work_slot_to_be_approved_entity();
    }

    public ResultSet viewWorkSlots()
    {
        try
        {
            ResultSet resultSet = entity.retrieveWorkSlots();
            return resultSet;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

class HomePageCafe_Manager_view_work_slot_to_be_approved_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public ResultSet retrieveWorkSlots() throws SQLException
    {
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT * FROM work_slots WHERE username <> ' ' AND is_booked = '0'");
    }
}

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
//CAFE STAFF
class HomePageCafe_Staff extends JPanel
{
    public HomePageCafe_Staff()
    {
        //search available work slots button
        JButton btnViewAvailableWorkSlots = new JButton("View Available Work Slots");
        btnViewAvailableWorkSlots.addActionListener(e -> {
            HomePageCafe_Staff_view_available_workslots_boundary boundary = new HomePageCafe_Staff_view_available_workslots_boundary();
            boundary.viewWorkSlot();
        });

        //create a bid button (adding their username to the username columns as a bid)
        JButton btnCreateABid = new JButton("Make A Bid");
        btnCreateABid.addActionListener(e -> {
            HomePageCafe_Staff_create_bid_boundary boundary = new HomePageCafe_Staff_create_bid_boundary();
            boundary.createBid();
        });

        //View my bids button
        JButton btnViewMyBids = new JButton("View My Bids");
        btnViewMyBids.addActionListener(e -> {
            HomePageCafe_Staff_view_my_bid_boundary boundary = new HomePageCafe_Staff_view_my_bid_boundary();
            boundary.viewBid();
        });

        //deleting a bid (replacing their username with a ' ')
        JButton btnDeleteBid = new JButton("Delete A Bid");
        btnDeleteBid.addActionListener(e -> {
            HomePageCafe_Staff_delete_a_bid_boundary boundary = new HomePageCafe_Staff_delete_a_bid_boundary();
            boundary.deleteBid();
        });

        //Indicate bidding amount
        JButton btnIndicateBiddingAmount = new JButton("Indicate Bidding Amount");
        btnIndicateBiddingAmount.addActionListener(e -> {
            HomePageCafe_Staff_indicate_bidding_amount_boundary boundary = new HomePageCafe_Staff_indicate_bidding_amount_boundary();
            boundary.indicateBidAmount();
        });

        //view approved bids
        JButton btnViewApprovedBids = new JButton("View My Approved Bids");
        btnViewApprovedBids.addActionListener(e -> {
            HomePageCafe_Staff_view_approved_bids_boundary boundary = new HomePageCafe_Staff_view_approved_bids_boundary();
            boundary.viewApprovedBids();
        });

        //search my bid 
        JButton btnSearchMyBids = new JButton("Search For Your Bid Here");
        btnSearchMyBids.addActionListener(e -> {
            HomePageCafe_Staff_search_my_bids_boundary boundary = new HomePageCafe_Staff_search_my_bids_boundary();
            boundary.searchBids();
        });

        JButton btnIndicateJobRole = new JButton("Indicate Job Role");
        btnIndicateJobRole.addActionListener(e -> {
            HomePageCafe_Staff_indicate_job_role_boundary boundary = new HomePageCafe_Staff_indicate_job_role_boundary();
            boundary.indicateJobRole();
        });

        //adding the buttons
        add(btnViewAvailableWorkSlots);
        add(btnCreateABid);
        add(btnViewMyBids);
        add(btnDeleteBid);
        add(btnIndicateBiddingAmount);
        add(btnViewApprovedBids);
        add(btnSearchMyBids);
        add(btnIndicateJobRole);
    }
}

//VIEW AVAILABLE WORK SLOTS (DONE)
class HomePageCafe_Staff_view_available_workslots_boundary
{
    public HomePageCafe_Staff_view_available_workslots_boundary()
    {

    }

    HomePageCafe_Staff_view_available_workslots_controller controller = new HomePageCafe_Staff_view_available_workslots_controller();

    public void viewWorkSlot() 
    {
        // Get input from user
        String username = LoginPage.getUsername();
        //String username = JOptionPane.showInputDialog("Enter username:");

        ArrayList<WorkSlot> workSlots = controller.viewWorkSlot(username);
        
        if (!workSlots.isEmpty()) 
        {
            StringBuilder result = new StringBuilder();
            // Display the found work slots
            for (WorkSlot slot : workSlots) {
                result.append("Work Slot ID: ").append(slot.getWorkSlotId()).append("\n");
                result.append("Username: ").append(slot.getUsername()).append("\n");
                result.append("Date: ").append(slot.getDate()).append("\n");
                result.append("Time: ").append(slot.getTime()).append("\n");
                result.append("Job Role: ").append(slot.getJobRole()).append("\n");
                result.append("Is Booked: ").append(slot.getIsBooked()).append("\n");
                result.append("------------\n");
            }
            // Create a custom JFrame with a JTextArea within JScrollPane
            JFrame frame = new JFrame("Available work slots");
            JTextArea textArea = new JTextArea(result.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane);
            frame.setSize(700, 700);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            //JOptionPane.showMessageDialog(null, result.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "No work slots found for the given date.");
        }
    }
}

class HomePageCafe_Staff_view_available_workslots_controller
{
    private HomePageCafe_Staff_view_available_workslots_entity entity;

    public HomePageCafe_Staff_view_available_workslots_controller()
    {
        entity = new HomePageCafe_Staff_view_available_workslots_entity();
    }

    public ArrayList<WorkSlot> viewWorkSlot(String username) 
    {
        ArrayList<WorkSlot> workSlots = entity.viewWorkSlots(username);
        
        return workSlots;
    }
}

class HomePageCafe_Staff_view_available_workslots_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public ArrayList<WorkSlot> viewWorkSlots(String username) 
    {
        ArrayList<WorkSlot> workSlots = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "SELECT * FROM work_slots WHERE username = ' ' AND job_role = (SELECT job_role FROM user_accounts WHERE username = ?)";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, username);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    WorkSlot slot = new WorkSlot();
                    slot.setWorkSlotId(resultSet.getInt("work_slot_id"));
                    slot.setUsername(resultSet.getString("username"));
                    slot.setDate(resultSet.getString("date"));
                    slot.setTime(resultSet.getString("time"));
                    slot.setJobRole(resultSet.getString("job_role"));
                    slot.setIsBooked(resultSet.getString("is_booked"));

                    workSlots.add(slot);
                }
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return workSlots;
    }
}

//CREATE BID (DONE)
class HomePageCafe_Staff_create_bid_boundary
{
    public HomePageCafe_Staff_create_bid_boundary()
    {

    }

    HomePageCafe_Staff_create_bid_controller controller = new HomePageCafe_Staff_create_bid_controller();

    public void createBid()
    {
        String username = LoginPage.getUsername();
        //System.out.println(username);

        String input = JOptionPane.showInputDialog("Enter work slot id:");
        
        //String username = JOptionPane.showInputDialog("Enter username:");

        try 
        {
            int bid_id = Integer.parseInt(input);
            boolean success = controller.createBid(bid_id, username);

            if (success) 
            {
                JOptionPane.showMessageDialog(null, "Bid created successfully!");
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "Failed to create bid.");
            }
        } 

        catch (NumberFormatException e) 
        {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer for work slot id.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class HomePageCafe_Staff_create_bid_controller
{
    private HomePageCafe_Staff_create_bid_entity entity;

    HomePageCafe_Staff_create_bid_controller()
    {
        entity = new HomePageCafe_Staff_create_bid_entity();
    }

    public boolean createBid(int bid_id, String username)
    {
        boolean success = entity.createBid(bid_id, username);
        
        return success;
    }
}

class HomePageCafe_Staff_create_bid_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME1 = "root";
    private static final String PASSWORD1 = "sql123";

    public boolean createBid(int bid_id, String username) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME1, PASSWORD1)) 
        {
            String sql = "UPDATE work_slots SET username = ? WHERE work_slot_id = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, bid_id);

                int rowsInserted = preparedStatement.executeUpdate();

                return rowsInserted > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}

//VIEW BIDS (DONE)
class HomePageCafe_Staff_view_my_bid_boundary
{
    public HomePageCafe_Staff_view_my_bid_boundary()
    {

    }

    HomePageCafe_Staff_view_my_bid_controller controller = new HomePageCafe_Staff_view_my_bid_controller();

    public void viewBid() 
    {
        // Get input from user
        String name = LoginPage.getUsername();
        //String name = JOptionPane.showInputDialog("Enter name used for bidding:");

        ArrayList<WorkSlot> workSlots = controller.viewBid(name);
        
        if (!workSlots.isEmpty()) 
        {
            StringBuilder result = new StringBuilder();

            // Display the found work slots
            for (WorkSlot slot : workSlots) {
                result.append("Work Slot ID: ").append(slot.getWorkSlotId()).append("\n");
                result.append("name: ").append(slot.getUsername()).append("\n");
                result.append("Date: ").append(slot.getDate()).append("\n");
                result.append("Time: ").append(slot.getTime()).append("\n");
                result.append("Job Role: ").append(slot.getJobRole()).append("\n");
                result.append("Is Booked: ").append(slot.getIsBooked()).append("\n");
                result.append("------------\n");
            }
            // Create a custom JFrame with a JTextArea within JScrollPane
            JFrame frame = new JFrame("My bids");
            JTextArea textArea = new JTextArea(result.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane);
            frame.setSize(700, 700);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            //JOptionPane.showMessageDialog(null, result.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "No work slots found for any of your bids.");
        }
    }

}

class HomePageCafe_Staff_view_my_bid_controller
{
    private HomePageCafe_Staff_view_my_bid_entity entity;

    public HomePageCafe_Staff_view_my_bid_controller() 
    {
        entity = new HomePageCafe_Staff_view_my_bid_entity();
    }

    public ArrayList<WorkSlot> viewBid(String name) 
    {

        ArrayList<WorkSlot> workSlots = entity.viewBid(name);
        
        return workSlots;
    }
}

class HomePageCafe_Staff_view_my_bid_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public ArrayList<WorkSlot> viewBid(String name) 
    {
        ArrayList<WorkSlot> workSlots = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "SELECT * FROM work_slots WHERE username = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, name);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    WorkSlot slot = new WorkSlot();
                    slot.setWorkSlotId(resultSet.getInt("work_slot_id"));
                    slot.setUsername(resultSet.getString("username"));
                    slot.setDate(resultSet.getString("date"));
                    slot.setTime(resultSet.getString("time"));
                    slot.setJobRole(resultSet.getString("job_role"));
                    slot.setIsBooked(resultSet.getString("is_booked"));

                    workSlots.add(slot);
                }
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return workSlots;
    }
}

//DELETE BID (DONE)
class HomePageCafe_Staff_delete_a_bid_boundary
{
    public HomePageCafe_Staff_delete_a_bid_boundary()
    {

    }

    HomePageCafe_Staff_delete_a_bid_controller controller = new HomePageCafe_Staff_delete_a_bid_controller();

    public void deleteBid() 
    {
        // Get inputs from user
        String name = " ";
        String work_slot_id = JOptionPane.showInputDialog("Enter work slot id:");
        //String date = JOptionPane.showInputDialog("Enter date of the work slot to delete:");
        //String time = JOptionPane.showInputDialog("Enter time of the work slot to delete:");
        int approval = 0;

        boolean success = controller.deleteBid(name, work_slot_id, approval);
        
        if (success) 
        {
            JOptionPane.showMessageDialog(null, "Bid deleted successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Failed to delete bid.\nYou have either entered wrong datatype \nOR\nYour bid has been approved");
        }
    }

}

class HomePageCafe_Staff_delete_a_bid_controller
{
    private HomePageCafe_Staff_delete_a_bid_entity entity;

        public HomePageCafe_Staff_delete_a_bid_controller() 
        {
            entity = new HomePageCafe_Staff_delete_a_bid_entity();
        }

        public boolean deleteBid(String username, String work_slot_id, int approval) 
        {
            boolean success = entity.deleteWorkSlot(username, work_slot_id, approval);
            
            return success;
        }
}

class HomePageCafe_Staff_delete_a_bid_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME1 = "root";
    private static final String PASSWORD1 = "sql123";

    public boolean deleteWorkSlot(String username, String work_slot_id, int approval) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME1, PASSWORD1)) 
        {
            String sql = "UPDATE work_slots SET username = ? WHERE work_slot_id = ? AND is_booked = ? ";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, work_slot_id);
                preparedStatement.setInt(3, approval);

                int rowsInserted = preparedStatement.executeUpdate();

                return rowsInserted > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

    }
}

//INDICATE BIDDING AMOUNT (DONE)
class HomePageCafe_Staff_indicate_bidding_amount_boundary
{
    public HomePageCafe_Staff_indicate_bidding_amount_boundary()
    {
        
    }

    HomePageCafe_Staff_indicate_bidding_amount_controller controller = new HomePageCafe_Staff_indicate_bidding_amount_controller();

    public void indicateBidAmount()
    {
        try
        {
            String username = LoginPage.getUsername();
            //String username = JOptionPane.showInputDialog("Enter your username:");
            String bidString = JOptionPane.showInputDialog("Enter bidding amount:");
            int bid = Integer.parseInt(bidString);

            boolean success = controller.indicateBidAmount(bid, username);

            if (success) 
            {
                JOptionPane.showMessageDialog(null, "Bidding amount updated successfully!");
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "Failed to update bidding amount.");
            }
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(null, "Invalid input for bidding amount. Please enter a valid number.");
        }
    }
}

class HomePageCafe_Staff_indicate_bidding_amount_controller
{
    private HomePageCafe_Staff_indicate_bidding_amount_entity entity;

    public HomePageCafe_Staff_indicate_bidding_amount_controller()
    {
        entity = new HomePageCafe_Staff_indicate_bidding_amount_entity();
    }

    public boolean indicateBidAmount(int bid, String username)
    {
        try
        {
            boolean success = entity.indicateBiddingAmount(bid, username);

            return success;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }
}

class HomePageCafe_Staff_indicate_bidding_amount_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME1 = "root";
    private static final String PASSWORD1 = "sql123";

    public boolean indicateBiddingAmount(int bid, String username) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME1, PASSWORD1)) 
        {
            String sql = "UPDATE user_accounts SET bids = ? WHERE username = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setInt(1, bid);
                preparedStatement.setString(2, username);

                int rowsInserted = preparedStatement.executeUpdate();

                return rowsInserted > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

    }
}

//VIEW APPROVED BIDS (DONE)
class HomePageCafe_Staff_view_approved_bids_boundary
{
    public HomePageCafe_Staff_view_approved_bids_boundary()
    {

    }

    HomePageCafe_Staff_view_approved_bids_controller controller = new HomePageCafe_Staff_view_approved_bids_controller();

    public void viewApprovedBids() 
    {
        // Get input from user
        String name = LoginPage.getUsername();
        //String name = JOptionPane.showInputDialog("Enter username:");
        int bid = 1;

        ArrayList<WorkSlot> workSlots = controller.viewApprovedBids(name, bid);
        
        if (!workSlots.isEmpty()) 
        {
            StringBuilder result = new StringBuilder();

            // Display the found work slots
            for (WorkSlot slot : workSlots) {
                result.append("Work Slot ID: ").append(slot.getWorkSlotId()).append("\n");
                result.append("name: ").append(slot.getUsername()).append("\n");
                result.append("Date: ").append(slot.getDate()).append("\n");
                result.append("Time: ").append(slot.getTime()).append("\n");
                result.append("Job Role: ").append(slot.getJobRole()).append("\n");
                result.append("Is Booked: ").append(slot.getIsBooked()).append("\n");
                result.append("------------\n");
            }
            // Create a custom JFrame with a JTextArea within JScrollPane
            JFrame frame = new JFrame("My approved bids");
            JTextArea textArea = new JTextArea(result.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane);
            frame.setSize(700, 700);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            //JOptionPane.showMessageDialog(null, result.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "No approved work slots found.");
        }
    }
}

class HomePageCafe_Staff_view_approved_bids_controller
{
    private HomePageCafe_Staff_view_approved_bids_entity entity;

    public HomePageCafe_Staff_view_approved_bids_controller() 
    {
        entity = new HomePageCafe_Staff_view_approved_bids_entity();
    }

    public ArrayList<WorkSlot> viewApprovedBids(String name, int bid) 
    {

        ArrayList<WorkSlot> workSlots = entity.searchBid(name, bid);
        
        if (!workSlots.isEmpty()) 
        {
            return workSlots;
        } 
        else 
        {
            return new ArrayList<>();
        }
    }
}

class HomePageCafe_Staff_view_approved_bids_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public ArrayList<WorkSlot> searchBid(String name, int bid) 
    {
        ArrayList<WorkSlot> workSlots = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "SELECT * FROM work_slots WHERE username = ? AND is_booked = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, name);
                preparedStatement.setInt(2, bid);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    WorkSlot slot = new WorkSlot();
                    slot.setWorkSlotId(resultSet.getInt("work_slot_id"));
                    slot.setUsername(resultSet.getString("username"));
                    slot.setDate(resultSet.getString("date"));
                    slot.setTime(resultSet.getString("time"));
                    slot.setJobRole(resultSet.getString("job_role"));
                    slot.setIsBooked(resultSet.getString("is_booked"));

                    workSlots.add(slot);
                }
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return workSlots;
    }
}

//SEARCH BID I MADE (Using name, date) (DONE)
class HomePageCafe_Staff_search_my_bids_boundary
{
    public HomePageCafe_Staff_search_my_bids_boundary()
    {

    }
    HomePageCafe_Staff_search_my_bids_controller controller = new HomePageCafe_Staff_search_my_bids_controller();

    public void searchBids() 
    {
        // Get input from user
        String name = LoginPage.getUsername();
        //String name = JOptionPane.showInputDialog("Enter name:");
        String date = JOptionPane.showInputDialog("Enter date of the work slot to search:");

        ArrayList<WorkSlot> workSlots = controller.searchBids(name, date);

        if (workSlots == null)
        {
            JOptionPane.showMessageDialog(null, "No work slots found for the given date.");
        }
        
        if (!workSlots.isEmpty()) 
        {
            StringBuilder result = new StringBuilder();
            // Display the found work slots
            for (WorkSlot slot : workSlots) {
                result.append("Work Slot ID: ").append(slot.getWorkSlotId()).append("\n");
                result.append("Username: ").append(slot.getUsername()).append("\n");
                result.append("Date: ").append(slot.getDate()).append("\n");
                result.append("Time: ").append(slot.getTime()).append("\n");
                result.append("Job Role: ").append(slot.getJobRole()).append("\n");
                result.append("Is Booked: ").append(slot.getIsBooked()).append("\n");
                result.append("------------\n");
            }

            // Create a custom JFrame with a JTextArea within JScrollPane
            JFrame frame = new JFrame("Available work slots");
            JTextArea textArea = new JTextArea(result.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane);
            frame.setSize(700, 700);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            //JOptionPane.showMessageDialog(null, result.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } 

        else
        {
            JOptionPane.showMessageDialog(null, "No work slots found for the given date.");
        }
    }
}

class HomePageCafe_Staff_search_my_bids_controller
{
    private HomePageCafe_Staff_search_my_bids_entity entity;

    public HomePageCafe_Staff_search_my_bids_controller() 
    {
        entity = new HomePageCafe_Staff_search_my_bids_entity();
    }

    public ArrayList<WorkSlot> searchBids(String name, String date) 
    {

        ArrayList<WorkSlot> workSlots = entity.searchBids(name, date);
        
        if (!workSlots.isEmpty()) 
        {
            return workSlots;
        } 
        else 
        {
            return null;
        }
    }
}

class HomePageCafe_Staff_search_my_bids_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sql123";

    public ArrayList<WorkSlot> searchBids(String name, String date) 
    {
        ArrayList<WorkSlot> workSlots = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) 
        {
            String sql = "SELECT * FROM work_slots WHERE date = ? AND username = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, date);
                preparedStatement.setString(2, name);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    WorkSlot slot = new WorkSlot();
                    slot.setWorkSlotId(resultSet.getInt("work_slot_id"));
                    slot.setUsername(resultSet.getString("username"));
                    slot.setDate(resultSet.getString("date"));
                    slot.setTime(resultSet.getString("time"));
                    slot.setJobRole(resultSet.getString("job_role"));
                    slot.setIsBooked(resultSet.getString("is_booked"));

                    workSlots.add(slot);
                }
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return workSlots;
    }
}

//INDICATE JOB ROLE (DONE)
class HomePageCafe_Staff_indicate_job_role_boundary
{
    public HomePageCafe_Staff_indicate_job_role_boundary()
    {
        
    }

    HomePageCafe_Staff_indicate_job_role_controller controller = new HomePageCafe_Staff_indicate_job_role_controller();

    public void indicateJobRole()
    {
        try
        {
            String username = LoginPage.getUsername();
            //String username = JOptionPane.showInputDialog("Enter your username:");

            String[] jobRoleArray = {"Chef", "Waiter", "Cashier"};
            JComboBox<String> accessLevelComboBox = new JComboBox<>(jobRoleArray);

            int result = JOptionPane.showOptionDialog(
                    null,
                    accessLevelComboBox,
                    "Select Job Role",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    null
            );

            String jobRole;

            if (result == JOptionPane.OK_OPTION) 
            {
                jobRole = (String) accessLevelComboBox.getSelectedItem();
            } 

            else 
            {
                JOptionPane.showMessageDialog(
                        null,
                        "Operation canceled by the user",
                        "Canceled",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;  // Exit method if user cancels
            }


            boolean success = controller.indicateJobRole(username, jobRole);

            if (success) 
            {
                JOptionPane.showMessageDialog(null, "Job role updated successfully!");
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "Failed to update job role.");
            }
        }

        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(null, "Invalid job role Please choose a valid job role.");
        }
    }
}

class HomePageCafe_Staff_indicate_job_role_controller
{
    private HomePageCafe_Staff_indicate_job_role_entity entity;

    public HomePageCafe_Staff_indicate_job_role_controller()
    {
        entity = new HomePageCafe_Staff_indicate_job_role_entity();
    }

    public boolean indicateJobRole(String username, String jobRole)
    {
        try
        {
            boolean success = entity.indicateJobRole(username, jobRole);

            return success;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }
}

class HomePageCafe_Staff_indicate_job_role_entity
{
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CafeSystem";
    private static final String USERNAME1 = "root";
    private static final String PASSWORD1 = "sql123";

    public boolean indicateJobRole(String username, String jobRole) 
    {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME1, PASSWORD1)) 
        {
            String sql = "UPDATE user_accounts SET job_role = ? WHERE username = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) 
            {
                preparedStatement.setString(1, jobRole);
                preparedStatement.setString(2, username);

                int rowsInserted = preparedStatement.executeUpdate();

                return rowsInserted > 0;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
class User 
{
    private int adminId;
    private String username;
    private String fullName;
    private String email;
    private String accessLevel;

    public User(int adminId, String username, String fullName, String email, String accessLevel) 
    {
        this.adminId = adminId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.accessLevel = accessLevel;
    }

    public User(String username)
    {
        this.username = username;
    }

    // Getter methods for user attributes
    public int getAdminId() 
    {
        return adminId;
    }

    public String getUsername() 
    {
        return username;
    }

    public String getFullName() 
    {
        return fullName;
    }

    public String getEmail() 
    {
        return email;
    }

    public String getAccessLevel() 
    {
        return accessLevel;
    }
}

class WorkSlot
{
    private int workSlotId;
    private String username;
    private String date;
    private String time;
    private String jobRole;
    private String isBooked;

    public WorkSlot()
    {

    }

    public WorkSlot (int workSlotId, String username, String date, String time, String jobRole, String isBooked)
    {
        this.workSlotId = workSlotId;
        this.username = username;
        this.date = date;
        this.time = time;
        this.jobRole = jobRole;
        this.isBooked = isBooked;
    }

    // Getters and setters for the work slot attributes
    public int getWorkSlotId() {
        return workSlotId;
    }

    public void setWorkSlotId(int workSlotId) {
        this.workSlotId = workSlotId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getIsBooked() {
        return isBooked;
    }

    public void setIsBooked(String isBooked) {
        this.isBooked = isBooked;
    }

}

class User_profile
{
    int id;
    String profileName;

    public User_profile()
    {

    }

    public User_profile(int id, String profileName)
    {
        this.id = id;
        this.profileName = profileName;
    }

    public int getID()
    {
        return id;
    }

    public String getProfileName()
    {
        return profileName;
    }

    public void setID(int id)
    {
        this.id = id;
    }

    public void setProfileName(String profileName)
    {
        this.profileName = profileName;
    }

}
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//

//main method to execute program
public class cafeSystem
{
    public static void main(String[] args) 
    {
       //running the program
       LoginPage startProgram = new LoginPage();

    }
}

