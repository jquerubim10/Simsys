using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dapper.FluentMap.Dommel.Mapping;
using SyncStock.Domain;
using Dapper.Extensions.Odbc;

namespace SyncStock.Data
{
    public class StockProcessingMap : DommelEntityMap<StockProcessing>
    {
        public StockProcessingMap()
        {
            ToTable("SE_HIST_PROCESSAMENTO_ESTOQUE");
            Map(x => x.Id).ToColumn("ID").IsIdentity().IsKey();
            Map(x => x.InitialTimestamp).ToColumn("DATA_INICIAL");
            Map(x => x.FinalTimestamp).ToColumn("DATA_FINAL");
            Map(x => x.Upstamp).ToColumn("ATUALIZACAO");
        }

    }
}
