
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

// BOOK CLASS
class Book {
    int id;
    String title, author;
    boolean available = true;

    Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }
}

// USER CLASS
class User {
    String username, password;

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

// ISSUE CLASS
class Issue {
    int bookId;
    String username;
    LocalDate issueDate, dueDate, returnDate;

    Issue(int bookId, String username) {
        this.bookId = bookId;
        this.username = username;
        issueDate = LocalDate.now();
        dueDate = issueDate.plusDays(7);
    }

    int fine() {
        LocalDate end = returnDate == null
                ? LocalDate.now() : returnDate;

        long days = ChronoUnit.DAYS.between(dueDate, end);
        return (int) Math.max(0, days * 5);
    }

    boolean active() {
        return returnDate == null;
    }
}

// MAIN CLASS
public class LibraryManagementSystem {

    static ArrayList<Book> books = new ArrayList<>();
    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<Issue> issues = new ArrayList<>();

    static TextArea area;

    static String admin = "admin";
    static String adminPass = "admin123";

    public static void main(String[] args) {

        // DEFAULT BOOKS
        books.add(new Book(101, "Java Programming", "James Gosling"));
        books.add(new Book(102, "Python Programming", "Guido"));
        books.add(new Book(103, "Data Structures", "Mark Allen"));
        books.add(new Book(104, "Computer Networks", "Andrew"));

        // DEFAULT USER
        users.add(new User("madhesh", "madhesh123"));

        homePage();
    }

    // MAIN PAGE

