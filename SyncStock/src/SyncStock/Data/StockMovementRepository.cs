using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;
using Dapper;
using Dommel;
using SyncStock.Domain;

namespace SyncStock.Data
{
    public class StockMovementRepository : Repository<StockMovement,long>
    {
        private DbSession _session;
        public StockMovementRepository(DbSession session):base(session)
        {
            _session = session;
        }
        #region Syncronization Inserts
        public void SyncEntriesPending(StockProcessing process, DateTime referenceDate)
        {
            Console.WriteLine("Computando as Entradas de Notas Diretas...");
            var sql = @"INSERT INTO SE_HIST_MOVIMENTO_ESTOQUE(ID_PROCESSAMENTO, DATA_MOVIMENTO, DOCUMENTO, SERIE, DATA_EMISSAO,
                        EVENTO, MOVIMENTO, PRODUTO, LOTE, VALIDADE, QUANTIDADE, CUSTO_UNIT, CUSTO_TOTAL, ID_ENTRADA, SALDO, ESTOQUECC,
                        CONSUMOCC, SEQUENCIA, TERCEIRO, FORNECEDOR, PEDIDO, EMPENHO, DOACAO)
                        SELECT DISTINCT 
                               @ProcessId ID_PROCESSAMENTO,
                               M.DATALANCAMENTO AS DATA_MOVIMENTO,
                               M.DOCUMENTO,
                               M.SERIE,
                               M.DATA AS  DATA_EMISSAO,
                               CASE WHEN M.TIPO = 0 THEN 'ENTRADA_SEM_PEDIDO' ELSE 'DEV_ENTRADA_SEM_PEDIDO' END AS EVENTO,
                               CASE WHEN M.TIPO = 0 THEN 1 ELSE -1 END MOVIMENTO,
                               M.PRODUTO,
                               M.LOTE,
                               M.VALIDADELOTE,
                               M.QUANTIDADE,
                               M.PRECOBRASINDICE AS CUSTO_UNIT,
                               ROUND(ISNULL(M.VALOR_TOTAL,M.PRECOBRASINDICE * M.QUANTIDADE),2) AS CUSTO_TOTAL,
                               NULL ID_ENTRADA,
                               0 AS SALDO,
                               ISNULL(M.CENTROCUSTO_ENTRADA,M.CCORIGEM) AS ESTOQUECC,
                               NULL CONSUMOCC,
                               M.ID SEQUENCIA,
                               NULL TERCEIRO,
                               M.FORNECEDOR,
                               NULL AS PEDIDO,
                               M.EMPENHO AS EMPENHO,
                               M.DOACAO
                        FROM MOVIMENTOAVULSO M
                        WHERE 1 = 1
                        AND ISNULL(M.FINALIZADO,1) = 1
                        AND NOT EXISTS (
                           SELECT TOP 1 1 
                           FROM SE_HIST_MOVIMENTO_ESTOQUE H
                           WHERE H.DOCUMENTO = M.DOCUMENTO
                           AND H.SERIE = M.SERIE
                           AND H.FORNECEDOR = M.FORNECEDOR
                           AND H.PRODUTO = M.PRODUTO
                           AND H.LOTE = M.LOTE
                           AND H.VALIDADE = M.VALIDADELOTE   
                           AND H.MOVIMENTO = (CASE WHEN M.TIPO = 0 THEN 1 ELSE -1 END)
                           AND H.DATA_MOVIMENTO = M.DATALANCAMENTO
                           AND H.EVENTO IN ('ENTRADA_SEM_PEDIDO','DEV_ENTRADA_SEM_PEDIDO')
                        )
                        AND M.DATALANCAMENTO >= @ReferenceDate;";
            var result = _session.Connection
                                 .Execute(sql, new { @ProcessId = process.Id, @ReferenceDate = referenceDate }, _session.Transaction); 
        }
        public void SyncLendsPending(StockProcessing process, DateTime referenceDate)
        {
            Console.WriteLine("Computando os Emprestimos...");
            var sql = @"INSERT INTO SE_HIST_MOVIMENTO_ESTOQUE(ID_PROCESSAMENTO, DATA_MOVIMENTO, DOCUMENTO, SERIE, DATA_EMISSAO,
                        EVENTO, MOVIMENTO, PRODUTO, LOTE, VALIDADE, QUANTIDADE, CUSTO_UNIT, CUSTO_TOTAL, ID_ENTRADA, SALDO, ESTOQUECC,
                        CONSUMOCC, SEQUENCIA, TERCEIRO, FORNECEDOR, PEDIDO, EMPENHO, DOACAO)
                        SELECT DISTINCT 
                               @ProcessId ID_PROCESSAMENTO,
                               M.DATADIGITACAO AS DATA_MOVIMENTO,
                               M.DOCUMENTO,
                               NULL AS SERIE,
                               M.DATA AS  DATA_EMISSAO,
                               CASE M.TIPOEMPRESTIMO
                                    WHEN 0 THEN(CASE WHEN M.TIPO = 0 THEN 'EMPRESTIMO_SAIDA' ELSE 'DEV_EMPRESTIMO_SAIDA' END)
                                    WHEN 1 THEN(CASE WHEN M.TIPO = 0 THEN 'EMPRESTIMO_ENTRADA' ELSE 'DEV_EMPRESTIMO_ENTRADA' END)
                                    WHEN 2 THEN(CASE WHEN M.TIPO = 0 THEN 'EMPRESTIMO_PACIENTE' ELSE 'DEV_EMPRESTIMO_PACIENTE' END)
                               END AS EVENTO,
                               CASE M.TIPOEMPRESTIMO
                                    WHEN 0 THEN(CASE WHEN M.TIPO = 0 THEN - 1 ELSE 1 END)
                                    WHEN 1 THEN(CASE WHEN M.TIPO = 0 THEN 1 ELSE - 1 END)
                                    WHEN 2 THEN(CASE WHEN M.TIPO = 0 THEN 1 ELSE - 1 END)
                               END MOVIMENTO,
                               M.PRODUTO,
                               M.LOTE,
                               M.VALIDADELOTE,
                               M.QUANTIDADE,
                               M.VALORUNITARIO AS CUSTO_UNIT,
                               ROUND(M.VALORUNITARIO * M.QUANTIDADE, 2) AS CUSTO_TOTAL,
                                NULL ID_ENTRADA,
                               0 AS SALDO,
                               CASE WHEN M.TIPOEMPRESTIMO = 0 THEN M.ORIGEM ELSE M.DESTINO END AS ESTOQUECC,
                               NULL AS CONSUMOCC,
                               M.SEQUENCIA AS SEQUENCIA,
                               CASE M.TIPOEMPRESTIMO WHEN 0 THEN M.DESTINO WHEN 1 THEN M.ORIGEM END AS TERCEIRO,
                               NULL AS FORNCECEDOR,
                               NULL AS PEDIDO,
                               NULL AS EMPENHO,
                               M.DOACAO AS DOACAO
                        FROM MOVIM_EMPRESTIMO M
                        WHERE NOT EXISTS(
                           SELECT TOP 1 1
                           FROM SE_HIST_MOVIMENTO_ESTOQUE H
                           WHERE H.DOCUMENTO = M.DOCUMENTO
                           AND H.PRODUTO = M.PRODUTO
                           AND H.LOTE = M.LOTE
                           AND H.VALIDADE = M.VALIDADELOTE
                           AND H.DATA_MOVIMENTO = M.DATADIGITACAO
                           AND H.SEQUENCIA = M.SEQUENCIA
                           AND H.EVENTO LIKE '%EMPRESTIMO%'
                        )
                        AND M.DATADIGITACAO >= @ReferenceDate";
            var result = _session.Connection
                                 .Execute(sql, new { @ProcessId = process.Id, @ReferenceDate = referenceDate }, _session.Transaction);       
        }
        public void SyncCostCenterMovtsPending(StockProcessing process, DateTime referenceDate)
        {
            Console.WriteLine("Computando as Movimentações entre Centro de Custo...");
            var sql = @"INSERT INTO SE_HIST_MOVIMENTO_ESTOQUE(ID_PROCESSAMENTO, DATA_MOVIMENTO, DOCUMENTO, SERIE, DATA_EMISSAO,
                        EVENTO, MOVIMENTO, PRODUTO, LOTE, VALIDADE, QUANTIDADE, CUSTO_UNIT, CUSTO_TOTAL, ID_ENTRADA, SALDO, ESTOQUECC,
                        CONSUMOCC, SEQUENCIA, TERCEIRO, FORNECEDOR, PEDIDO, EMPENHO, DOACAO)
                        SELECT DISTINCT 
                               @ProcessId ID_PROCESSAMENTO,
                               M.DATADIGITACAO AS DATA_MOVIMENTO,
                               M.DOCUMENTO,
                               NULL AS SERIE,
                               DATA AS  DATA_EMISSAO,
                               CASE WHEN M.TIPO = 0 THEN 'CONSUMO_SETOR' ELSE 'DEV_CONSUMO_SETOR' END AS EVENTO,
                               CASE WHEN M.TIPO = 0 THEN - 1 ELSE 1 END MOVIMENTO,
                                M.PRODUTO,
                               M.LOTE,
                               M.VALIDADELOTE,
                               M.QUANTIDADE,
                               0 AS CUSTO_UNIT,
                               0 AS CUSTO_TOTAL,
                               NULL ID_ENTRADA,
                               0 AS SALDO,
                               M.CCORIGEM AS ESTOQUECC,
                               M.CCDESTINO AS CONSUMOCC,
                               M.SEQUENCIA AS SEQUENCIA,
                               NULL AS TERCEIRO,
                               NULL AS FORNCECEDOR,
                               NULL AS PEDIDO,
                               NULL AS EMPENHO,
                               NULL AS DOACAO
                        FROM MOVIMENTOCC M
                        WHERE 1=1 --ISNULL(M.CONSUMOINTERNO,0) = 1
                        AND NOT EXISTS(
                           SELECT TOP 1 1
                           FROM SE_HIST_MOVIMENTO_ESTOQUE H
                           WHERE H.DOCUMENTO = M.DOCUMENTO
                           AND H.ESTOQUECC = M.CCORIGEM
                           AND H.CONSUMOCC = M.CCDESTINO
                           AND H.SEQUENCIA = M.SEQUENCIA
                           AND H.PRODUTO = M.PRODUTO
                           AND H.LOTE = M.LOTE
                           AND H.VALIDADE = M.VALIDADELOTE
                           AND H.MOVIMENTO = (CASE WHEN M.TIPO = 0 THEN - 1 ELSE 1 END)
                           AND H.DATA_MOVIMENTO = M.DATADIGITACAO
                           AND H.EVENTO IN('CONSUMO_SETOR','DEV_CONSUMO_SETOR')    
                        )
                        AND M.DATADIGITACAO >= @ReferenceDate";
            var result = _session.Connection
                                 .Execute(sql, new { @ProcessId = process.Id, @ReferenceDate = referenceDate }, _session.Transaction);
        }
        public void SyncPatientMovtsPending(StockProcessing process, DateTime referenceDate)
        {
            var sql = @"";
            var result = _session.Connection
                                 .Execute(sql, new { @ProcessId = process.Id, @ReferenceDate = referenceDate }, _session.Transaction);
        }
        public void SyncAdjustMovtsPending(StockProcessing process, DateTime referenceDate)
        {
            Console.WriteLine("Computando as Contagens e Estoque...");
            var sql = @"INSERT INTO SE_HIST_MOVIMENTO_ESTOQUE(ID_PROCESSAMENTO, DATA_MOVIMENTO, DOCUMENTO, SERIE, DATA_EMISSAO,
                        EVENTO, MOVIMENTO, PRODUTO, LOTE, VALIDADE, QUANTIDADE, CUSTO_UNIT, CUSTO_TOTAL, ID_ENTRADA, SALDO, ESTOQUECC,
                        CONSUMOCC, SEQUENCIA, TERCEIRO, FORNECEDOR, PEDIDO, EMPENHO, DOACAO, SALDO_ALTERADO)
                        SELECT DISTINCT 
                               @ProcessId ID_PROCESSAMENTO,
                               (SELECT TOP 1 DATA FROM SAVELOG..CONTAGEMESTOQUE M2 WHERE M2.ATUALIZACAO = M.ATUALIZACAO ORDER BY DATA ASC) AS DATA_MOVIMENTO,
                               NULL AS DOCUMENTO,
                               NULL AS SERIE,
                               NULL AS DATA_EMISSAO,
                               'AJUSTE_ESTOQUE' AS EVENTO,
                               0 MOVIMENTO,
                               M.PRODUTO,
                               M.LOTE,
                               M.VALIDADE,
                               M.SALDONOVO AS QUANTIDADE,
                               M.CUSTO AS CUSTO_UNIT,
                               ROUND(M.CUSTO * M.SALDONOVO,2) AS CUSTO_TOTAL,
                               NULL ID_ENTRADA,
                               0 AS SALDO,
                               M.CENTROCUSTO AS ESTOQUECC,
                               NULL AS CONSUMOCC,
                               NULL AS SEQUENCIA,
                               NULL AS TERCEIRO,
                               NULL AS FORNCECEDOR,
                               NULL AS PEDIDO,
                               NULL AS EMPENHO,
                               NULL AS DOACAO,
                               M.SALDONOVO AS SALDO_ALTERADO
                        FROM SAVELOG..CONTAGEMESTOQUE M
                        WHERE 1 = 1
                        AND NOT EXISTS(
                           SELECT TOP 1 1 
                           FROM SE_HIST_MOVIMENTO_ESTOQUE H
                           WHERE H.ESTOQUECC = M.CENTROCUSTO       
                           AND H.PRODUTO = M.PRODUTO
                           AND H.LOTE = M.LOTE
                           AND H.VALIDADE = M.VALIDADE
                           --AND H.MOVIMENTO <> 0
                           AND H.DATA_MOVIMENTO = (SELECT TOP 1 DATA FROM SAVELOG..CONTAGEMESTOQUE M2 WHERE M2.ATUALIZACAO = M.ATUALIZACAO ORDER BY DATA ASC)
                           AND H.EVENTO IN ('AJUSTE_ESTOQUE')
                        )
                        AND ( M.LOTE <> 'LOTEAUX' OR M.SALDONOVO > 0)       
                        AND M.DATA >= (SELECT MIN(DATA_MOVIMENTO) 
                                                 FROM  SE_HIST_MOVIMENTO_ESTOQUE)
                        AND M.DATA >= @ReferenceDate;";
            var result = _session.Connection
                                 .Execute(sql, new { @ProcessId = process.Id, @ReferenceDate = referenceDate }, _session.Transaction);
        }
        public void SyncAllPending(StockProcessing process, DateTime referenceDate)
        {
            SyncEntriesPending(process, referenceDate);
            SyncCostCenterMovtsPending(process, referenceDate);
            SyncLendsPending(process, referenceDate);
            //SyncPatientMovtsPending(process, referenceDate);
            SyncAdjustMovtsPending(process, referenceDate);
        }
        #endregion
        #region Get Itens to Syncronization
        public List<StockMovement> GetEntriesPending(DateTime referenceDate)
        {
            Console.WriteLine("Computando as Entradas de Notas Diretas...");
            var sql = @"SELECT DISTINCT
                               M.DATALANCAMENTO AS EventDate,
                               M.DOCUMENTO AS Document,
                               M.SERIE AS Series,
                               M.DATA AS IssueDate,
                               CASE WHEN M.TIPO = 0 THEN 'ENTRADA_SEM_PEDIDO' ELSE 'DEV_ENTRADA_SEM_PEDIDO' END AS EventType,
                               CASE WHEN M.TIPO = 0 THEN 1 ELSE -1 END AS MovementType,
                               M.PRODUTO AS ProductId,
                               M.LOTE AS LotNumber,
                               M.VALIDADELOTE AS ValidityDate,
                               M.QUANTIDADE AS Quantity,
                               M.PRECOBRASINDICE AS UnityValue,
                               ROUND(ISNULL(M.VALOR_TOTAL,M.PRECOBRASINDICE * M.QUANTIDADE),2) AS TotalValue,
                               --NULL AS EntryId,
                               --0 AS StockBalance,
                               ISNULL(M.CENTROCUSTO_ENTRADA,M.CCORIGEM) AS StockId,
                               --NULL AS ConsumeId,
                               --NULL AS OriginalSequence,
                               --NULL AS OutSourceId,
                               M.FORNECEDOR AS ProviderId,
                               --NULL AS PurchaseOrder ,
                               M.EMPENHO AS Pledge,
                               M.DOACAO AS Donation,
                               NULL AS Nothing
                            FROM MOVIMENTOAVULSO M
                            WHERE 1 = 1
                            AND ISNULL(M.FINALIZADO,1) = 1
                            AND NOT EXISTS (
                                SELECT TOP 1 1 
                                FROM SE_HIST_MOVIMENTO_ESTOQUE H
                                WHERE H.DOCUMENTO = M.DOCUMENTO
                                AND H.SERIE = M.SERIE
                                AND H.FORNECEDOR = M.FORNECEDOR
                                AND H.PRODUTO = M.PRODUTO
                                AND H.LOTE = M.LOTE
                                AND H.VALIDADE = M.VALIDADELOTE   
                                AND H.MOVIMENTO = (CASE WHEN M.TIPO = 0 THEN 1 ELSE -1 END)
                                AND H.DATA_MOVIMENTO = M.DATALANCAMENTO
                                AND H.EVENTO IN ('ENTRADA_SEM_PEDIDO','DEV_ENTRADA_SEM_PEDIDO')
                            )
                            AND M.DATALANCAMENTO >= @ReferenceDate;";
            var result = _session.Connection.Query<StockMovement>(sql, new { @ReferenceDate = referenceDate }, _session.Transaction);
            return result.ToList();
        }
        public List<StockMovement> GetLendsPending(DateTime referenceDate)
        {
            Console.WriteLine("Computando os Emprestimos...");
            var sql = @"SELECT DISTINCT 
                        M.DATADIGITACAO AS EventDate,
                        M.DOCUMENTO AS Document,
                        --NULL AS Series,
                        M.DATA AS IssueDate,
                        CASE M.TIPOEMPRESTIMO
                            WHEN 0 THEN(CASE WHEN M.TIPO = 0 THEN 'EMPRESTIMO_SAIDA' ELSE 'DEV_EMPRESTIMO_SAIDA' END)
                            WHEN 1 THEN(CASE WHEN M.TIPO = 0 THEN 'EMPRESTIMO_ENTRADA' ELSE 'DEV_EMPRESTIMO_ENTRADA' END)
                            WHEN 2 THEN(CASE WHEN M.TIPO = 0 THEN 'EMPRESTIMO_PACIENTE' ELSE 'DEV_EMPRESTIMO_PACIENTE' END)
                        END AS EventType,
                        CASE M.TIPOEMPRESTIMO
                            WHEN 0 THEN(CASE WHEN M.TIPO = 0 THEN - 1 ELSE 1 END)
                            WHEN 1 THEN(CASE WHEN M.TIPO = 0 THEN 1 ELSE - 1 END)
                            WHEN 2 THEN(CASE WHEN M.TIPO = 0 THEN 1 ELSE - 1 END)
                        END AS MovementType,
                        M.PRODUTO AS ProductId,
                        M.LOTE AS LotNumber,
                        M.VALIDADELOTE AS ValidityDate,
                        M.QUANTIDADE AS Quantity,
                        M.VALORUNITARIO AS UnityValue,
                        ROUND(M.VALORUNITARIO * M.QUANTIDADE, 2) AS TotalValue,
                        --NULL AS EntryId,
                        --0 AS StockBalance,
                        CASE WHEN M.TIPOEMPRESTIMO = 0 THEN M.ORIGEM ELSE M.DESTINO END AS StockID,
                        --NULL AS ConsumeId,
                        M.SEQUENCIA AS OriginalSequence,
                        CASE M.TIPOEMPRESTIMO WHEN 0 THEN M.DESTINO WHEN 1 THEN M.ORIGEM END AS OutSourceId,
                        --NULL AS ProviderId,
                        --NULL AS PurchaseOrder,
                        --NULL AS Pledge,
                        M.DOACAO AS Donation,
                        NULL AS Nothing
                FROM MOVIM_EMPRESTIMO M
                WHERE NOT EXISTS(
                    SELECT TOP 1 1
                    FROM SE_HIST_MOVIMENTO_ESTOQUE H
                    WHERE H.DOCUMENTO = M.DOCUMENTO
                    AND H.PRODUTO = M.PRODUTO
                    AND H.LOTE = M.LOTE
                    AND H.VALIDADE = M.VALIDADELOTE
                    AND H.DATA_MOVIMENTO = M.DATADIGITACAO
                    AND H.SEQUENCIA = M.SEQUENCIA
                    AND H.EVENTO LIKE '%EMPRESTIMO%'
                )
                AND M.DATADIGITACAO >= @ReferenceDate";
             var result = _session.Connection.Query<StockMovement>(sql, new { @ReferenceDate = referenceDate }, _session.Transaction);
            return result.ToList();
        }
        public List<StockMovement> GetCostCenterMovtsPending(DateTime referenceDate)
        {
            Console.WriteLine("Computando os Consumos de Setor...");
            var sql = @"SELECT DISTINCT 
                               M.DATADIGITACAO AS EventDate,
                               M.DOCUMENTO AS Document,
                               --NULL AS Series,
                               DATA AS IssueDate,
                               CASE WHEN M.TIPO = 0 THEN 'CONSUMO_SETOR' ELSE 'DEV_CONSUMO_SETOR' END AS EventType,
                               CASE WHEN M.TIPO = 0 THEN - 1 ELSE 1 END MovementType,
                                M.PRODUTO AS ProductId,
                               M.LOTE AS LotNumber,
                               M.VALIDADELOTE AS ValidityDate,
                               M.QUANTIDADE AS Quantity,
                               --0 AS UnityValue,
                               --0 AS TotalValue,
                               --NULL AS EntryID,
                               --0 AS StockBalance,
                               M.CCORIGEM AS StockId,
                               M.CCDESTINO AS ConsumeId,
                               M.SEQUENCIA AS OriginalSequence,
                               --NULL AS OutSourceId,
                               --NULL AS ProviderId,
                               --NULL AS PurchaseOrder,
                               --NULL AS Pledge,
                               --NULL AS Donation,
                               NULL AS Nothing
                        FROM MOVIMENTOCC M
                        WHERE 1=1 --ISNULL(M.CONSUMOINTERNO,0) = 1
                        AND NOT EXISTS(
                           SELECT TOP 1 1
                           FROM SE_HIST_MOVIMENTO_ESTOQUE H
                           WHERE H.DOCUMENTO = M.DOCUMENTO
                           AND H.ESTOQUECC = M.CCORIGEM
                           AND H.CONSUMOCC = M.CCDESTINO
                           AND H.SEQUENCIA = M.SEQUENCIA
                           AND H.PRODUTO = M.PRODUTO
                           AND H.LOTE = M.LOTE
                           AND H.VALIDADE = M.VALIDADELOTE
                           AND H.MOVIMENTO = (CASE WHEN M.TIPO = 0 THEN - 1 ELSE 1 END)
                           AND H.DATA_MOVIMENTO = M.DATADIGITACAO
                           AND H.EVENTO IN('CONSUMO_SETOR','DEV_CONSUMO_SETOR')    
                        )
                        AND M.DATADIGITACAO >= @ReferenceDate
            ";
            var result = _session.Connection.Query<StockMovement>(sql, new { @ReferenceDate = referenceDate }, _session.Transaction);
            return result.ToList();
        }
        public List<StockMovement> GetPatientMovtsPending()
        {
            return new List<StockMovement>();
        }
        public List<StockMovement> GetAdjustsPending(DateTime referenceDate)
        {
            Console.WriteLine("Computando os Ajustes de estoque...");
            var sql = @"SELECT DISTINCT 
                       (SELECT TOP 1 DATA FROM SAVELOG..CONTAGEMESTOQUE M2 WHERE M2.ATUALIZACAO = M.ATUALIZACAO ORDER BY DATA ASC) AS EventDate,
                       --NULL AS Document,
                       --NULL AS Series,
                       --NULL AS IssueDate,
                       'AJUSTE_ESTOQUE' AS EventType,
                       0 AS MovementType,
                       M.PRODUTO AS ProductId,
                       M.LOTE AS LotNumber,
                       M.VALIDADE AS ValidityDate,
                       M.SALDONOVO AS Quantity,
                       M.CUSTO AS UnityValue,
                       ROUND(M.CUSTO * M.SALDONOVO,2) AS TotalValue,
                       --NULL AS EntryId,
                       --0 AS StockBalance,
                       M.CENTROCUSTO AS StockId,
                       --NULL AS ConsumeId,
                       --NULL AS OriginalSequence,
                       --NULL AS OutSourceId,
                       --NULL AS ProviderId,
                       --NULL AS PurchaseOrder,
                       --NULL AS Pledge,
                       --NULL AS Donation,
                       M.SALDONOVO AS NewStockBalance,
                       NULL AS Nothing
                FROM SAVELOG..CONTAGEMESTOQUE M
                WHERE 1 = 1
                AND NOT EXISTS(
                   SELECT TOP 1 1 
                   FROM SE_HIST_MOVIMENTO_ESTOQUE H
                   WHERE H.ESTOQUECC = M.CENTROCUSTO       
                   AND H.PRODUTO = M.PRODUTO
                   AND H.LOTE = M.LOTE
                   AND H.VALIDADE = M.VALIDADE
                   --AND H.MOVIMENTO <> 0
                   AND H.DATA_MOVIMENTO = (SELECT TOP 1 DATA FROM SAVELOG..CONTAGEMESTOQUE M2 WHERE M2.ATUALIZACAO = M.ATUALIZACAO ORDER BY DATA ASC)
                   AND H.EVENTO IN ('AJUSTE_ESTOQUE')
                )
                AND ( M.LOTE <> 'LOTEAUX' OR M.SALDONOVO > 0)       
                AND M.DATA >= (SELECT MIN(DATA_MOVIMENTO) 
                                         FROM  SE_HIST_MOVIMENTO_ESTOQUE)
                AND M.DATA >= @ReferenceDate;";

            var result = _session.Connection.Query<StockMovement>(sql, new { @ReferenceDate = referenceDate }, _session.Transaction);
            return result.ToList();
        }

        public List<StockMovement> GetAllPending(StockProcessing process, DateTime referenceDate)
        {
            List<StockMovement> movims = new List<StockMovement>();
            movims.AddRange(GetEntriesPending(referenceDate));
            movims.AddRange(GetCostCenterMovtsPending(referenceDate));
            //movims.AddRange(GetPatientMovtsPending());
            movims.AddRange(GetLendsPending(referenceDate));
            movims.AddRange(GetAdjustsPending(referenceDate));

            foreach (var m in movims)
            {
                m.ProcessingId = process.Id;
            }

            return movims;
        }
        #endregion
    }
}
