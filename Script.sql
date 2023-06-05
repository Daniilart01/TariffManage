CREATE TABLE tariffs (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tariff_name VARCHAR2(255),
    tariff_cost DECIMAL(10, 2),
    gigabytes VARCHAR2(255),
    minutes_out VARCHAR2(255),
    minutes_abroad VARCHAR2(255)
);

CREATE TABLE clients (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    login VARCHAR2(255) NOT NULL, 
    user_password VARCHAR2(255) NOT NULL, 
    phone_number VARCHAR2(255) UNIQUE,
    tariff_id NUMBER, 
    balance DECIMAL(10,2),
    tariff_start_date DATE,
    tariff_paid_until DATE,
    gigabytes_left VARCHAR2(255),
    minutes_out_left VARCHAR2(255),
    minutes_abroad_left VARCHAR2(255),
    FOREIGN KEY (tariff_id) REFERENCES tariffs(id) ON DELETE CASCADE
);

CREATE TABLE usage_history (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    client_id NUMBER, 
    tariff_id NUMBER, 
    start_date DATE,
    end_date DATE, 
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (tariff_id) REFERENCES tariffs(id) ON DELETE CASCADE
);
CREATE TABLE options (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    option_name VARCHAR2(255), 
    option_description VARCHAR2(255), 
    option_value DECIMAL(10,2),
    option_cost DECIMAL(10, 2)
);
CREATE TABLE Client_Option (
    client_id NUMBER,
    option_id NUMBER,
    paid_until DATE,
    value_left DECIMAL(10,2),
    PRIMARY KEY (client_id, option_id),
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (option_id) REFERENCES options(id) ON DELETE CASCADE
);
CREATE TABLE Top_Ups(
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    client_id NUMBER,
    top_up_date DATE,
    top_up_amount NUMBER(10,2),
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);
INSERT INTO tariffs (tariff_name, tariff_cost, gigabytes, minutes_out, minutes_abroad) VALUES ('Wow S', 80, '5', '100', '0');
INSERT INTO tariffs (tariff_name, tariff_cost, gigabytes, minutes_out, minutes_abroad) VALUES ('Wow M', 110, '10', '200', '20');
INSERT INTO tariffs (tariff_name, tariff_cost, gigabytes, minutes_out, minutes_abroad) VALUES ('Wow L', 160, '20', '300', '40');
INSERT INTO tariffs (tariff_name, tariff_cost, gigabytes, minutes_out, minutes_abroad) VALUES ('Wow SUPER', 220, '40', '400', '50');
INSERT INTO tariffs (tariff_name, tariff_cost, gigabytes, minutes_out, minutes_abroad) VALUES ('UNLIMITED', 320, 'UNLIMITED', '1000', '100');


