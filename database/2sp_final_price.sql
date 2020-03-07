USE SAB2019
go

   CREATE VIEW Discounts
	as
	SELECT art.Id as articleId, a.Price as price, s.Discount as discount, s.Id as shopId
	FROM Shop s, Amount a, Article art
	WHERE s.Id = a.IdShop AND art.Id = a.IdArticle
	--select * from Discounts
go


CREATE PROCEDURE dbo.SP_FINAL_PRICE 
    @orderId int  
AS
BEGIN

	-- 1. kursorm preci kroz sve artikle i dodavati po jedan, sa popustom!
	DECLARE @kursor CURSOR 
	DECLARE @artId int, @cnt int
	DECLARE @sum decimal(10,3) = 0, @sum_with_discount decimal(10,3) = 0

	--UPDATE Seller SET Amount = 0, SysPercentage = 0 WHERE IdOrder = @orderId

	SET @kursor = CURSOR FOR
	SELECT IdArticle, [Count]
	FROM Contain
	WHERE IdOrder = @orderId

	OPEN @kursor

	FETCH NEXT FROM @kursor
	INTO @artId, @cnt

	WHILE @@FETCH_STATUS = 0
	BEGIN
		DECLARE @final_price decimal(10,3) = 0, @no_discount decimal(10,3) = 0, @shop int

		SELECT @final_price = price * (1 - discount*0.01) * @cnt, 
			   @no_discount = price * @cnt, 
			   @shop = shopId
		FROM Discounts
		WHERE @artId = articleId

		-- update Seller
		IF EXISTS (SELECT * FROM Seller WHERE IdOrder = @orderId AND IdShop = @shop)
			UPDATE Seller SET Amount = Amount + @final_price
			WHERE IdOrder = @orderId AND IdShop = @shop
		ELSE 
			INSERT INTO Seller(IdShop, IdOrder, Amount, SysPercentage)
			VALUES (@shop, @orderId, @final_price, 0)

		SET @sum = @sum + @no_discount
		SET @sum_with_discount = @sum_with_discount + @final_price

		FETCH NEXT FROM @kursor
		INTO @artId, @cnt
	END
	
	CLOSE @kursor
	DEALLOCATE @kursor

	-- 2. selektovati sve ZAVRSENE porudzbine sa istog kupca i izracunati sumu - da li prelazi 10000?
	DECLARE @buyerId int, @prev_amount decimal(10,3)
	--DECLARE @sum_of_sys_profit decimal(10,3) = 0

	SELECT @buyerId = IdBuyer
	FROM [Order]
	WHERE @orderId = Id

	SELECT @prev_amount = SUM(FinalPrice)
	FROM [Order]
	WHERE Id <> @orderId AND [Status] = 'received' AND @buyerId = IdBuyer AND
		DATEDIFF(DAY, ReceivedTime, GETDATE()) < 30

	-- 3. update tabela
	IF (@prev_amount > 10000)
	BEGIN
		UPDATE [Order] SET FinalPrice = @sum_with_discount * 0.98, -- 2% popusta 
				   	DiscountSum = @sum - @sum_with_discount + @sum_with_discount * 0.02 -- DA LI OVO TREBA???
		WHERE Id = @orderId
		
		-- dodati 3% sistemu, i te iznose oduzeti od Amount iz Seller!
		--SELECT @sum_of_sys_profit = SUM(Amount) * 0.03 
		--FROM Seller 
		--WHERE IdOrder = @orderId

		UPDATE Seller SET Amount = Amount * 0.97, SysPercentage = 3 WHERE IdOrder = @orderId
		--UPDATE Profit SET Amount = Amount + @sum_of_sys_profit WHERE IdOrder = @orderId
	END
	ELSE 
	BEGIN
		UPDATE [Order] 
		SET FinalPrice = @sum_with_discount, 
			DiscountSum = (@sum-@sum_with_discount) 
		WHERE Id = @orderId
		
		-- dodati 5% sistemu, i te iznose oduzeti od Amount iz Seller!
		--SELECT @sum_of_sys_profit = SUM(Amount) * 0.05 
		--FROM Seller 
		--WHERE IdOrder = @orderId

		UPDATE Seller SET Amount = Amount * 0.95, SysPercentage = 5 WHERE IdOrder = @orderId
		-- UPDATE Profit SET Amount = Amount + @sum_of_sys_profit WHERE IdOrder = @orderId
	END

RETURN 0

END 


--print DATEDIFF(DAY, '2019-06-15 23:59:59.9999999', GETDATE()) 