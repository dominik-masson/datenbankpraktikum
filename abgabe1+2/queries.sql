-- 1.)
SELECT
    (SELECT COUNT(*) AS numberbooks FROM book),
    (SELECT COUNT(*) AS numbercds FROM cd),
    (SELECT COUNT(*) AS numberdvds FROM dvd);

-- Nennen Sie die 5 besten Produkte jedes Typs (Buch, Musik-CD, DVD)
-- sortiert nach dem durchschnittlichem Rating.
-- Hinweis: Geben Sie das Ergebnis in einer einzigen Relation mit den
-- Attributen Typ, ProduktNr, Rating aus.
-- Wie werden gleiche durchschnittliche Ratings behandelt?
-- 2.)

(SELECT 'DVD' AS typ, product.asin, product.rating
 FROM dvd INNER JOIN product ON dvd.asin = product.asin
 WHERE product.rating IS NOT NULL
 ORDER BY product.rating DESC
     FETCH FIRST 5 ROWS ONLY)
UNION
(SELECT 'CD' AS typ, product.asin, product.rating
 FROM cd INNER JOIN product ON cd.asin = product.asin
 WHERE product.rating IS NOT NULL
 ORDER BY product.rating DESC
     FETCH FIRST 5 ROWS ONLY)
UNION
(SELECT 'Book' AS typ, product.asin, product.rating
 FROM book INNER JOIN product ON book.asin = product.asin
 WHERE product.rating IS NOT NULL
 ORDER BY product.rating DESC
FETCH FIRST 5 ROWS ONLY);


-- 3.)
SELECT asin, title
FROM product p
INNER JOIN products_in_store pis on p.asin = pis.product
WHERE pis.availability = FALSE;


-- Für welche Produkte ist das teuerste Angebot mehr als doppelt so teuer wie das preiswerteste?
-- (es existieren keine)
-- 4.)
SELECT product.asin, product.title
    FROM
(SELECT product
FROM products_in_store
WHERE price IS NOT NULL
GROUP BY product
HAVING MAX(price) >= 2 * MIN(price)) result
NATURAL JOIN product WHERE result.product = product.asin;



-- 5.)
SELECT  asin, title
FROM product p
WHERE
    EXISTS (SELECT 1 FROM review WHERE product = p.asin AND points = 1) AND
    EXISTS (SELECT 1 FROM review WHERE product = p.asin AND points = 5)
GROUP BY asin, title;

-- 6.)
SELECT COUNT(*)
FROM product p
WHERE NOT EXISTS (SELECT 1 FROM review WHERE product = p.asin);

-- 7.)
SELECT username
FROM customer INNER JOIN review r on customer.uid = r.customer
GROUP BY username
HAVING COUNT(*) >= 10;

-- Geben Sie eine duplikatfreie und alphabetisch sortierte Liste der
-- Namen aller Buchautoren an, die auch an DVDs oder Musik-CDs beteiligt sind.
-- 8.)
SELECT DISTINCT p.name, p.uid FROM
    ((SELECT person AS p1 FROM associated_person WHERE associated_person.title = 'author') author
        INNER JOIN
        (SELECT person AS p2 FROM associated_person WHERE associated_person.title <> 'author') not_author
     ON author.p1 = not_author.p2) result
        LEFT JOIN person p
                  ON result.p1 = p.uid;

-- 9.)
SELECT AVG(array_length(tracks, 1)) as average FROM cd;

-- Für welche Produkte gibt es ähnliche Produkte in einer anderen Hauptkategorie?
-- Hinweis: Eine Hauptkategorie ist eine Produktkategorie ohne Oberkategorie.
-- Erstellen Sie eine rekursive Anfrage, die zu jedem Produkt dessen Hauptkategorie bestimmt.
-- 10.)
WITH RECURSIVE recursive_category(product, uid, flag) AS (
    SELECT product, category, FALSE FROM is_in_category
    UNION
    SELECT r.product,
           CASE WHEN c.parent_category IS NULL THEN c.uid
                ELSE c.parent_category END,
           c.parent_category IS NULL
    FROM recursive_category r, category c
    WHERE c.uid = r.uid AND NOT r.flag
),
parent_category(product, uid) AS (SELECT product, uid FROM recursive_category WHERE flag)
SELECT product1 AS asin
FROM similar_product
WHERE NOT EXISTS (
        SELECT uid FROM parent_category WHERE product = product1
        INTERSECT
        SELECT uid FROM parent_category WHERE product = product2
    );

-- Welche Produkte werden in allen Filialen angeboten?
-- Hinweis: Ihre Query muss so formuliert werden, dass sie für eine beliebige Anzahl von Filialen funktioniert.
-- Hinweis: Beachten Sie, dass ein Produkt mehrfach von einer Filiale angeboten werden kann (z.B. neu und gebraucht).
-- 11.)
SELECT asin, title
FROM product p
WHERE
    (SELECT COUNT(*) from store)
        =
    (SELECT COUNT(distinct store) FROM products_in_store pis
                                  WHERE p.asin = pis.product
                                  AND pis.availability = true);

-- In wieviel Prozent der Fälle der Frage 11 gibt
-- es in Leipzig das preiswerteste Angebot?
-- 12.)

WITH sold_everyhwere(asin) AS(
    SELECT asin, title
    FROM product p
    WHERE
            (SELECT COUNT(*) from store)
            =
            (SELECT COUNT(distinct store) FROM products_in_store pis
             WHERE p.asin = pis.product
               AND pis.availability = true)
)
SELECT
(SELECT COUNT(*)::FLOAT FROM sold_everyhwere se WHERE
    (SELECT MIN(price) FROM products_in_store pis WHERE pis.product = se.asin)
    =
    (SELECT MIN(price) FROM products_in_store pis WHERE pis.product = se.asin AND pis.store = 1)
) / NULLIF((SELECT COUNT(*)::FLOAT FROM sold_everyhwere), 0) * 100.0 AS ratio;