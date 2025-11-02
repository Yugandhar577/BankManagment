public class AuthService {
    private UserDAO userDAO = new UserDAO();

    public boolean register(String name, String email, String password) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required!");
            return false;
        }

        User user = new User(name, email, password);
        boolean result = userDAO.register(user);
        if (result) System.out.println("Registration successful!");
        else System.out.println("Registration failed. Email might already exist.");
        return result;
    }

    public User login(String email, String password) {
        User u = userDAO.login(email, password);
        if (u == null) {
            System.out.println("Invalid email or password.");
            return null;
        }
        System.out.println("Welcome, " + u.name + "!");
        return u;
    }
}
