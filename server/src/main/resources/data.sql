-- Insert sample products
INSERT INTO products (name, category, quantity, price) VALUES
    ('Laptop', 'Electronics', 15, 1200.00),
    ('Desk Chair', 'Furniture', 25, 150.00),
    ('Notebook', 'Stationery', 100, 2.50),
    ('Monitor', 'Electronics', 10, 200.00),
    ('Smartphone', 'Electronics', 20, 800.00),
    ('Wireless Mouse', 'Electronics', 50, 35.00),
    ('Bookshelf', 'Furniture', 8, 250.00),
    ('Desk Lamp', 'Furniture', 30, 45.00),
    ('Pen Set', 'Stationery', 75, 12.99),
    ('Printer', 'Electronics', 12, 150.00),
    ('Projector', 'Electronics', 5, 500.00),
    ('Filing Cabinet', 'Furniture', 15, 180.00),
    ('Whiteboard', 'Office Supplies', 20, 75.00),
    ('Calculator', 'Stationery', 40, 25.00),
    ('External Hard Drive', 'Electronics', 25, 100.00);

-- Insert default users with the same password (123456)
INSERT INTO employees (username, fullname, password, role) VALUES
    ('admin', 'Muhammad Abdullah', '$2a$12$OgFuXHF0NDNvwdzELlUS8ubDelgiT42WUoZxyQVbP3kSe1DuUzBmi', 'admin'),
    ('employee1', 'Ahmed Hassan', '$2a$12$OgFuXHF0NDNvwdzELlUS8ubDelgiT42WUoZxyQVbP3kSe1DuUzBmi', 'user'),
    ('employee2', 'Fatima Muhammad', '$2a$12$OgFuXHF0NDNvwdzELlUS8ubDelgiT42WUoZxyQVbP3kSe1DuUzBmi', 'user'),
    ('employee3', 'Ali Ibrahim', '$2a$12$OgFuXHF0NDNvwdzELlUS8ubDelgiT42WUoZxyQVbP3kSe1DuUzBmi', 'user'),
    ('employee4', 'Layla Omar', '$2a$12$OgFuXHF0NDNvwdzELlUS8ubDelgiT42WUoZxyQVbP3kSe1DuUzBmi', 'user'),
    ('employee5', 'Khalid Said', '$2a$12$OgFuXHF0NDNvwdzELlUS8ubDelgiT42WUoZxyQVbP3kSe1DuUzBmi', 'user'),
    ('employee6', 'Noura Ahmed', '$2a$12$OgFuXHF0NDNvwdzELlUS8ubDelgiT42WUoZxyQVbP3kSe1DuUzBmi', 'user'),
    ('employee7', 'Omar Mohsen', '$2a$12$OgFuXHF0NDNvwdzELlUS8ubDelgiT42WUoZxyQVbP3kSe1DuUzBmi', 'user'),
    ('employee8', 'Zainab Mahmoud', '$2a$12$OgFuXHF0NDNvwdzELlUS8ubDelgiT42WUoZxyQVbP3kSe1DuUzBmi', 'user');