USE [SAB2019]
GO

CREATE PROCEDURE decreaseAmount 
	@shopId int, 
	@articleId int, 
	@cnt int
AS
BEGIN
	UPDATE Amount SET Amount = Amount - @cnt WHERE IdShop = @shopId AND IdArticle = @articleId
END

go

CREATE PROCEDURE updateContain 
    @articleId int,
    @cnt int, 
	@orderId int  
AS
	IF (EXISTS (SELECT * FROM Contain WHERE IdArticle = @articleId AND IdOrder = @orderId))
		UPDATE Contain SET [Count] = [Count] + @cnt WHERE IdArticle = @articleId AND IdOrder = @orderId
	ELSE 
		INSERT INTO Contain(IdArticle, IdOrder, [Count]) VALUES (@articleId, @orderId, @cnt)
RETURN 0 

go

CREATE PROCEDURE addArticle
(
    @articleId int,
	@shopId int,
    @cnt int, 
	@orderId int,
	@result int OUTPUT
)
AS
BEGIN
    IF (SELECT Amount FROM Amount WHERE IdShop = @shopId AND IdArticle = @articleId) >= @cnt
	BEGIN
		EXECUTE decreaseAmount @shopId, @articleId, @cnt
		EXECUTE updateContain @articleId, @cnt, @orderId 
		SELECT @result = 0 
		--RETURN 0
	END
	ELSE
		SELECT @result = -1
	--RETURN -1;
	RETURN 0
END

go