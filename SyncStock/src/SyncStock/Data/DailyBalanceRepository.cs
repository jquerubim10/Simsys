using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SyncStock.Domain;
using Dapper;
using System.Linq.Expressions;

namespace SyncStock.Data
{
    public class DailyBalanceRepository : Repository<DailyBalance,long>
    {
        DbSession _session;
        public DailyBalanceRepository(DbSession session) : base(session)
        {
            _session = session;
        }

        public void Process(StockProcessing process, DateTime referenceDate)
        {
            Console.WriteLine("Processando os dados coleados...");
            var sql = @"SET NOCOUNT ON
                        CREATE TABLE #TMP_ENTRADAS(
                            ID BIGINT, 
                            DATA DATETIME,
                            CENTROCUSTO INT,
                            PRODUTO INT,
                            LOTE VARCHAR(50),
                            VALIDADE DATETIME,
                            CUSTO NUMERIC(19,11),
                            QUANTIDADE NUMERIC(19,11),
                            SALDO NUMERIC(19,11),
                            ALTERADO BIT DEFAULT(1)
                        )

                        CREATE TABLE #TMP_FRACIONADO(
                           ID_ORIGEM BIGINT,
                           ID_ENTRADA BIGINT,
                           DATA_MOVIMENTO DATETIME,
                           QTDE NUMERIC(19,11),
                           CUSTO NUMERIC(19,11)
                        )

                        INSERT INTO #TMP_ENTRADAS(ID, DATA, CENTROCUSTO, PRODUTO, LOTE, VALIDADE, CUSTO, QUANTIDADE, SALDO, ALTERADO)                        
                        SELECT DISTINCT S.ID, S.DATA_MOVIMENTO, S.ESTOQUECC, S.PRODUTO, S.LOTE, S.VALIDADE, S.CUSTO_UNIT, S.QUANTIDADE, ISNULL(P.SALDO,0) AS SALDO, 0
                        FROM SE_HIST_MOVIMENTO_ESTOQUE S
                        LEFT JOIN SE_HIST_PRODUTOSALDO P
                                ON P.DATA = CONVERT(DATETIME,@ReferenceDate) -1
                               AND P.ID_ENTRADA = S.ID
                               AND P.CENTROCUSTO = S.ESTOQUECC
                               AND P.PRODUTO = S.PRODUTO
                               AND P.LOTE = S.LOTE
                               AND P.VALIDADE = S.VALIDADE
                        WHERE S.MOVIMENTO = 1 
                          AND ((S.EVENTO LIKE 'ENTRADA_%') OR (S.EVENTO LIKE '%EMPRESTIMO%') OR (S.EVENTO LIKE '%AJUSTE_ESTOQUE%'))
                          AND S.DATA_MOVIMENTO < @ReferenceDate                        

                        --SELECT * INTO TMP_ENTRADAS_PRE FROM #TMP_ENTRADAS

                        DELETE FROM SE_HIST_PRODUTOSALDO
                              WHERE DATA >= @ReferenceDate

                        DELETE FROM SE_HIST_MOVIMENTO_ESTOQUE 
                              WHERE ID_ORIGEM_FRA IS NOT NULL
                                AND DATA_MOVIMENTO >= @ReferenceDate

                        DECLARE PROC_INICIALIZACAO CURSOR FOR 
                          SELECT DISTINCT M.PRODUTO, M.ESTOQUECC, M.DATA_MOVIMENTO
                            FROM SE_HIST_MOVIMENTO_ESTOQUE M
                           WHERE M.EVENTO = 'AJUSTE_ESTOQUE'
                             AND M.MOVIMENTO = 0
                             AND M.DATA_MOVIMENTO >= @ReferenceDate
                           ORDER BY M.PRODUTO ASC, M.ESTOQUECC ASC, M.DATA_MOVIMENTO ASC

                        DECLARE PROC_SALDO CURSOR FOR 
                            SELECT M.ID, M.DATA_MOVIMENTO, M.EVENTO, M.MOVIMENTO, M.ESTOQUECC, M.PRODUTO, M.LOTE, 
                                   M.VALIDADE, M.QUANTIDADE AS QUANTIDADE, M.CUSTO_UNIT
                            FROM SE_HIST_MOVIMENTO_ESTOQUE M
                            WHERE M.DATA_MOVIMENTO >= @ReferenceDate
                            ORDER BY M.PRODUTO ASC, M.DATA_MOVIMENTO ASC, M.ESTOQUECC, M.LOTE, M.VALIDADE, M.MOVIMENTO DESC

