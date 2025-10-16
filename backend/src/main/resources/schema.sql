DROP TABLE IF EXISTS USER_FRIEND;
DROP TABLE IF EXISTS USER_TEAM_BELONG;
DROP TABLE IF EXISTS USER_TASK_ASSIGN;
DROP TABLE IF EXISTS USER_PROJECT_PARTICIPATE;
DROP TABLE IF EXISTS USER_PROJECT_LEAD;
DROP TABLE IF EXISTS ATTACHMENT;
DROP TABLE IF EXISTS NOTIFICATION;
DROP TABLE IF EXISTS TASK;
DROP TABLE IF EXISTS SPRINT;
DROP TABLE IF EXISTS TEAM;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS PROJECT;


CREATE TABLE PROJECT (
                         project_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                         sprint_key VARCHAR(50) NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         status VARCHAR(30) CHECK (STATUS IN ('TODO', 'IN_PROGRESS', 'COMPLETED')) NOT NULL DEFAULT 'TODO',
                         created_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
                         updated_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP /* ON UPDATE CURRENT_TIMESTAMP */

);


CREATE TABLE SPRINT (
                        sprint_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        project_id INT NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        start_date DATE NOT NULL,
                        end_date DATE NOT NULL,
                        goal TEXT,
                        FOREIGN KEY (project_id) REFERENCES PROJECT(project_id) ON DELETE CASCADE,
                        CONSTRAINT check_dates CHECK (end_date > start_date)
);
CREATE TABLE TASK (
                      task_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                      sprint_id INT,
                      parent_task_id INT,
                      title VARCHAR(255) NOT NULL,
                      description TEXT,
                      priority VARCHAR(30) CHECK (PRIORITY IN ('LOW', 'MEDIUM', 'HIGH')) NOT NULL DEFAULT 'MEDIUM',
                      status VARCHAR(30) CHECK (STATUS IN ('TODO', 'IN_PROGRESS', 'REVIEW', 'DONE')) NOT NULL DEFAULT 'TODO',
                      work_type VARCHAR(30) CHECK (WORK_TYPE IN ('TASK', 'BUG')) NOT NULL DEFAULT 'TASK',
                      due_date DATE,
                      attachment_link TEXT,
                      created_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
                      updated_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP /* ON UPDATE CURRENT_TIMESTAMP */,
                      FOREIGN KEY (sprint_id) REFERENCES SPRINT(sprint_id) ON DELETE SET NULL,
                      FOREIGN KEY (parent_task_id) REFERENCES TASK(task_id) ON DELETE CASCADE
);
CREATE TABLE USERS (
                        user_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        username VARCHAR(255) NOT NULL,
                        email VARCHAR(255) UNIQUE NOT NULL,
                        password VARCHAR(50) NOT NULL,
                        phone VARCHAR(20),
                        role VARCHAR(50) CHECK (ROLE IN ('ADMIN', 'USER' )) NOT NULL DEFAULT 'USER',
                        avatar_link VARCHAR(500),
                        status VARCHAR(30) CHECK (STATUS IN ('ACTIVE', 'INACTIVE')) NOT NULL DEFAULT 'ACTIVE'
                      --  created_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
                      --  updated_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP /* ON UPDATE CURRENT_TIMESTAMP */
);

CREATE INDEX idx_email ON USERS (email);
CREATE INDEX idx_status ON USERS (status);
CREATE TABLE TEAM (
                      team_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                      name VARCHAR(255) NOT NULL,
                      description TEXT,
                      created_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE NOTIFICATION (
                              notification_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                              user_id INT NOT NULL,
                              task_id INT,
                              title VARCHAR(255) NOT NULL,
                              content TEXT NOT NULL,
                              created_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
                              is_read BOOLEAN DEFAULT FALSE,
                              type VARCHAR(30) CHECK (TYPE IN ('TASK_ASSIGNED', 'COMMENT', 'STATUS_CHANGE', 'SPRINT_START', 'DEADLINE')) DEFAULT 'TASK_ASSIGNED',
                              FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
                              FOREIGN KEY (task_id) REFERENCES TASK(task_id) ON DELETE CASCADE
);

CREATE INDEX idx_user_read ON NOTIFICATION (user_id, is_read);
CREATE INDEX idx_created ON NOTIFICATION (created_date);
CREATE TABLE USER_PROJECT_LEAD (
                                   project_id INT NOT NULL UNIQUE,
                                   user_id INT NOT NULL,
                                   assigned_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (project_id, user_id),
                                   FOREIGN KEY (project_id) REFERENCES PROJECT(project_id) ON DELETE CASCADE,
                                   FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

CREATE TABLE USER_PROJECT_PARTICIPATE (
                                          project_id INT NOT NULL,
                                          user_id INT NOT NULL,
                                          team_id INT NULL ,
                                          joined_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
                                          PRIMARY KEY (project_id, user_id),
                                          FOREIGN KEY (project_id) REFERENCES PROJECT(project_id) ON DELETE CASCADE,
                                          FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
                                          FOREIGN KEY (team_id) REFERENCES TEAM(team_id)
);
CREATE TABLE USER_TASK_ASSIGN (
                                  task_id INT NOT NULL,
                                  user_id INT NOT NULL,
                                  assigned_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (task_id, user_id),
                                  FOREIGN KEY (task_id) REFERENCES TASK(task_id) ON DELETE CASCADE,
                                  FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);
CREATE TABLE USER_TEAM_BELONG (
                                  user_id INT NOT NULL,
                                  team_id INT NOT NULL,
                                  joined_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (user_id, team_id),
                                  FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
                                  FOREIGN KEY (team_id) REFERENCES TEAM(team_id) ON DELETE CASCADE
);

CREATE TABLE USER_FRIEND (
                             user_id INT NOT NULL,
                             friend_id INT NOT NULL,
                             created_date TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (user_id, friend_id),
                             FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
                             FOREIGN KEY (friend_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
                             CONSTRAINT check_not_self CHECK (user_id != friend_id)
    );

CREATE TABLE ATTACHMENT (
                            user_id INT NOT NULL,
                            task_id INT,
                            title VARCHAR(50) DEFAULT 'Untitled',
                            link TEXT NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
                            FOREIGN KEY (task_id) REFERENCES TASK(task_id) ON DELETE CASCADE
)
