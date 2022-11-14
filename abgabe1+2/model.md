## address

- uid (pk)
- street
- number
- zip


## product

- asin (pk)
- title
- rating 
- image
- salesrank


## category

- uid (pk)
- name
- parent_category (fk category.uid)


## customer

- uid (pk)
- username
- acount_number
- address (fk address.uid)


## review
- customer (fk customer.uid)
- product (fk product.asin)
- description
- summary
- points
- helpful
- timestamp
- pk (customer + product + timestamp)


## sale

- customer (fk customer.uid)
- product (fk product.asin)
- timestamp
- price
- pk (customer + product + timestamp)


## store

- uid (pk)
- name
- address (fk address.uid)


## products_in_store

- store (fk store.uid)
- product (fk products.asin)
- currency
- price
- availability
- condition
- pk (store + product + condition)


## similiar_product

- product1 (fk product.asin)
- product2 (fk product.asin)
- pk (product1 + product2)


## is_in_category

- product (fk product.asin)
- category (fk category.uid)
- pk (product + category)

## book

- asin (pk + fk product.asin)
- pages
- release_date
- isbn


## dvd

- asin (pk + fk product.asin)
- ean
- format
- runtime
- release_date
- region_code


## cd

- asin (pk + fk product.asin)#
- ean
- release_date
- tracks (array)


## person
- uid (pk)
- name

## company
- uid (pk)
- name


## associated_company (stores artist, authors and dvd associates all in one)

- company (fk company)
- product (fk product.uid)
- role (label/publisher)
- pk (company + product)


## associated_person (stores artist, authors and dvd associates all in one)

- person (fk person.uid)
- product (fk product.asin)
- title (artist/author/creator/director/actor)
- pk( person + product + title)