CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,        -- Primary Key for each user
    username VARCHAR(50) NOT NULL UNIQUE,     -- Username must be unique
    email VARCHAR(100) NOT NULL UNIQUE,       -- Email must be unique
    password VARCHAR(255) NOT NULL,           -- Store hashed password
    status ENUM('connected', 'disconnected') DEFAULT 'disconnected',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Automatically set creation time
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- Automatically update time on changes
);

-- desc user;

CREATE TABLE ChatRoom (
  chat_room_id INT AUTO_INCREMENT PRIMARY KEY, -- Unique identifier for the chat room
  name VARCHAR(255) NOT NULL, -- Name of the chat room, cannot be null
  created_by INT NOT NULL, -- ID of the user who created the chat room
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- Timestamp of when the chat room was created
  CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES user(user_id) -- Foreign key constraint referencing the user table
);

CREATE TABLE `Message` (
    message_id INT AUTO_INCREMENT PRIMARY KEY,                 -- Primary Key for the message
    sender_user_id INT NOT NULL,                               -- Foreign Key referencing user_id in the user table
    content TEXT NOT NULL,                                     -- Message content
    recipient_user_id INT,                                     -- Foreign Key referencing user_id (nullable if chat room exists)
    recipient_chat_room_id INT,                                -- Foreign Key referencing chat_room_id (nullable if recipient_user_id exists)
    recipient_type ENUM('private', 'group') NOT NULL,          -- Specify whether recipient is a user (private) or a group
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP         -- Automatically track message creation time

--     -- Foreign Key Constraints
--     CONSTRAINT fk_sender_user FOREIGN KEY (sender_user_id) REFERENCES user(user_id)
--         ON DELETE CASCADE,                                     -- Delete messages if user is deleted
--     CONSTRAINT fk_recipient_user FOREIGN KEY (recipient_user_id) REFERENCES user(user_id),
--     CONSTRAINT fk_recipient_chat_room FOREIGN KEY (recipient_chat_room_id) REFERENCES chat_room(chat_room_id)
);

ALTER TABLE `Message`
ADD CONSTRAINT fk_sender_user
FOREIGN KEY (sender_user_id) REFERENCES user(user_id)
ON DELETE CASCADE;

ALTER TABLE `Message`
ADD CONSTRAINT fk_recipient_user
FOREIGN KEY (recipient_user_id) REFERENCES `user`(user_id);

ALTER TABLE `Message`
ADD CONSTRAINT fk_recipient_chat_room
FOREIGN KEY (recipient_chat_room_id) REFERENCES `ChatRoom`(chat_room_id)
ON DELETE SET NULL;

desc user;
desc chatroom;

drop table Message;

ALTER TABLE `Message`
ADD CONSTRAINT `fk_chat_room`
FOREIGN KEY (`recipient_chat_room_id`) REFERENCES `chat_room`(`chat_room_id`)
ON DELETE CASCADE; -- Adjust behavior as per your need

desc message;

drop table chatroom;

CREATE TABLE `Participant` (
    `user_id` INT,                                -- Foreign Key referencing user table
    `chat_room_id` INT,                           -- Foreign Key referencing chat_room table
    PRIMARY KEY (`user_id`, `chat_room_id`)     -- Composite Primary Key

);

ALTER TABLE `Participant`
ADD CONSTRAINT fk_participant_user FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
        ON DELETE CASCADE;                       -- If user is deleted, remove from Participant

ALTER TABLE `Participant`
ADD CONSTRAINT fk_participant_chat_room FOREIGN KEY (`chat_room_id`) REFERENCES chatroom(`chat_room_id`)
        ON DELETE CASCADE;                     -- If user is deleted, remove from Participant

SELECT *
FROM chatroom c
JOIN participant p ON c.chat_room_id = p.chat_room_id
WHERE p.user_id = 1
  AND p.chat_room_id = 1;

desc participant;