
CREATE DATABASE abxwebappgen;
CREATE USER 'abxwebappgen'@'localhost' IDENTIFIED BY 'abxwebappgen';
CREATE USER 'abxwebappgen'@'%' IDENTIFIED BY 'abxwebappgen';
GRANT ALL PRIVILEGES ON abxwebappgen.* TO 'abxwebappgen'@'localhost' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON abxwebappgen.* TO 'abxwebappgen'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;