CREATE TABLE "VEHICLE" (
    "ID" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    "BRAND" VARCHAR(25) NOT NULL,
    "PRICE" DECIMAL(19,4) NOT NULL,
    CONSTRAINT "VEHICLE_PK" PRIMARY KEY ("ID"),
    CONSTRAINT "PRICE_MIN" CHECK ("PRICE" > 0)
);

CREATE TABLE "CUSTOMER" (
    "ID" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,    
    "NAME" VARCHAR(25) NOT NULL,
    "ADDRESS" VARCHAR(25) NOT NULL,
    "PHONE" VARCHAR(25) NOT NULL,
    "EMAIL" VARCHAR(25) NOT NULL,
    CONSTRAINT "CUSTOMER_PK" PRIMARY KEY ("ID")    
);

CREATE TABLE "RESERVATION" (
    "ID" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,     
    "VEHICLE" BIGINT NOT NULL,
    "CUSTOMER" BIGINT NOT NULL,
    "STARTDATE" TIMESTAMP NOT NULL,
    "ENDDATE" TIMESTAMP NOT NULL,
    "REALENDDATE" TIMESTAMP DEFAULT NULL,
    "INFO" VARCHAR(255),
    CONSTRAINT "RESERVATION_PK" PRIMARY KEY ("ID"),
    CONSTRAINT "RESERVATION_VEHICLE_FK" FOREIGN KEY ("VEHICLE") REFERENCES "VEHICLE" ("ID"),
    CONSTRAINT "RESERVATION_CUSTOMER_FK" FOREIGN KEY ("CUSTOMER") REFERENCES "CUSTOMER" ("ID")     
);