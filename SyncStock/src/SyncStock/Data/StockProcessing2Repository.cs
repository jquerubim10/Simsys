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
    public class StockProcessing2Repository : Repository<StockProcessing,long>
    {

        public StockProcessing2Repository(DbSession session) : base(session)
        {
        }
    }
}