    static void homePage() {

        Frame f = new Frame("Library Management System");
        f.setSize(450, 350);
        f.setLayout(new GridLayout(4, 1, 10, 10));

        Label heading = new Label(
                "LIBRARY MANAGEMENT SYSTEM", Label.CENTER);

        Button librarian = new Button("Librarian Login");
        Button userLogin = new Button("User Login");
        Button exit = new Button("Exit");

        f.add(heading);
        f.add(librarian);
        f.add(userLogin);
        f.add(exit);

        librarian.addActionListener(e -> {
            f.dispose();
            loginPage(true);
        });

        userLogin.addActionListener(e -> {
            f.dispose();
            loginPage(false);
        });

        exit.addActionListener(e -> System.exit(0));

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // LOGIN PAGE

    static void loginPage(boolean isAdmin) {

        Frame f = new Frame(
                isAdmin ? "Librarian Login" : "User Login");

        f.setSize(400, 250);
        f.setLayout(new GridLayout(4, 2, 10, 10));

        TextField name = new TextField();
        TextField pass = new TextField();
        pass.setEchoChar('*');

        Button login = new Button("Login");
        Button back = new Button("Back");

        f.add(new Label("Username:"));
        f.add(name);
        f.add(new Label("Password:"));
        f.add(pass);
        f.add(login);
        f.add(back);

        login.addActionListener(e -> {

            boolean valid = false;

            if (isAdmin) {
                valid = name.getText().equals(admin)
                        && pass.getText().equals(adminPass);
            } else {
                for (User u : users) {
                    if (u.username.equals(name.getText())
                            && u.password.equals(pass.getText())) {
                        valid = true;
                        break;
                    }
                }
            }

            if (valid) {
                String username = name.getText();
                f.dispose();

                if (isAdmin) {
                    librarianDashboard();
                } else {
                    userDashboard(username);
                }

            } else {
                message(f, "Invalid Username or Password!");
            }
        });

        back.addActionListener(e -> {
            f.dispose();
            homePage();
        });

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // LIBRARIAN DASHBOARD

    static void librarianDashboard() {

        Frame f = new Frame("Librarian Dashboard");
        f.setSize(850, 550);
        f.setLayout(new BorderLayout(10, 10));

        Label heading = new Label(
                "LIBRARIAN DASHBOARD", Label.CENTER);

        Panel p = new Panel(new GridLayout(6, 1, 5, 10));

        Button add = new Button("Add Book");
        Button view = new Button("View Books");
        Button issued = new Button("Issued Book Details");
        Button fine = new Button("Fine Details");
        Button addUser = new Button("Add New User");
        Button logout = new Button("Logout");

        p.add(add);
        p.add(view);
        p.add(issued);
        p.add(fine);
        p.add(addUser);
        p.add(logout);

        area = new TextArea();
        area.setEditable(false);

        f.add(heading, BorderLayout.NORTH);
        f.add(p, BorderLayout.WEST);
        f.add(area, BorderLayout.CENTER);

        // ADD BOOK

        add.addActionListener(e -> {

            String id = input(f, "Enter Book ID:");
            String title = input(f, "Enter Book Title:");
            String author = input(f, "Enter Author Name:");

            try {
                int bookId = Integer.parseInt(id);

                if (findBook(bookId) != null) {
                    message(f, "Book ID Already Exists!");
                } else if (title.trim().isEmpty()
                        || author.trim().isEmpty()) {
                    message(f, "Enter All Details!");
                } else {
                    books.add(new Book(bookId, title, author));
                    message(f, "Book Added Successfully!");
                }

            } catch (NumberFormatException ex) {
                message(f, "Invalid Book ID!");
            }
        });

        // VIEW BOOKS

        view.addActionListener(e -> {

            area.setText("ALL BOOK DETAILS\n\n");

            for (Book b : books) {
                area.append(
                        "Book ID: " + b.id
                        + "\nTitle: " + b.title
                        + "\nAuthor: " + b.author
                        + "\nStatus: "
                        + (b.available ? "Available" : "Issued")
                        + "\n-------------------------\n");
            }
        });

        // ISSUED BOOK DETAILS

        issued.addActionListener(e -> {

            area.setText("ISSUED BOOK DETAILS\n\n");

            boolean found = false;

            for (Issue i : issues) {

                if (i.active()) {

                    Book b = findBook(i.bookId);

                    area.append(
                            "Book ID: " + i.bookId
                            + "\nBook Name: " + b.title
                            + "\nUsername: " + i.username
                            + "\nIssue Date: " + i.issueDate
                            + "\nDue Date: " + i.dueDate
                            + "\nFine: Rs." + i.fine()
                            + "\n-------------------------\n");

                    found = true;
                }
            }

            if (!found) {
                area.setText("No Books Currently Issued.");
            }
        });

        // FINE DETAILS

        fine.addActionListener(e -> {

            area.setText("FINE DETAILS\n\n");

            int total = 0;

            for (Issue i : issues) {

                Book b = findBook(i.bookId);
                int amount = i.fine();

                area.append(
                        "Username: " + i.username
                        + "\nBook Name: " + b.title
                        + "\nDue Date: " + i.dueDate
                        + "\nReturn Date: "
                        + (i.returnDate == null
                            ? "Not Returned" : i.returnDate)
                        + "\nFine: Rs." + amount
                        + "\n-------------------------\n");

                total += amount;
            }

            area.append("\nTOTAL FINE: Rs." + total);
        });

        // ADD NEW USER

        addUser.addActionListener(e -> {

            String username = input(f, "Enter New Username:");

            if (username.trim().isEmpty()) {
                message(f, "Username Cannot Be Empty!");
                return;
            }

            for (User u : users) {
                if (u.username.equals(username)) {
                    message(f, "Username Already Exists!");
                    return;
                }
            }

            String password = input(f, "Enter New Password:");

            if (password.trim().isEmpty()) {
                message(f, "Password Cannot Be Empty!");
                return;
            }

            users.add(new User(username, password));

            message(f, "New User Registered Successfully!");
        });

        // LOGOUT

        logout.addActionListener(e -> {
            f.dispose();
            homePage();
        });

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // USER DASHBOARD

    static void userDashboard(String username) {

        Frame f = new Frame("User Dashboard");
        f.setSize(850, 500);
        f.setLayout(new BorderLayout(10, 10));

        Label heading = new Label(
                "USER DASHBOARD - " + username,
                Label.CENTER);

        Panel p = new Panel(new GridLayout(5, 1, 5, 10));

        Button view = new Button("Available Books");
        Button issue = new Button("Issue Book");
        Button returnBook = new Button("Return Book");
        Button details = new Button("My Issued Details & Fine");
        Button logout = new Button("Logout");

        p.add(view);
        p.add(issue);
        p.add(returnBook);
        p.add(details);
        p.add(logout);

        area = new TextArea();
        area.setEditable(false);

        f.add(heading, BorderLayout.NORTH);
        f.add(p, BorderLayout.WEST);
        f.add(area, BorderLayout.CENTER);

        // AVAILABLE BOOKS

        view.addActionListener(e -> {

            area.setText("AVAILABLE BOOKS\n\n");

            for (Book b : books) {

                if (b.available) {
                    area.append(
                            "Book ID: " + b.id
                            + "\nTitle: " + b.title
                            + "\nAuthor: " + b.author
                            + "\n-------------------------\n");
                }
            }
        });

        // ISSUE BOOK

        issue.addActionListener(e -> {

            String id = input(f, "Enter Book ID:");

            try {
                int bookId = Integer.parseInt(id);
                Book b = findBook(bookId);

                if (b == null) {
                    message(f, "Book Not Found!");
                } else if (!b.available) {
                    message(f, "Book Already Issued!");
                } else {
                    b.available = false;
                    issues.add(new Issue(bookId, username));

                    message(f, "Book Issued Successfully!\n"
                            + "Return Within 7 Days.");
                }

            } catch (NumberFormatException ex) {
                message(f, "Invalid Book ID!");
            }
        });

        // RETURN BOOK

        returnBook.addActionListener(e -> {

            String id = input(f, "Enter Book ID:");

            try {
                int bookId = Integer.parseInt(id);
                Issue record = null;

                for (Issue i : issues) {
                    if (i.bookId == bookId
                            && i.username.equals(username)
                            && i.active()) {
                        record = i;
                        break;
                    }
                }

                if (record == null) {
                    message(f, "No Active Issue Found!");
                } else {
                    record.returnDate = LocalDate.now();

                    Book b = findBook(bookId);
                    b.available = true;

                    message(f, "Book Returned Successfully!\n"
                            + "Fine: Rs." + record.fine());
                }

            } catch (NumberFormatException ex) {
                message(f, "Invalid Book ID!");
            }
        });

        // USER ISSUE AND FINE DETAILS

        details.addActionListener(e -> {

            area.setText("MY ISSUED BOOK DETAILS\n\n");

            boolean found = false;

            for (Issue i : issues) {

                if (i.username.equals(username)) {

                    Book b = findBook(i.bookId);

                    area.append(
                            "Book Name: " + b.title
                            + "\nIssue Date: " + i.issueDate
                            + "\nDue Date: " + i.dueDate
                            + "\nReturn Date: "
                            + (i.returnDate == null
                                ? "Not Returned" : i.returnDate)
                            + "\nStatus: "
                            + (i.active() ? "Issued" : "Returned")
                            + "\nFine: Rs." + i.fine()
                            + "\n-------------------------\n");

                    found = true;
                }
            }

            if (!found) {
                area.setText("No Issued Book Details Found.");
            }
        });

        // LOGOUT

        logout.addActionListener(e -> {
            f.dispose();
            homePage();
        });

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // FIND BOOK

    static Book findBook(int id) {

        for (Book b : books) {
            if (b.id == id) {
                return b;
            }
        }

        return null;
    }

    // INPUT DIALOG

    static String input(Frame f, String text) {

        Dialog d = new Dialog(f, "Enter Details", true);
        d.setSize(350, 150);
        d.setLayout(new GridLayout(3, 1, 5, 5));

        TextField field = new TextField();
        Button ok = new Button("OK");

        d.add(new Label(text));
        d.add(field);
        d.add(ok);

        final String[] result = {""};

        ok.addActionListener(e -> {
            result[0] = field.getText();
            d.dispose();
        });

        field.addActionListener(e -> {
            result[0] = field.getText();
            d.dispose();
        });

        d.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                d.dispose();
            }
        });

        d.setLocationRelativeTo(f);
        d.setVisible(true);

        return result[0];
    }

    // MESSAGE DIALOG

    static void message(Frame f, String text) {

        Dialog d = new Dialog(f, "Message", true);
        d.setSize(320, 150);
        d.setLayout(new BorderLayout());

        Label label = new Label(text, Label.CENTER);
        Button ok = new Button("OK");

        ok.addActionListener(e -> d.dispose());

        d.add(label, BorderLayout.CENTER);
        d.add(ok, BorderLayout.SOUTH);

        d.setLocationRelativeTo(f);
        d.setVisible(true);
    }
}
