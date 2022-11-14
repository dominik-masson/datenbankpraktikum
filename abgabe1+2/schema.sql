DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public AUTHORIZATION mediastore;

CREATE TABLE address(
                        uid SERIAL PRIMARY KEY,
                        street VARCHAR(255),
                        number INT,
                        zip INT
);

CREATE TABLE product(
                        asin VARCHAR(50) NOT NULL,
                        title VARCHAR(512) NOT NULL,
                        rating FLOAT,
                        salesrank INT,
                        image VARCHAR(512),
                        CHECK (length(asin) = 10),
                        CHECK (rating > 0),
                        PRIMARY KEY (asin)
);

CREATE TABLE category(
                         uid SERIAL PRIMARY KEY,
                         name VARCHAR(255),
                         parent_category INT,
                         FOREIGN KEY (parent_category) REFERENCES category(uid) ON DELETE SET NULL
);

CREATE TABLE customer(
                         uid SERIAL PRIMARY KEY,
                         username VARCHAR(50) NOT NULL,
                         account_number VARCHAR(50),
                         address INT,
                         FOREIGN KEY (address) REFERENCES address(uid)
);

CREATE TABLE review(
                       customer INT NOT NULL,
                       product VARCHAR(50) NOT NULL,
                       description TEXT NOT NULL,
                       summary TEXT NOT NULL,
                       points INT,
                       helpful INT,
                       timestamp TIMESTAMP,
                       CHECK (points BETWEEN 1 AND 5),
                       PRIMARY KEY (customer, product, timestamp),
                       FOREIGN KEY (customer) REFERENCES customer(uid) ON DELETE CASCADE,
                       FOREIGN KEY (product) REFERENCES product(asin) ON DELETE CASCADE
);

CREATE TABLE sale(
                     customer INT NOT NULL,
                     product VARCHAR(50) NOT NULL,
                     timestamp TIMESTAMP,
                     price FLOAT,
                     CHECK (price > 0),
                     PRIMARY KEY (customer, product, timestamp),
                     FOREIGN KEY (customer) REFERENCES customer(uid) ON DELETE CASCADE,
                     FOREIGN KEY (product) REFERENCES product(asin) ON DELETE CASCADE
);

CREATE TABLE store(
                      uid SERIAL PRIMARY KEY,
                      name VARCHAR(255),
                      address INT NOT NULL,
                      FOREIGN KEY (address) REFERENCES address(uid) ON DELETE CASCADE
);

CREATE TABLE products_in_store(
                                  store INT NOT NULL,
                                  product VARCHAR(50) NOT NULL,
                                  price FLOAT,
                                  currency VARCHAR(5),
                                  availability BOOLEAN,
                                  condition VARCHAR(50),
                                  CHECK (price > 0 OR price IS NULL),
                                  PRIMARY KEY (store, product, condition),
                                  FOREIGN KEY (store) REFERENCES store(uid) ON DELETE CASCADE,
                                  FOREIGN KEY (product) REFERENCES product(asin) ON DELETE CASCADE
);

CREATE TABLE similar_product(
                                product1 VARCHAR(50) NOT NULL,
                                product2 VARCHAR(50) NOT NULL,
                                PRIMARY KEY (product1, product2),
                                FOREIGN KEY (product1) REFERENCES product(asin) ON DELETE CASCADE,
                                FOREIGN KEY (product2) REFERENCES product(asin) ON DELETE CASCADE
);

CREATE TABLE is_in_category(
                               product VARCHAR(50) NOT NULL,
                               category INT NOT NULL,
                               PRIMARY KEY (product, category),
                               FOREIGN KEY (product) REFERENCES product(asin) ON DELETE CASCADE,
                               FOREIGN KEY (category) REFERENCES category(uid) ON DELETE CASCADE
);

CREATE TABLE book(
                     asin VARCHAR(50) NOT NULL,
                     pages INT,
                     release_date DATE,
                     isbn VARCHAR(13) NOT NULL,
                     PRIMARY KEY (asin),
                     FOREIGN KEY (asin) REFERENCES product(asin) ON DELETE CASCADE
);

