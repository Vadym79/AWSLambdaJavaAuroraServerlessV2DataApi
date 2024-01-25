Example of Lambda using the new Amazon Aurora Serverless V2 Data API with AWS SDK for Java

To use it you're required to:

1) Execute these 2  sql statements before calling GetProductByIdViaServerlessV2DataAPILambda via API Gateway

CREATE TABLE products (
    id int NOT NULL,
    name varchar(255) NOT NULL,
    price decimal NOT NULL,
    PRIMARY KEY (id)    
);

insert 50 products with id 1 to 50

INSERT INTO products (id, name, price)
VALUES (1, 'Photobook A3', 2.19); 

....

...
INSERT INTO products (id, name, price)
VALUES (50, 'Calender A5', 43.65);

2)
Transactional Example

CREATE TABLE tbl_user (
    id bigint NOT NULL,
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    email varchar(128) NOT NULL,
    PRIMARY KEY (id)    
);

CREATE SEQUENCE user_id START 1;

CREATE TABLE tbl_user_address (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    street varchar(255) NOT NULL,
    city varchar(255) NOT NULL,
    country varchar(128) NOT NULL,
    zip varchar(64) NOT NULL,
    PRIMARY KEY (id) ,  
    CONSTRAINT fk_user_id
      FOREIGN KEY(user_id) 
	  REFERENCES tbl_user(id)
);

CREATE SEQUENCE user_address_id START 1;

Successful JSON Request to create user and address


{
  "first_name": "Vadym",
  "last_name":  "Kazulkin",
  "email":  "blabla@email.com",
  "address": {
     "street": "Alexandra Platz",
     "city": "Berlin",
     "country": "Germany",
     "zip": "53334"
   }
}
 

Failure JSON Request (missing user's last name) to create user and address


{
  "first_name": "Vadym",
  "email":  "blabla@email.com",
  "address": {
     "street": "Alexandra Platz",
     "city": "Berlin",
     "country": "Germany",
     "zip": "53334"
   }
}


3) Provide your own subnet ids in the template.yaml in the Parameters section

 Subnets:
    Type: CommaDelimitedList  
    Default: subnet-0787be4d, subnet-88dc46e0
    Description: The list of SubnetIds, for at least two Availability Zones in the
      region in your Virtual Private Cloud (VPC)