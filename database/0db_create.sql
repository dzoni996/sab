CREATE DATABASE SAB2019
go
USE SAB2019
go
CREATE TABLE [Amount]
( 
	[IdShop]             integer  NOT NULL ,
	[IdArticle]          integer  NOT NULL ,
	[Amount]             integer  NOT NULL ,
	[Price]              decimal(10,3)  NOT NULL 
)
go

ALTER TABLE [Amount]
	ADD CONSTRAINT [XPKAmount] PRIMARY KEY  CLUSTERED ([IdShop] ASC,[IdArticle] ASC)
go

CREATE TABLE [Article]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[Name]               nvarchar(100)  NOT NULL 
)
go

ALTER TABLE [Article]
	ADD CONSTRAINT [XPKArticle] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [Buyer]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[Name]               nvarchar(100)  NOT NULL ,
	[Credit]             decimal(10,3)  NOT NULL ,
	[IdCity]             integer  NOT NULL 
)
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [XPKBuyer] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [City]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[Name]               nvarchar(100)  NOT NULL 
)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [CityName] UNIQUE ([Name]  ASC)
go

CREATE TABLE [Connection]
( 
	[Id1]                integer  NOT NULL ,
	[Id2]                integer  NOT NULL ,
	[Id]                 integer  IDENTITY  NOT NULL ,
	[Distance]           integer  NOT NULL 
)
go

ALTER TABLE [Connection]
	ADD CONSTRAINT [XPKConnection] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [Contain]
( 
	[IdArticle]          integer  NOT NULL ,
	[IdOrder]            integer  NOT NULL ,
	[Count]              integer  NOT NULL ,
	[Id]                 integer  IDENTITY  NOT NULL 
)
go

ALTER TABLE [Contain]
	ADD CONSTRAINT [XPKContain] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [Order]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[ReceivedTime]       datetime  NULL ,
	[SentTime]           datetime  NULL ,
	[Status]             varchar(20)  NOT NULL ,
	[IdCity]             integer  NULL ,
	[IdBuyer]            integer  NOT NULL ,
	[FinalPrice]         decimal(10,3)  NULL ,
	[DiscountSum]        decimal(10,3)  NULL ,
	[IdCityNearest]      integer  NULL ,
	[ReceivedTimeNearest] datetime  NULL 
)
go

ALTER TABLE [Order]
	ADD CONSTRAINT [XPKOrder] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [Profit]
( 
	[IdSystem]           integer  NOT NULL ,
	[IdOrder]            integer  NOT NULL ,
	[Amount]             decimal(10,3)  NOT NULL 
	CONSTRAINT [Default_Value_350_291916850]
		 DEFAULT  0
)
go

ALTER TABLE [Profit]
	ADD CONSTRAINT [XPKProfit] PRIMARY KEY  CLUSTERED ([IdSystem] ASC,[IdOrder] ASC)
go

CREATE TABLE [Seller]
( 
	[IdShop]             integer  NOT NULL ,
	[IdOrder]            integer  NOT NULL ,
	[Amount]             decimal(10,3)  NOT NULL 
	CONSTRAINT [Default_Value_350_543770163]
		 DEFAULT  0,
	[SysPercentage]      integer  NOT NULL 
)
go

ALTER TABLE [Seller]
	ADD CONSTRAINT [XPKSeller] PRIMARY KEY  CLUSTERED ([IdShop] ASC,[IdOrder] ASC)
go

CREATE TABLE [Shop]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[Name]               nvarchar(100)  NOT NULL ,
	[Discount]           integer  NOT NULL 
	CONSTRAINT [MinMaxDiscount_1937020928]
		CHECK  ( Discount BETWEEN 0 AND 100 ),
	[DateFrom]           datetime  NULL ,
	[DateTo]             datetime  NULL ,
	[IdCity]             integer  NOT NULL 
)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [XPKShop] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [AKShop] UNIQUE ([Name]  ASC)
go

CREATE TABLE [System]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[Name]               nvarchar(100)  NOT NULL 
)
go

ALTER TABLE [System]
	ADD CONSTRAINT [XPKSystem] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [Transaction]
( 
	[IdOrder]            integer  NOT NULL ,
	[Id]                 integer  IDENTITY  NOT NULL ,
	[Amount]             decimal(10,3)  NOT NULL ,
	[ExecutionTime]      datetime  NOT NULL ,
	[IdBuyer]            integer  NULL ,
	[IdShop]             integer  NULL 
)
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [XPKTransaction] PRIMARY KEY  CLUSTERED ([Id] ASC)
go


ALTER TABLE [Amount]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([IdShop]) REFERENCES [Shop]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Amount]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([IdArticle]) REFERENCES [Article]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Buyer]
	ADD CONSTRAINT [R_12] FOREIGN KEY ([IdCity]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Connection]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([Id1]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Connection]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([Id2]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Contain]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([IdArticle]) REFERENCES [Article]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Contain]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Order]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([IdCity]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Order]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([IdBuyer]) REFERENCES [Buyer]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Order]
	ADD CONSTRAINT [R_20] FOREIGN KEY ([IdCityNearest]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Profit]
	ADD CONSTRAINT [R_15] FOREIGN KEY ([IdSystem]) REFERENCES [System]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Profit]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Seller]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([IdShop]) REFERENCES [Shop]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Seller]
	ADD CONSTRAINT [R_10] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Shop]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([IdCity]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_14] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_18] FOREIGN KEY ([IdBuyer]) REFERENCES [Buyer]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_19] FOREIGN KEY ([IdShop]) REFERENCES [Shop]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go
