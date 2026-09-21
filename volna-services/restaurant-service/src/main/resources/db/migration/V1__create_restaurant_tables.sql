CREATE TABLE restaurants (
 id UUID PRIMARY KEY,
 owner_id UUID NOT NULL UNIQUE,
 name VARCHAR(150) NOT NULL,
 description VARCHAR(1000),
 phone VARCHAR(20) NOT NULL,
 email VARCHAR(255) NOT NULL UNIQUE,
 address VARCHAR(500) NOT NULL,
 latitude NUMERIC(9,6),
 longitude NUMERIC(9,6),
 opening_time TIME,
 closing_time TIME,
 status VARCHAR(30) NOT NULL,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE restaurant_documents (
 id UUID PRIMARY KEY,
 restaurant_id UUID NOT NULL,
 type VARCHAR(50) NOT NULL,
 file_url VARCHAR(1000) NOT NULL,
 status VARCHAR(30) NOT NULL,
 uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_document_restaurant FOREIGN KEY(restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);