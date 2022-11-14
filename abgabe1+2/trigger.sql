CREATE OR REPLACE FUNCTION insert_rating()
    RETURNS TRIGGER
    LANGUAGE plpgsql
    AS $$
    BEGIN
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