                        DECLARE @ID BIGINT
                        DECLARE @DATA_A DATETIME
                        DECLARE @DATA_B DATETIME
                        DECLARE @EVENTO CHAR(50)
                        DECLARE @MOVIMENTO CHAR(50)
                        DECLARE @PRODUTO_A INT
                        DECLARE @PRODUTO_B INT
                        DECLARE @ESTOQUECC_A INT
                        DECLARE @ESTOQUECC_B INT
                        DECLARE @LOTE_A VARCHAR(50)
                        DECLARE @LOTE_B VARCHAR(50)
                        DECLARE @VALIDADE_A DATETIME
                        DECLARE @VALIDADE_B DATETIME
                        DECLARE @QUANTIDADE NUMERIC(19,11)
                        DECLARE @QUANTIDADE_AUX NUMERIC(19,11)
                        DECLARE @ACUMULADO NUMERIC(19,11)
                        DECLARE @CUSTO NUMERIC(19,11)
                        DECLARE @SALDO NUMERIC(19,11)
                        DECLARE @ID_NOTA BIGINT
                        DECLARE @CUSTO_NOTA NUMERIC(19,11)

                        SELECT 'PROCESSANDO INICIALIZAÇÕES'

                        OPEN PROC_INICIALIZACAO
                        FETCH NEXT FROM PROC_INICIALIZACAO INTO @PRODUTO_A, @ESTOQUECC_A, @DATA_A
                        WHILE @@FETCH_STATUS = 0
                        BEGIN
   
                            IF @PRODUTO_A <> @PRODUTO_B OR @ESTOQUECC_A <> @ESTOQUECC_B SET @DATA_B = NULL
    
                            INSERT INTO SE_HIST_MOVIMENTO_ESTOQUE(ID_PROCESSAMENTO, DATA_MOVIMENTO, DOCUMENTO, SERIE, DATA_EMISSAO,
                               EVENTO, MOVIMENTO, PRODUTO, LOTE, VALIDADE, QUANTIDADE, CUSTO_UNIT, CUSTO_TOTAL, ID_ENTRADA, SALDO, ESTOQUECC,
                               CONSUMOCC, SEQUENCIA, TERCEIRO, FORNECEDOR, PEDIDO, EMPENHO, DOACAO, SALDO_ALTERADO)
                               SELECT DISTINCT @ProcessId ID_PROCESSAMENTO,       
                                      @DATA_A AS DATA_MOVIMENTO,
                                      NULL AS DOCUMENTO,
                                      NULL AS SERIE,
                                      NULL AS DATA_EMISSAO,
                                      'AJUSTE_ESTOQUE' AS EVENTO,
                                      -1 MOVIMENTO,
                                      M.PRODUTO,
                                      M.LOTE,
                                      M.VALIDADE,
                                      0 AS QUANTIDADE,
                                      0 AS CUSTO_UNIT,
                                      0 AS CUSTO_TOTAL,
                                      NULL ID_ENTRADA,
                                      0 AS SALDO,
                                      M.ESTOQUECC AS ESTOQUECC,
                                      NULL AS CONSUMOCC,
                                      NULL AS SEQUENCIA,
                                      NULL AS TERCEIRO,
                                      NULL AS FORNCECEDOR,
                                      NULL AS PEDIDO,
                                      NULL AS EMPENHO,
                                      NULL AS DOACAO,
                                      0 AS SALDO_ALTERADO
                               FROM SE_HIST_MOVIMENTO_ESTOQUE M
                               WHERE 1 = 1
                                AND M.DATA_MOVIMENTO < @DATA_A
                                --AND M.DATA_MOVIMENTO >= ISNULL(@DATA_B,'1900/01/01')
                                AND M.PRODUTO = @PRODUTO_A
                                AND M.ESTOQUECC = @ESTOQUECC_A
                                AND NOT EXISTS( SELECT TOP 1 1 
                                                  FROM SE_HIST_MOVIMENTO_ESTOQUE X
                                                  WHERE X.PRODUTO = M.PRODUTO
                                                    AND X.ESTOQUECC = M.ESTOQUECC
                                                    AND X.DATA_MOVIMENTO = @DATA_A
                                                    AND X.LOTE = M.LOTE
                                                    AND X.VALIDADE = M.VALIDADE
                                                    AND X.EVENTO = 'AJUSTE_ESTOQUE')
                               GROUP BY M.DATA_MOVIMENTO, M.PRODUTO, M.LOTE, M.VALIDADE, M.ESTOQUECC       
                               HAVING ISNULL(SUM(M.QUANTIDADE * M.MOVIMENTO),0) <> 0
       
