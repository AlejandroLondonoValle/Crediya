CREATE TABLE users (
   id                BIGINT        NOT NULL AUTO_INCREMENT,
   first_name        VARCHAR(100)  NOT NULL,
   last_name         VARCHAR(100)  NOT NULL,
   email             VARCHAR(150)  NOT NULL,
   identity_document VARCHAR(20)   NOT NULL,
   phone             VARCHAR(20),
   birth_date        DATE,
   address           VARCHAR(255),
   base_salary       DECIMAL(15,2) NOT NULL,
   password          VARCHAR(255)  NOT NULL,
   role_id           BIGINT        NOT NULL,
   CONSTRAINT pk_users PRIMARY KEY (id),
   CONSTRAINT uk_users_email UNIQUE (email),
   CONSTRAINT uk_users_identity_document UNIQUE (identity_document),
   CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id)
);