INSERT INTO clients (login, user_password, tariff_id, balance, tariff_start_date, tariff_paid_until, phone_number, gigabytes_left, minutes_out_left, minutes_abroad_left) VALUES ('user1', '7c6a180b36896a0a8c02787eeafb0e4c', 1, 100.00, TO_DATE('2023-02-15', 'yyyy/mm/dd'), TO_DATE('2023-05-01', 'yyyy/mm/dd'), '+380474082457','3.56',75,0);
INSERT INTO clients (login, user_password, tariff_id, balance, tariff_start_date, tariff_paid_until, phone_number, gigabytes_left, minutes_out_left, minutes_abroad_left) VALUES ('user2', '6cb75f652a9b52798eb6cf2201057c73', 2, 50.00, TO_DATE('2022-09-07', 'yyyy/mm/dd'), TO_DATE('2023-05-02', 'yyyy/mm/dd'), '+380474707801','9.56',127,20);
INSERT INTO clients (login, user_password, tariff_id, balance, tariff_start_date, tariff_paid_until, phone_number, gigabytes_left, minutes_out_left, minutes_abroad_left) VALUES ('user3', '819b0643d6b89dc9b579fdfc9094f28e', 3, 75.00, TO_DATE('2023-01-09', 'yyyy/mm/dd'), TO_DATE('2023-05-03', 'yyyy/mm/dd'), '+380472007108','12.41',93,24);
INSERT INTO clients (login, user_password, tariff_id, balance, tariff_start_date, tariff_paid_until, phone_number, gigabytes_left, minutes_out_left, minutes_abroad_left) VALUES ('user4', '34cc93ece0ba9e3f6f235d4af979b16c', 4, 200.00, TO_DATE('2022-09-11', 'yyyy/mm/dd'), TO_DATE('2023-04-25', 'yyyy/mm/dd'), '+380476400223','32.89',245,32);
INSERT INTO clients (login, user_password, tariff_id, balance, tariff_start_date, tariff_paid_until, phone_number, gigabytes_left, minutes_out_left, minutes_abroad_left) VALUES ('user5', 'db0edd04aaac4506f7edab03ac855d56', 5, 30.00, TO_DATE('2023-02-19', 'yyyy/mm/dd'), TO_DATE('2023-04-22', 'yyyy/mm/dd'), '+380472297765','UNLIMITED',578,21);
INSERT INTO clients (login, user_password, tariff_id, balance, tariff_start_date, tariff_paid_until, phone_number, gigabytes_left, minutes_out_left, minutes_abroad_left) VALUES ('user6', '218dd27aebeccecae69ad8408d9a36bf', 1, 150.00, TO_DATE('2022-08-03', 'yyyy/mm/dd'), TO_DATE('2023-05-11', 'yyyy/mm/dd'), '+380474692614','2.57',45,0);
INSERT INTO clients (login, user_password, tariff_id, balance, tariff_start_date, tariff_paid_until, phone_number, gigabytes_left, minutes_out_left, minutes_abroad_left) VALUES ('user7', '00cdb7bb942cf6b290ceb97d6aca64a3', 3, 100.00, TO_DATE('2022-05-07', 'yyyy/mm/dd'), TO_DATE('2023-05-09', 'yyyy/mm/dd'), '+380472473554','2.1',31,0);
INSERT INTO clients (login, user_password, tariff_id, balance, tariff_start_date, tariff_paid_until, phone_number, gigabytes_left, minutes_out_left, minutes_abroad_left) VALUES ('user8', 'b25ef06be3b6948c0bc431da46c2c738', 2, 75.00, TO_DATE('2022-07-16', 'yyyy/mm/dd'), TO_DATE('2023-04-28', 'yyyy/mm/dd'), '+380470422768','3.21',77,0);
INSERT INTO clients (login, user_password, tariff_id, balance, tariff_start_date, tariff_paid_until, phone_number, gigabytes_left, minutes_out_left, minutes_abroad_left) VALUES ('user9', '5d69dd95ac183c9643780ed7027d128a', 5, 50.00, TO_DATE('2022-04-12', 'yyyy/mm/dd'), TO_DATE('2023-05-04', 'yyyy/mm/dd'), '+380478876528','UNLIMITED',912,100);
INSERT INTO clients (login, user_password, tariff_id, balance, tariff_start_date, tariff_paid_until, phone_number, gigabytes_left, minutes_out_left, minutes_abroad_left) VALUES ('user10', '87e897e3b54a405da144968b2ca19b45', 4, 25.00, TO_DATE('2022-03-17', 'yyyy/mm/dd'), TO_DATE('2023-04-25', 'yyyy/mm/dd'), '+380478825064','31.1',122,30);

INSERT INTO options (option_name, option_description,option_value ,option_cost) VALUES('5 GB', 'Add 5 GB of data to your tariff for a month', 5 ,40);
INSERT INTO options (option_name, option_description,option_value, option_cost) VALUES('10 GB', 'Add 10 GB of data to your tariff for a month', 10 ,60);
INSERT INTO options (option_name, option_description,option_value, option_cost) VALUES('100 Minutes', 'Add 100 minutes of calls to other operators to your monthly tariff', 100,30);
INSERT INTO options (option_name, option_description,option_value, option_cost) VALUES('200 Minutes', 'Add 200 minutes of calls to other operators to your monthly tariff', 200 ,50);
INSERT INTO options (option_name, option_description,option_value, option_cost) VALUES('20 Minutes Abroad', 'Add 20 minutes of calls abroad to your tariff for a month', 20 ,50);

INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (1, 1, TO_DATE('2022-02-01', 'yyyy/mm/dd'), TO_DATE('2022-04-01', 'yyyy/mm/dd'));
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (1, 1, TO_DATE('2022-04-01', 'yyyy/mm/dd'), TO_DATE('2022-07-01', 'yyyy/mm/dd'));
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (1, 2, TO_DATE('2022-07-01', 'yyyy/mm/dd'), TO_DATE('2023-02-15', 'yyyy/mm/dd'));
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (2, 2, TO_DATE('2022-09-07', 'yyyy/mm/dd'), null                               );
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (1, 3, TO_DATE('2023-02-15', 'yyyy/mm/dd'), null                               );
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (3, 3, TO_DATE('2023-01-09', 'yyyy/mm/dd'), null                               );
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (4, 4, TO_DATE('2022-01-11', 'yyyy/mm/dd'), TO_DATE('2022-04-11', 'yyyy/mm/dd'));
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (4, 4, TO_DATE('2022-04-11', 'yyyy/mm/dd'), TO_DATE('2022-08-11', 'yyyy/mm/dd'));
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (4, 5, TO_DATE('2022-08-11', 'yyyy/mm/dd'), TO_DATE('2022-09-11', 'yyyy/mm/dd'));
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (4, 1, TO_DATE('2022-09-11', 'yyyy/mm/dd'), null                               );
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (5, 5, TO_DATE('2023-02-19', 'yyyy/mm/dd'), null                               );
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (6, 1, TO_DATE('2022-08-03', 'yyyy/mm/dd'), null                               );
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (7, 3, TO_DATE('2022-05-07', 'yyyy/mm/dd'), null                               );
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (9, 2, TO_DATE('2022-04-12', 'yyyy/mm/dd'), null                               );
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (8, 5, TO_DATE('2022-07-16', 'yyyy/mm/dd'), null                               );
INSERT INTO usage_history (client_id, tariff_id, start_date, end_date) VALUES (10, 4,TO_DATE('2022-03-17', 'yyyy/mm/dd'), null                               );

INSERT INTO Client_Option (client_id, option_id, paid_until, value_left) VALUES (1, 1, TO_DATE('2023-05-09', 'YYYY-MM-DD'), 5);
INSERT INTO Client_Option (client_id, option_id, paid_until, value_left) VALUES (2, 2, TO_DATE('2023-05-08', 'YYYY-MM-DD'), 10);
INSERT INTO Client_Option (client_id, option_id, paid_until, value_left) VALUES (3, 3, TO_DATE('2023-05-03', 'YYYY-MM-DD'), 100);
INSERT INTO Client_Option (client_id, option_id, paid_until, value_left) VALUES (4, 4, TO_DATE('2023-04-28', 'YYYY-MM-DD'), 200);
INSERT INTO Client_Option (client_id, option_id, paid_until, value_left) VALUES (5, 5, TO_DATE('2023-04-25', 'YYYY-MM-DD'), 20);
INSERT INTO Client_Option (client_id, option_id, paid_until, value_left) VALUES (1, 3, TO_DATE('2023-05-09', 'YYYY-MM-DD'), 70);

INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (1, TO_DATE('2023-01-11', 'YYYY-MM-DD'), 80);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (1, TO_DATE('2023-02-11', 'YYYY-MM-DD'), 80);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (1, TO_DATE('2023-03-11', 'YYYY-MM-DD'), 80);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (1, TO_DATE('2023-04-11', 'YYYY-MM-DD'), 150);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (2, TO_DATE('2023-04-11', 'YYYY-MM-DD'), 20);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (3, TO_DATE('2023-04-11', 'YYYY-MM-DD'), 20);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (4, TO_DATE('2023-04-11', 'YYYY-MM-DD'), 20);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (5, TO_DATE('2023-04-11', 'YYYY-MM-DD'), 20);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (6, TO_DATE('2023-04-11', 'YYYY-MM-DD'), 20);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (7, TO_DATE('2023-04-11', 'YYYY-MM-DD'), 20);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (8, TO_DATE('2023-04-11', 'YYYY-MM-DD'), 20);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (9, TO_DATE('2023-04-11', 'YYYY-MM-DD'), 20);
INSERT INTO TOP_UPS(client_id,top_up_date,top_up_amount) VALUES (10, TO_DATE('2023-04-11', 'YYYY-MM-DD'), 20);


COMMIT;

