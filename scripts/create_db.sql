-- Database and table creation for quanly_nha_tro
CREATE DATABASE IF NOT EXISTS quanly_nha_tro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quanly_nha_tro;

-- Table: thong_tin_thue
CREATE TABLE IF NOT EXISTS thong_tin_thue (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  contact_number VARCHAR(20),
  gender VARCHAR(10),
  roomNumber INT,
  name VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Optional sample data
INSERT INTO thong_tin_thue (contact_number, gender, roomNumber, name) VALUES
('0901234567', 'Male', 101, 'Nguyen Van A'),
('0909876543', 'Female', 102, 'Tran Thi B');
