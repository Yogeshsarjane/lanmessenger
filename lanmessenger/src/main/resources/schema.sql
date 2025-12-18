-- Creates the user table for authentication
CREATE TABLE "user" (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(50) NOT NULL,
                        allowed_ip VARCHAR(255) NULL, -- ✅ Add this line
                        current_ip VARCHAR(255) NULL, -- ✅ Add this line
                        status VARCHAR(255) DEFAULT 'Offline' -- ✅ Add this line
);

-- Creates the table to log all file transfers
CREATE TABLE file_log (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          original_filename VARCHAR(255),
                          stored_filename VARCHAR(255) NOT NULL UNIQUE,
                          sender VARCHAR(255) NOT NULL,
                          recipient VARCHAR(255) NOT NULL,
                          timestamp TIMESTAMP NOT NULL,
                          status VARCHAR(50) DEFAULT 'SENT' -- ✅ Add this line
);

-- Creates the table to log all chat messages
CREATE TABLE chat_message_log (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  sender VARCHAR(255),
                                  recipient VARCHAR(255),
                                  content TEXT,
                                  timestamp TIMESTAMP
);
CREATE TABLE fault (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject VARCHAR(255),
    category VARCHAR(255),
    priority VARCHAR(255),
    description VARCHAR(2000),
    reporter_username VARCHAR(255),
    submission_timestamp TIMESTAMP,
    screenshot_filename VARCHAR(255)
);