                            UPDATE H
                               SET H.QUANTIDADE = ABS(ISNULL(H.SALDO_ALTERADO,H.QUANTIDADE) - ISNULL((SELECT SUM(X.QUANTIDADE * X.MOVIMENTO)
                                                                  FROM SE_HIST_MOVIMENTO_ESTOQUE X
                                                                 WHERE X.PRODUTO = H.PRODUTO
                                                                   AND X.ESTOQUECC = H.ESTOQUECC
                                                                   AND X.LOTE = H.LOTE
                                                                   AND X.VALIDADE = H.VALIDADE
                                                                   AND X.DATA_MOVIMENTO < H.DATA_MOVIMENTO
                                                                   --AND X.DATA_MOVIMENTO >=  ISNULL(@DATA_B,'1900/01/01')
                                                                   HAVING ISNULL(SUM(X.QUANTIDADE * X.MOVIMENTO),0) > 0
                                                                   ),0)),
                                   H.MOVIMENTO = CASE WHEN ISNULL(H.SALDO_ALTERADO,H.QUANTIDADE) - ISNULL((SELECT SUM(X.QUANTIDADE * X.MOVIMENTO)
                                                                  FROM SE_HIST_MOVIMENTO_ESTOQUE X
                                                                 WHERE X.PRODUTO = H.PRODUTO
                                                                   AND X.ESTOQUECC = H.ESTOQUECC
                                                                   AND X.LOTE = H.LOTE
                                                                   AND X.VALIDADE = H.VALIDADE
                                                                   AND X.DATA_MOVIMENTO < H.DATA_MOVIMENTO
                                                                   --AND X.DATA_MOVIMENTO >=  ISNULL(@DATA_B,'1900/01/01')                                    
                                                                   HAVING ISNULL(SUM(X.QUANTIDADE * X.MOVIMENTO),0) > 0
                                                                   ),0) < 0 
                                                    THEN -1 ELSE 1 END
                               FROM SE_HIST_MOVIMENTO_ESTOQUE H
                               WHERE H.DATA_MOVIMENTO = @DATA_A
                                 AND H.PRODUTO = @PRODUTO_A
                                 AND H.ESTOQUECC = @ESTOQUECC_A
                                 AND H.EVENTO = 'AJUSTE_ESTOQUE'
   
                           SET @PRODUTO_B = @PRODUTO_A
                           SET @ESTOQUECC_B = @ESTOQUECC_A
                           SET @DATA_B = @DATA_A
   
                           FETCH NEXT FROM PROC_INICIALIZACAO INTO @PRODUTO_A, @ESTOQUECC_A, @DATA_A
                        END
                        CLOSE PROC_INICIALIZACAO
                        DEALLOCATE PROC_INICIALIZACAO

                        SELECT 'PROCESSANDO MOVIMENTAÇÕES'
 
                        OPEN PROC_SALDO
                        FETCH NEXT FROM PROC_SALDO
                        INTO @ID, @DATA_A, @EVENTO, @MOVIMENTO, @ESTOQUECC_A, @PRODUTO_A, @LOTE_A, @VALIDADE_A, @QUANTIDADE, @CUSTO
                        WHILE @@FETCH_STATUS = 0
                        BEGIN
           
