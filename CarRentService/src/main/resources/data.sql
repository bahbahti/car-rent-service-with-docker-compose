INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('BMW', 2100, 'Sovetskiy', 'N522UA', 'green');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('AUDI', 2342, 'Nizhegorodskiy','A333AA','white');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('VOLVO', 231, 'Leninskiy', 'O756OO', 'red');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('RENO', 21, 'Nizhegorodskiy', 'M654SK', 'black');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('FERRARI', 342,'Sormovskiy', 'D001RR', 'blue');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('FORD', 876, 'Sormovskiy', 'N123UN', 'green');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('LADA', 211, 'Kanavinsky', 'A634SK', 'black');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('SHELBY', 342,'Sormovskiy', 'L101RR', 'blue');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('KIA', 876, 'Moskovsky', 'Z173UN', 'green');

INSERT INTO roles VALUES (1, 'ADMIN');
INSERT INTO roles VALUES (2, 'USER');

INSERT INTO customers (first_name, last_name, area_of_living, discount, passport_number, phone_number, username, password, role_id, enabled) VALUES ('Stanislav','Petrov', 'Sormovskiy',10, 111112, 987112233, 'stas', '$2a$10$CJfVQAR6B6QYk.uz0OhP0OhpHNsHpcK50TXVszQQJOa91i4wiamCe', 2, true);
INSERT INTO customers (first_name, last_name, area_of_living, discount, passport_number, username, password, role_id, enabled) VALUES ('Ivan','Ivanov', 'Nizhegorodskiy',15, 321311, 'ivan', '$2a$10$CJfVQAR6B6QYk.uz0OhP0OhpHNsHpcK50TXVszQQJOa91i4wiamCe', 2, true);
INSERT INTO customers (first_name, last_name, area_of_living, passport_number, phone_number, username, password, role_id, enabled) VALUES ('Gleb', 'Sidorov', 'Sovetskiy', 321312, 987112232, 'gleb', '$2a$10$CJfVQAR6B6QYk.uz0OhP0OhpHNsHpcK50TXVszQQJOa91i4wiamCe', 2, true);
INSERT INTO customers (first_name, last_name, area_of_living, discount, passport_number, username, password, role_id, enabled) VALUES ('Oleg','Malinin', 'Leninskiy',5, 321414, 'admin', '$2a$10$CJfVQAR6B6QYk.uz0OhP0OhpHNsHpcK50TXVszQQJOa91i4wiamCe', 1, true);

INSERT INTO orders(start_day, end_day, customer_id, car_id, order_price) VALUES ('2018-02-23', '2018-02-24',1,1, 2100);
INSERT INTO orders(start_day, end_day, customer_id, car_id, order_price) VALUES ('2018-08-12', '2018-08-27',1,2, 31500);
INSERT INTO orders(start_day, customer_id, car_id) VALUES ('2018-08-21', 1,6);
INSERT INTO orders(start_day, end_day, customer_id, car_id, order_price) VALUES ('2018-01-02', '2018-02-03',1,3, 67200);

INSERT INTO orders(start_day, end_day, customer_id, car_id, order_price) VALUES ('2018-06-14', '2018-07-24', 2,4, 96022);
INSERT INTO orders(start_day, customer_id, car_id) VALUES ('2018-05-17',2,5);
INSERT INTO orders(start_day, customer_id, car_id) VALUES ('2018-07-25', 2,4);
INSERT INTO orders(start_day, end_day, customer_id, car_id, order_price) VALUES ('2018-02-17', '2018-04-19',2,3, 140520);

INSERT INTO orders(start_day, end_day, customer_id, car_id, order_price) VALUES ('2018-03-14', '2018-04-24', 3, 4, 9471);
INSERT INTO orders(start_day, customer_id, car_id) VALUES ('2018-05-17', 3, 8);
INSERT INTO orders(start_day, customer_id, car_id) VALUES ('2018-07-25', 3, 9);
INSERT INTO orders(start_day, end_day, customer_id, car_id, order_price) VALUES ('2018-07-17', '2019-04-19', 3, 3, 13860);


INSERT INTO repairs (car_id, customer_id, start_day, end_day, price, status_of_repair) VALUES (1, 1, '2018-02-24', '2018-04-24', 1300, 'FINISHED');
INSERT INTO repairs (car_id, customer_id, start_day, end_day, price, status_of_repair) VALUES (2, 1, '2018-08-27', '2018-08-29', 1100, 'FINISHED');
INSERT INTO repairs (car_id, start_day, end_day, price, status_of_repair) VALUES (1, '2018-04-25', '2018-04-28', 700, 'FINISHED');
INSERT INTO repairs (car_id, start_day, end_day, price, status_of_repair) VALUES (8, '2018-04-25', '2018-04-28', 2700, 'FINISHED');
INSERT INTO repairs (car_id, customer_id, start_day, end_day, price, status_of_repair) VALUES (4, 3, '2017-04-24', '2017-06-24', 1200, 'FINISHED');
INSERT INTO repairs (car_id, customer_id, start_day, status_of_repair) VALUES (3, 2, '2019-04-19', 'PENDING');