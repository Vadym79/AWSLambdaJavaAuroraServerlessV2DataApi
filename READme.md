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

2) Provide your own subnet ids in the template.yaml in the Parameters section

 Subnets:
    Type: CommaDelimitedList  
    Default: subnet-0787be4d, subnet-88dc46e0
    Description: The list of SubnetIds, for at least two Availability Zones in the
      region in your Virtual Private Cloud (VPC)