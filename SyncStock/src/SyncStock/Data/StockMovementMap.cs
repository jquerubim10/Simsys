using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using Dapper.FluentMap.Dommel.Mapping;
using SyncStock.Domain;

namespace SyncStock.Data
{
    public class StockMovementMap : DommelEntityMap<StockMovement>
    {
        public StockMovementMap()
        {            
            ToTable("SE_HIST_MOVIMENTO_ESTOQUE");
            Map(x => x.Id).ToColumn("ID").IsIdentity().IsKey();
            Map(x => x.ProcessingId).ToColumn("ID_PROCESSAMENTO");
            Map(x => x.EventDate).ToColumn("DATA_MOVIMENTO");
            Map(x => x.EventType).ToColumn("EVENTO");
            Map(x => x.MovementType).ToColumn("MOVIMENTO");
            Map(x => x.Document).ToColumn("DOCUMENTO");
            Map(x => x.Series).ToColumn("SERIE");
            Map(x => x.IssueDate).ToColumn("DATA_EMISSAO");
            Map(x => x.ProductId).ToColumn("PRODUTO");
            Map(x => x.LotNumber).ToColumn("LOTE");
            Map(x => x.ValidityDate).ToColumn("VALIDADE");
            Map(x => x.Quantity).ToColumn("QUANTIDADE");
            Map(x => x.UnityValue).ToColumn("CUSTO_UNIT");
            Map(x => x.TotalValue).ToColumn("CUSTO_TOTAL");
            Map(x => x.StockBalance).ToColumn("SALDO");
            Map(x => x.EntryId).ToColumn("ID_ENTRADA");
            Map(x => x.StockId).ToColumn("ESTOQUECC");
            Map(x => x.ConsumeId).ToColumn("CONSUMOCC");
            Map(x => x.OriginalSequence).ToColumn("SEQUENCIA");
            Map(x => x.OutSourceId).ToColumn("TERCEIRO");
            Map(x => x.ProviderId).ToColumn("FORNECEDOR");
            Map(x => x.PurchaseOrder).ToColumn("PEDIDO");
            Map(x => x.Pledge).ToColumn("EMPENHO");
            Map(x => x.Donation).ToColumn("DOACAO");
            Map(x => x.FractionQuantity).ToColumn("QUANTIDADE_FRA");
            Map(x => x.FractionSourceId).ToColumn("ID_ORIGEM_FRA");
            Map(x => x.NewStockBalance).ToColumn("SALDO_ALTERADO");
        }
    }
}
