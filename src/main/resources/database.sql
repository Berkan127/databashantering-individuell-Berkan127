-- Skapa tabeller
CREATE TABLE categories (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(255) NOT NULL
);

CREATE TABLE product (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(255) NOT NULL,
                         price DECIMAL(10,2) NOT NULL,
                         category_id INT,
                         vat_rate DECIMAL(5,2),
                         FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE order (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       order_datetime DATETIME NOT NULL,
                       total_amount DECIMAL(10,2) NOT NULL,
                       total_vat DECIMAL(10,2) NOT NULL
);

CREATE TABLE order_item (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            order_id INT,
                            product_id INT,
                            quantity INT NOT NULL,
                            total_price DECIMAL(10,2) NOT NULL,
                            vat_amount DECIMAL(10,2) NOT NULL,
                            FOREIGN KEY (order_id) REFERENCES `order`(id),
                            FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE orders (
                        OrderId INT PRIMARY KEY AUTO_INCREMENT,
                        receipt_number VARCHAR(255) NOT NULL,
                        order_date DATETIME NOT NULL,
                        total_amount DECIMAL(10,2) NOT NULL,
                        total_vat DECIMAL(10,2) NOT NULL
);

CREATE TABLE products (
                          ProductId INT PRIMARY KEY AUTO_INCREMENT,
                          Name VARCHAR(255) NOT NULL,
                          Price DECIMAL(10,2) NOT NULL,
                          VatRate DECIMAL(5,2)
);

CREATE TABLE orderdetails (
                              OrderDetailId INT PRIMARY KEY AUTO_INCREMENT,
                              OrderId INT,
                              ProductId INT,
                              Quantity INT NOT NULL,
                              UnitPrice DECIMAL(10,2) NOT NULL,
                              VatRate DECIMAL(5,2),
                              FOREIGN KEY (OrderId) REFERENCES orders(OrderId),
                              FOREIGN KEY (ProductId) REFERENCES products(ProductId)
);

-- Infoga några exempelprodukter
INSERT INTO categories (name) VALUES ('Elektronik'), ('Kläder'), ('Mat');
INSERT INTO product (name, price, category_id, vat_rate) VALUES
                                                             ('Laptop', 15000.00, 1, 25.00),
                                                             ('T-shirt', 199.00, 2, 12.00),
                                                             ('Bröd', 30.00, 3, 6.00);

INSERT INTO products (Name, Price, VatRate) VALUES
                                                ('Mobiltelefon', 9999.99, 25.00),
                                                ('Jeans', 499.99, 12.00),
                                                ('Mjölk', 20.00, 6.00);
