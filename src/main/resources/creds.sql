
CREATE DATABASE abxwebapp;
CREATE USER 'abxwebapp'@'localhost' IDENTIFIED BY 'abxwebapp';
CREATE USER 'abxwebapp'@'%' IDENTIFIED BY 'v';
GRANT ALL PRIVILEGES ON abxwebapp.* TO 'abxwebapp'@'localhost' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON abxwebapp.* TO 'abxwebapp'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;