                            IF @PRODUTO_A <> ISNULL(@PRODUTO_B,0) -- OR @LOTE_A <> @LOTE_B OR @VALIDADE_A <> @VALIDADE_B 
                            BEGIN   
                                IF @DATA_B IS NULL SET @DATA_B = @ReferenceDate
                                IF @PRODUTO_B IS NULL SET @PRODUTO_B = 0
                                --SET @DATA_B = @ReferenceDate
                                --SET @DATA_B = '1900/01/01'
                                PRINT 'PROCESSANDO PRODUTO:' +  CONVERT(VARCHAR,@PRODUTO_A) + ' LOTE:' + CONVERT(VARCHAR,@LOTE_A)
                            END 
        
                            IF CONVERT(VARCHAR(10),@DATA_A,121) <> CONVERT(VARCHAR(10),@DATA_B,121) OR @PRODUTO_A <> @PRODUTO_B
                            BEGIN
                                /*Atualiza saldo do produto B*/
                                INSERT INTO SE_HIST_PRODUTOSALDO(DATA, ID_ENTRADA, CENTROCUSTO, PRODUTO, LOTE, VALIDADE, CUSTO_UNIT, SALDO, CUSTO_TOTAL)
                                SELECT DISTINCT D.DATA, T.ID, T.CENTROCUSTO, T.PRODUTO, T.LOTE, T.VALIDADE, T.CUSTO, T.SALDO, ROUND(T.SALDO * T.CUSTO,2)
                                FROM DIAMES D
                                JOIN #TMP_ENTRADAS T
                                    ON T.PRODUTO = @PRODUTO_B
                                  -- AND T.CENTROCUSTO = @ESTOQUECC_B
                                  -- AND T.LOTE = @LOTE_B
                                  -- AND T.VALIDADE = @VALIDADE_B
                                WHERE 1 = 1
                                AND D.DATA >= CONVERT(DATETIME,CONVERT(VARCHAR(10),@DATA_B,121))
                                AND D.DATA < CASE WHEN @PRODUTO_A <> @PRODUTO_B THEN CONVERT(DATETIME,CONVERT(VARCHAR(10),GETDATE(),121)) ELSE CONVERT(DATETIME,CONVERT(VARCHAR(10),@DATA_A,121)) END
                                AND T.SALDO > 0

                                /*Atualiza saldo do produto A quando diferente do produto B*/
                                IF @PRODUTO_A <> @PRODUTO_B
                                BEGIN
                                    /*Marca o produto como alterado*/
                                    UPDATE #TMP_ENTRADAS 
                                       SET ALTERADO = 1 
                                     WHERE PRODUTO = @PRODUTO_A 
                                       AND SALDO > 0
                                    
                                    /*Atualiza o saldo do produto*/
                                    INSERT INTO SE_HIST_PRODUTOSALDO(DATA, ID_ENTRADA, CENTROCUSTO, PRODUTO, LOTE, VALIDADE, CUSTO_UNIT, SALDO, CUSTO_TOTAL)
                                    SELECT DISTINCT D.DATA, T.ID, T.CENTROCUSTO, T.PRODUTO, T.LOTE, T.VALIDADE, T.CUSTO, T.SALDO, ROUND(T.SALDO * T.CUSTO,2)
                                    FROM DIAMES D
                                    JOIN #TMP_ENTRADAS T
                                        ON T.PRODUTO = @PRODUTO_A
                                      -- AND T.CENTROCUSTO = @ESTOQUECC_A
                                      -- AND T.LOTE = @LOTE_A
                                      -- AND T.VALIDADE = @VALIDADE_A
                                    WHERE 1 = 1
                                    AND D.DATA >= CONVERT(DATETIME,CONVERT(VARCHAR(10),@ReferenceDate,121))
                                    AND D.DATA < CONVERT(DATETIME,CONVERT(VARCHAR(10),@DATA_A,121))
                                    AND T.SALDO > 0                                   
                                END
                            END 
        
