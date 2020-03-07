USE SAB2019
go

ALTER TRIGGER TR_TRANSFER_MONEY_TO_SHOPS
ON [Order]
FOR UPDATE
AS
BEGIN
	DECLARE @kursor CURSOR
	DECLARE @order int, @buyer int

	SET @kursor = CURSOR FOR
	SELECT Id, IdBuyer
	FROM inserted
	WHERE [Status] = 'sent' AND ReceivedTime IS NOT NULL 

	OPEN @kursor

	FETCH NEXT FROM @kursor
	INTO @order, @buyer

	WHILE @@FETCH_STATUS = 0
	BEGIN
		DECLARE @kursor2 CURSOR
		DECLARE @shop int, @amount decimal(10,3), @perc int
		DECLARE @sys int

		IF NOT EXISTS (SELECT * FROM [System] WHERE [Name] = 'Server')
			INSERT INTO [System]([Name]) VALUES ('Server')
		SELECT @sys = Id FROM [System] WHERE [Name] = 'Server';
		--print @sys

		SET @kursor2 = CURSOR FOR
		SELECT IdShop, Amount, SysPercentage
		FROM Seller
		WHERE IdOrder = @order

		OPEN @kursor2

		FETCH NEXT FROM @kursor2
		INTO @shop, @amount, @perc

		WHILE @@FETCH_STATUS = 0
		BEGIN
			UPDATE Buyer SET Credit = Credit - @amount 

			INSERT INTO [Transaction](IdOrder, IdBuyer, IdShop, Amount, ExecutionTime) 
			VALUES (@order, @buyer, @shop, @amount*(1-@perc*0.01), (SELECT SentTime FROM [Order] WHERE Id = @order));
			
			IF NOT EXISTS (SELECT * FROM Profit WHERE IdOrder = @order)
				INSERT INTO Profit(IdSystem, IdOrder, Amount) VALUES (@sys, @order, @amount*(@perc*0.01)/(1-@perc*0.01));
			ELSE 
				UPDATE Profit Set Amount = Amount +  @amount*(@perc*0.01)/(1-@perc*0.01) WHERE IdOrder = @order

			FETCH NEXT FROM @kursor2
			INTO @shop, @amount, @perc
		END

		CLOSE @kursor2
		DEALLOCATE @kursor2

		FETCH NEXT FROM @kursor
		INTO @order, @buyer
	END

	CLOSE @kursor
	--CLOSE @kursor2
	DEALLOCATE @kursor
	--DEALLOCATE @kursor2

END
