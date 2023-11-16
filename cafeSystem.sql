-- DROP DATABASE cafeSystem;

CREATE DATABASE cafeSystem;

Use cafeSystem;

CREATE TABLE user_accounts (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    access_level ENUM('admin', 'cafe_owner', 'cafe_manager' ,'cafe_staff') NOT NULL,
    is_suspended BOOLEAN DEFAULT false,
	bids INT CHECK (bids >= 0 AND bids <= 100),
    job_role VARCHAR(50)
);

CREATE TABLE work_slots (
    work_slot_id INT PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(50),
    date VARCHAR(50),
    time VARCHAR(50),
	job_role VARCHAR(255) NOT NULL,
    is_booked VARCHAR(255)
);

CREATE TABLE user_profile (
	user_profile_id INT PRIMARY KEY AUTO_INCREMENT,
    profile VARCHAR(50)
);

/*
INSERT INTO user_accounts (username, password, full_name, email, access_level) VALUES
    ('admin1', 'password_1', 'James Bond', 'admin1@example.com', 'admin'),
    ('cafe_owner1', 'password_2', 'Elmo the Red', 'moderator1@example.com', 'cafe_owner'),
    ('cafe_manager1', 'password_3', 'Bob the Builder', 'viewer1@example.com', 'cafe_manager'),
    ('cafe_staff1', 'password_4', 'Thomas the Train', 'admin2@example.com', 'cafe_staff');

INSERT INTO work_slots (username, date, time, job_role, is_booked) VAlUES
	(' ', '11 December 2023', '12pm-3pm', 'Chef', 0),
	(' ', '12 December 2023', '2pm-5pm', 'Waiter', 0),
	(' ', '13 December 2023', '10am-1pm', 'Barista', 0),
	(' ', '14 December 2023', '3pm-6pm', 'Bartender', 0),
	(' ', '15 December 2023', '1pm-4pm', 'Kitchen Staff', 0),
	(' ', '16 December 2023', '11am-2pm', 'Chef', 0),
	(' ', '17 December 2023', '5pm-8pm', 'Waiter', 0),
	(' ', '18 December 2023', '12pm-3pm', 'Barista', 0),
	(' ', '19 December 2023', '2pm-5pm', 'Bartender', 0),
	(' ', '20 December 2023', '4pm-7pm', 'Kitchen Staff', 0),
	(' ', '21 December 2023', '10am-1pm', 'Chef', 0),
	(' ', '22 December 2023', '3pm-6pm', 'Waiter', 0),
	(' ', '23 December 2023', '1pm-4pm', 'Barista', 0),
	(' ', '24 December 2023', '11am-2pm', 'Bartender', 0);
*/

INSERT INTO user_profile (profile) VALUES
	('admin'),
    ('cafe_owner'),
    ('cafe_manager'),
    ('cafe_staff');

SELECT * FROM user_accounts;

/*Changing the password to 'MyNewPass */ 
/*
ALTER USER 'root'@'localhost' IDENTIFIED BY 'MyNewPass';

DROP TABLE user_accounts;
DROP TABLE work_slots;
DROP TABLE user_profile;
*/