                            IF @MOVIMENTO = 1 AND ((@EVENTO LIKE 'ENTRADA_%') OR (@EVENTO LIKE '%EMPRESTIMO%') OR (@EVENTO LIKE '%AJUSTE_ESTOQUE%'))
                            BEGIN
                                IF @EVENTO LIKE '%AJUSTE_ESTOQUE%' AND ISNULL(@CUSTO,0) = 0
                                 --SET @CUSTO = ISNULL((SELECT TOP 1 CUSTO FROM #TMP_ENTRADAS WHERE PRODUTO = @PRODUTO_A AND DATA <= @DATA_A ORDER BY DATA DESC),0)
                                 SET @CUSTO = ISNULL((SELECT TOP 1 CUSTO_UNIT FROM SE_HIST_MOVIMENTO_ESTOQUE WHERE PRODUTO = @PRODUTO_A AND DATA_MOVIMENTO <= @DATA_A ORDER BY DATA_MOVIMENTO DESC),0)
         
                                INSERT INTO #TMP_ENTRADAS(ID, DATA, CENTROCUSTO, PRODUTO, LOTE, VALIDADE, CUSTO, QUANTIDADE, SALDO)
                                VALUES(@ID, @DATA_A, @ESTOQUECC_A, @PRODUTO_A, @LOTE_A, @VALIDADE_A, @CUSTO, @QUANTIDADE, @QUANTIDADE)
                                SELECT @ID_NOTA = @ID, @CUSTO_NOTA = @CUSTO
                            END
                            ELSE
                            BEGIN
                               SELECT TOP 1 @ID_NOTA = TMP.ID, @CUSTO_NOTA = TMP.CUSTO
                                 FROM #TMP_ENTRADAS TMP
                                WHERE TMP.PRODUTO = @PRODUTO_A
                                 --AND TMP.CENTROCUSTO = @ESTOQUECC_A
                                 AND TMP.LOTE = @LOTE_A
                                 AND TMP.VALIDADE = @VALIDADE_A
                                 AND ((@MOVIMENTO = -1 AND TMP.ID = (SELECT TOP 1 T.ID 
                                               FROM #TMP_ENTRADAS T 
                                              WHERE T.PRODUTO = TMP.PRODUTO
                                              --AND T.CENTROCUSTO = TMP.CENTROCUSTO
                                              AND T.LOTE = TMP.LOTE
                                              AND T.VALIDADE = TMP.VALIDADE
                                              AND T.SALDO >= @QUANTIDADE
                                              --AND T.SALDO > 0
                                              ORDER BY T.DATA ASC, T.ID ASC) AND TMP.SALDO >= @QUANTIDADE)
                                 OR  (@MOVIMENTO = 1 AND TMP.ID = (SELECT TOP 1 T.ID 
                                               FROM #TMP_ENTRADAS T 
                                              WHERE T.PRODUTO = TMP.PRODUTO
                                              --AND T.CENTROCUSTO = TMP.CENTROCUSTO
                                              AND T.LOTE = TMP.LOTE
                                              AND T.VALIDADE = TMP.VALIDADE
                                              AND (T.QUANTIDADE - T.SALDO) >= @QUANTIDADE
                                              ORDER BY T.DATA DESC, T.ID DESC)))
                      
                                UPDATE TMP 
                                   SET TMP.SALDO = TMP.SALDO + (@QUANTIDADE * @MOVIMENTO),
                                       TMP.ALTERADO = 1
                                  FROM #TMP_ENTRADAS TMP
                                 WHERE TMP.ID = @ID_NOTA
                            END                    
    
                            IF @ID_NOTA IS NULL AND @MOVIMENTO = -1 
                            BEGIN              
    
                                SET @QUANTIDADE_AUX = @QUANTIDADE
                                SET @ACUMULADO = 0
        
                                LOOP:
        
                                SELECT TOP 1  @ID_NOTA = TMP.ID, @CUSTO_NOTA = TMP.CUSTO, @SALDO = TMP.SALDO
                                 FROM #TMP_ENTRADAS TMP
                                WHERE TMP.PRODUTO = @PRODUTO_A
                                 --AND TMP.CENTROCUSTO = @ESTOQUECC_A
                                 AND TMP.LOTE = @LOTE_A
                                 AND TMP.VALIDADE = @VALIDADE_A 
                                 AND TMP.SALDO > 0
                                 ORDER BY TMP.DATA ASC
        
                                IF ISNULL(@SALDO,0) > 0
                                BEGIN
                                    IF @SALDO <  (@QUANTIDADE_AUX - @ACUMULADO)
                                    BEGIN
                                       SET @QUANTIDADE = @SALDO
                                       SET @ACUMULADO = @ACUMULADO + @SALDO
                                    END
                                    ELSE 
                                    BEGIN 
                                        SET @QUANTIDADE = @QUANTIDADE_AUX - @ACUMULADO
                                        SET @ACUMULADO = @QUANTIDADE_AUX
                                    END
            
                                    UPDATE TMP 
                                       SET TMP.SALDO = TMP.SALDO + (@QUANTIDADE * @MOVIMENTO),
                                           TMP.ALTERADO = 1
                                      FROM #TMP_ENTRADAS TMP
                                     WHERE TMP.ID = @ID_NOTA
            
                                    INSERT INTO #TMP_FRACIONADO (ID_ORIGEM, ID_ENTRADA, DATA_MOVIMENTO, QTDE, CUSTO)
                                    VALUES(@ID, @ID_NOTA, @DATA_A, @QUANTIDADE, @CUSTO_NOTA );            
            
                                    IF ISNULL(@ACUMULADO,0) < ISNULL(@QUANTIDADE_AUX,0) GOTO LOOP
                                END   
        
                                SELECT @CUSTO_NOTA = SUM(T.QTDE * T.CUSTO)/SUM(T.QTDE)
                                FROM #TMP_FRACIONADO T 
                                WHERE T.ID_ORIGEM = @ID
        
                                SET @ID_NOTA = NULL
                                SET @QUANTIDADE = @QUANTIDADE_AUX
                            END    
    
                            SET @CUSTO_NOTA = ISNULL(@CUSTO_NOTA, 0)
                            --SET @SALDO = @SALDO + (@MOVIMENTO * @QUANTIDADE)
    
                            SET @SALDO = (SELECT  SUM(MOVIMENTO * QUANTIDADE ) FROM SE_HIST_MOVIMENTO_ESTOQUE WHERE DATA_MOVIMENTO <= @DATA_A
                                             AND PRODUTO = @PRODUTO_A AND LOTE = @LOTE_A AND VALIDADE = @VALIDADE_A AND ESTOQUECC = @ESTOQUECC_A)
                     
                            SET @SALDO = (CASE WHEN @SALDO >= 0 THEN @SALDO ELSE 0 END)
    
                            /*ATUALIZA OS DADOS DA MOVIMENTAÇÃO*/
                            UPDATE SE_HIST_MOVIMENTO_ESTOQUE
                            SET SALDO = @SALDO,
                                CUSTO_UNIT = @CUSTO_NOTA,
                                CUSTO_TOTAL = ROUND(@QUANTIDADE * @CUSTO_NOTA,2),
                                ID_ENTRADA = @ID_NOTA
                            WHERE ID = @ID
    
                            /*INSERE OS REGISTROS GERADOS PARA FRAGMENTAR A QUANTIDADE DA NOTA EM NOTAS QUEBRADAS*/
                            INSERT INTO SE_HIST_MOVIMENTO_ESTOQUE(ID_PROCESSAMENTO, DATA_MOVIMENTO, DOCUMENTO, SERIE, DATA_EMISSAO,
                            EVENTO, MOVIMENTO, PRODUTO, LOTE, VALIDADE, QUANTIDADE, CUSTO_UNIT, CUSTO_TOTAL, ID_ENTRADA, SALDO, ESTOQUECC,
                            CONSUMOCC, SEQUENCIA, TERCEIRO, FORNECEDOR, PEDIDO, EMPENHO, DOACAO, QUANTIDADE_FRA, ID_ORIGEM_FRA)
                            SELECT @ProcessId, H.DATA_MOVIMENTO, H.DOCUMENTO, H.SERIE, H.DATA_EMISSAO, H.EVENTO, H.MOVIMENTO, 
                                   H.PRODUTO, H.LOTE, H.VALIDADE, 0 AS QUANTIDADE, T.CUSTO CUSTO_UNIT, ROUND(T.CUSTO * T.QTDE,2) CUSTO_TOTAL, 
                                   T.ID_ENTRADA, H.SALDO, H.ESTOQUECC, H.CONSUMOCC, NULL AS SEQUENCIA, H.TERCEIRO, H.FORNECEDOR, H.PEDIDO, H.EMPENHO, 
                                   H.DOACAO, T.QTDE QUANTIDADE_FRA, T.ID_ORIGEM ID_ORIGEM_FRA
                            FROM SE_HIST_MOVIMENTO_ESTOQUE H
                            INNER JOIN #TMP_FRACIONADO T 
                                    ON T.ID_ORIGEM = H.ID
                            WHERE T.ID_ORIGEM = @ID
    
                            SET @PRODUTO_B = @PRODUTO_A
                            SET @LOTE_B = @LOTE_A
                            SET @VALIDADE_B = @VALIDADE_A
                            SET @DATA_B = @DATA_A
                            SET @ESTOQUECC_B = @ESTOQUECC_A
                            SET @CUSTO_NOTA = NULL
                            SET @ID_NOTA = NULL
    
                            FETCH NEXT FROM PROC_SALDO
                            INTO @ID, @DATA_A, @EVENTO, @MOVIMENTO, @ESTOQUECC_A, @PRODUTO_A, @LOTE_A, @VALIDADE_A, @QUANTIDADE, @CUSTO

                        END
                        
                        /*Insere o saldo da última posição do último produto processado*/
                        INSERT INTO SE_HIST_PRODUTOSALDO(DATA, ID_ENTRADA, CENTROCUSTO, PRODUTO, LOTE, VALIDADE, CUSTO_UNIT, SALDO, CUSTO_TOTAL)
                        SELECT D.DATA, T.ID, T.CENTROCUSTO, T.PRODUTO, T.LOTE, T.VALIDADE, T.CUSTO, T.SALDO, ROUND(T.SALDO * T.CUSTO,2)
                        FROM DIAMES D
                        JOIN #TMP_ENTRADAS T
                            ON T.PRODUTO = @PRODUTO_B      
                        WHERE 1 = 1
                        AND D.DATA >= CONVERT(DATETIME,CONVERT(VARCHAR(10),@DATA_B,121))
                        AND D.DATA < CONVERT(DATETIME,CONVERT(VARCHAR(10),GETDATE(),121)) 
                        AND T.SALDO > 0
                        
                        /*Itens cujo saldo não foi modificado até a próxima movimentação*/                        
                        INSERT INTO SE_HIST_PRODUTOSALDO(DATA, ID_ENTRADA, CENTROCUSTO, PRODUTO, LOTE, VALIDADE, CUSTO_UNIT, SALDO, CUSTO_TOTAL)
                        SELECT DISTINCT D.DATA, T.ID, T.CENTROCUSTO, T.PRODUTO, T.LOTE, T.VALIDADE, T.CUSTO, T.SALDO, ROUND(T.SALDO * T.CUSTO,2)
                        --SELECT COUNT(1)
                        FROM #TMP_ENTRADAS T
                        CROSS JOIN DIAMES D
                        WHERE 1 = 1
                        AND D.DATA >= CONVERT(DATETIME,@ReferenceDate)
                        AND ((T.ALTERADO = 0 AND D.DATA < GETDATE()) OR 
                             (T.ALTERADO = 1 AND 1 = 2 AND D.DATA < ISNULL(( SELECT MIN(S.DATA) 
                                                    FROM SE_HIST_PRODUTOSALDO S
                                                    WHERE S.PRODUTO = T.PRODUTO
                                                    AND S.LOTE = T.LOTE 
                                                    AND S.VALIDADE = T.VALIDADE 
                                                    AND S.ID_ENTRADA = T.ID 
                                                    AND S.DATA > CONVERT(DATETIME,@ReferenceDate)
                                                ),GETDATE())))
                        --AND T.PRODUTO =  1000008                      
                        AND T.SALDO > 0;

                        --SELECT * INTO TMP_ENTRADAS FROM #TMP_ENTRADAS

                        DROP TABLE #TMP_ENTRADAS;
                        DROP TABLE #TMP_FRACIONADO;

                        CLOSE PROC_SALDO
                        DEALLOCATE PROC_SALDO";

            var result = _session.Connection
                                 .Execute(sql, new { @ProcessId = process.Id, @ReferenceDate = referenceDate }, _session.Transaction,720);
            Console.WriteLine("Concluido o Processamento...");

        }
    }
}
