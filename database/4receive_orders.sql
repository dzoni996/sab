USE SAB2019
go

CREATE PROCEDURE RECEIVE_ORDERS
	@cur_date DATE
AS
BEGIN
	DECLARE @kursor CURSOR
	DECLARE @order INT, @received date, @nearest date, @buyer INT

	SET @kursor = CURSOR FOR
	SELECT Id, ReceivedTime, ReceivedTimeNearest, IdBuyer
	FROM [Order]
	WHERE [Status] = 'sent'

	OPEN @kursor

	FETCH NEXT FROM @kursor
	INTO @order, @received, @nearest, @buyer

	WHILE @@FETCH_STATUS = 0
	BEGIN
		IF @cur_date > @nearest
			UPDATE [Order] SET IdCity = IdCityNearest  WHERE Id = @order
		ELSE IF @cur_date > @received
			UPDATE [Order] SET [Status] = 'received', IdCity = (SELECT IdCity FROM Buyer WHERE Id = @buyer) WHERE Id = @order

		FETCH NEXT FROM @kursor
		INTO @order, @received, @nearest, @buyer
	END

	CLOSE @kursor
	DEALLOCATE @kursor
	
	RETURN 0;
END