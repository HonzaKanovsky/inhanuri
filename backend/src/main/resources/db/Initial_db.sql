-- 1. Create the Database
CREATE DATABASE inhanuri;

-- 3. Create the Schema
CREATE SCHEMA exchange_notice_board;

-- 4. Set the Default Schema for the Current Session
SET search_path TO exchange_notice_board;

-- 5. Verify the Search Path
SHOW search_path;

-- Admin users table
CREATE TABLE admin_users (
                             user_name VARCHAR(255) PRIMARY KEY,
                             password_hash VARCHAR(255) NOT NULL
);

-- Notices Table
CREATE TABLE notices (
                         id SERIAL PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         content TEXT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         views INT DEFAULT 0
);

-- Q&A Questions Table
CREATE TABLE qa_questions (
                              id SERIAL PRIMARY KEY,
                              title VARCHAR(255) NOT NULL,
                              content TEXT NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              views INT DEFAULT 0,
                              tags VARCHAR(255), -- Example: "support,schedule"
                              password_hash TEXT -- Password-based deletion for anonymous users
);

-- Q&A Answers Table (Linked to Questions)
CREATE TABLE qa_answers (
                            id SERIAL PRIMARY KEY,
                            question_id INT REFERENCES qa_questions(id) ON DELETE CASCADE,
                            content TEXT NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Community Posts Table
CREATE TABLE community_posts (
                                 id SERIAL PRIMARY KEY,
                                 title VARCHAR(255) NOT NULL,
                                 content TEXT NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 views INT DEFAULT 0,
                                 likes INT DEFAULT 0,
                                 password_hash TEXT -- For anonymous deletion
);

-- Comments Table (Linked to Community Posts)
CREATE TABLE comments (
                          id SERIAL PRIMARY KEY,
                          post_id INT REFERENCES community_posts(id) ON DELETE CASCADE,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE FUNCTION update_timestamp() RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for qa_questions
CREATE TRIGGER trigger_update_timestamp
    BEFORE UPDATE ON qa_questions
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- Trigger for community_posts
CREATE TRIGGER trigger_update_community_posts
    BEFORE UPDATE ON community_posts
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- Trigger for notices
CREATE TRIGGER trigger_update_notices
    BEFORE UPDATE ON notices
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();