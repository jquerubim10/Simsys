using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dapper.FluentMap.Dommel.Mapping;
using SyncStock.Domain;

namespace SyncStock.Data
{
    public class DailyBalanceMap : DommelEntityMap<DailyBalance>
    {
        public DailyBalanceMap()
        {
            ToTable("SE_HIST_PRODUTO_SALDO");
            Map(x => x.Id).ToColumn("ID").IsIdentity().IsKey();
            Map(x => x.BalanceDate).ToColumn("DATA");
            Map(x => x.EntryId).ToColumn("ENTRADA_ID");
            Map(x => x.StockId).ToColumn("ESTOQUECC");
            Map(x => x.ProductId).ToColumn("PRODUTO");
            Map(x => x.LotNumber).ToColumn("LOTE");
            Map(x => x.ValidityDate).ToColumn("VALIDADE");
            Map(x => x.Balance).ToColumn("SALDO");
            Map(x => x.UnityValue).ToColumn("VALOR_UNITARIO");
            Map(x => x.TotalValue).ToColumn("VALOR_TOTAL");
        }
    }
}