CREATE TABLE dvd(
                    asin VARCHAR(50) NOT NULL,
                    ean VARCHAR(50) NOT NULL,
                    format VARCHAR(255),
                    runtime INT,
                    region_code INT,
                    release_date DATE,
                    PRIMARY KEY (asin),
                    FOREIGN KEY (asin) REFERENCES product(asin) ON DELETE CASCADE
);

CREATE TABLE cd(
                   asin VARCHAR(50) NOT NULL,
                   ean VARCHAR(50) NOT NULL,
                   release_date DATE,
                   tracks TEXT[],
                   PRIMARY KEY (asin),
                   FOREIGN KEY (asin) REFERENCES product(asin) ON DELETE CASCADE
);

CREATE TABLE person(
                       uid SERIAL PRIMARY KEY,
                       name VARCHAR(255)
);

CREATE TABLE company(
                        uid SERIAL PRIMARY KEY,
                        name VARCHAR(255)
);

CREATE TABLE associated_company(
                                   company INT,
                                   product VARCHAR(50),
                                   role VARCHAR(128),
                                   PRIMARY KEY (company, product),
                                   FOREIGN KEY (company) REFERENCES company(uid) ON DELETE CASCADE,
                                   FOREIGN KEY (product) REFERENCES product(asin) ON DELETE CASCADE
);

CREATE TABLE associated_person(
                                  person INT,
                                  product VARCHAR(50),
                                  title VARCHAR(128),
                                  PRIMARY KEY (person, product, title),
                                  FOREIGN KEY (person) REFERENCES person(uid) ON DELETE CASCADE,
                                  FOREIGN KEY (product) REFERENCES product(asin) ON DELETE CASCADE
);

-- INSERT TRIGGERS

CREATE OR REPLACE FUNCTION insert_rating()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$ BEGIN
    UPDATE product
    SET rating = (
        SELECT cast(sum(points) AS REAL)/count(points)
        FROM review
        WHERE NEW.product=product
    )
    WHERE asin = NEW.product;
    RETURN NULL;
END;
$$;

DROP TRIGGER IF EXISTS insert_rating ON review;

CREATE TRIGGER insert_rating
    AFTER INSERT ON review
    FOR EACH ROW EXECUTE PROCEDURE insert_rating();



CREATE OR REPLACE FUNCTION delete_rating()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE product
    SET rating = (
        SELECT cast(sum(points) AS REAL)/count(points)
        FROM review
        WHERE OLD.product=product
    )
    WHERE asin = OLD.product;
    RETURN NULL;
END;
$$;

DROP TRIGGER IF EXISTS delete_rating ON review;

CREATE TRIGGER delete_rating
    AFTER DELETE ON review
    FOR EACH ROW EXECUTE PROCEDURE delete_rating();



CREATE OR REPLACE FUNCTION update_rating()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.product <> OLD.product THEN
        RAISE EXCEPTION 'product cant be changed';
    END IF;
    IF NEW.points <> OLD.points THEN
        UPDATE product
        SET rating = (
            SELECT cast(sum(points) AS REAL)/count(points)
            FROM review
            WHERE NEW.product=product
        )
        WHERE asin = NEW.product;
    END IF;
    RETURN NULL;
END;
$$;

DROP TRIGGER IF EXISTS update_rating ON review;

CREATE TRIGGER update_rating
    AFTER UPDATE ON review
    FOR EACH ROW EXECUTE PROCEDURE update_rating();


-- new for test 3
CREATE OR REPLACE  VIEW lowest_price AS
    SELECT p.asin,
           (SELECT MIN(ps.price)
            FROM products_in_store ps
            WHERE ps.product = p.asin
            AND ps.availability = true)
    FROM product p;

-- GRANT RIGHTS

GRANT ALL ON SCHEMA public TO mediastore;

GRANT ALL
    ON ALL TABLES IN SCHEMA public
    TO mediastore;

GRANT ALL
    ON ALL SEQUENCES IN SCHEMA public
    TO mediastore;

