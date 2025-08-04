-- Creates the user table for authentication
CREATE TABLE "user" (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(50) NOT NULL
);

-- Creates the table to log all file transfers
CREATE TABLE file_log (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          original_filename VARCHAR(255),
                          stored_filename VARCHAR(255) NOT NULL UNIQUE,
                          sender VARCHAR(255) NOT NULL,
                          recipient VARCHAR(255) NOT NULL,
                          timestamp TIMESTAMP NOT NULL
);

-- Creates the table to log all chat messages
CREATE TABLE chat_message_log (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  sender VARCHAR(255),
                                  recipient VARCHAR(255),
                                  content TEXT,
                                  timestamp TIMESTAMP
);
